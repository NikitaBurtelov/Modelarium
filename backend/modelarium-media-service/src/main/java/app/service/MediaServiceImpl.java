package app.service;

import app.config.properties.MinIOProperties;
import app.model.entity.MediaEntity;
import app.repository.MediaReactiveRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Log4j2
@RequiredArgsConstructor
public class MediaServiceImpl implements MediaService {
    private final MediaReactiveRepository mediaReactiveRepository;
    private final StorageService storageService;
    private final MinIOProperties minIOProperties;

    private final TransactionalOperator transactionalOperator;

    @Override
    public Mono<MediaEntity> upload(FilePart filePart, UUID externalId) {
        UUID id = UUID.randomUUID();
        String objectName = id + "_" + filePart.filename();
        String contentType = Objects.requireNonNull(filePart.headers().getContentType()).toString();

        return DataBufferUtils.join(filePart.content())
                .map(DataBuffer::asInputStream)
                .flatMap(stream ->
                        Mono.fromCallable(() -> {
                                    storageService.putObject(
                                            PutObjectRequest.builder()
                                                    .bucket(minIOProperties.getImg().getBucketName())
                                                    .key(objectName)
                                                    .contentType(contentType)
                                                    .build(),
                                            RequestBody.fromInputStream(stream, stream.available())
                                    );
                                    log.info("File uploaded to storage. Key: {}", objectName);
                                    return stream;
                                })
                                .subscribeOn(Schedulers.boundedElastic()))
                .map(stream ->
                        MediaEntity.builder()
                                .id(id)
                                .externalId(externalId)
                                .objectName(objectName)
                                .contentType(contentType)
                                .size(filePart.headers().getContentLength())
                                .build())
                .flatMap(entity ->
                        mediaReactiveRepository.insert(entity)
                                .as(transactionalOperator::transactional)
                                .onErrorResume(e -> {
                                    return Mono.fromRunnable(() -> {
                                        try {
                                            storageService.deleteObject(
                                                    DeleteObjectRequest.builder()
                                                            .bucket(minIOProperties.getImg().getBucketName())
                                                            .key(objectName)
                                                            .build()
                                            );
                                            log.info(
                                                    "Error saving entity, deleting file from S3. Key: {}. Error: {}",
                                                    objectName,
                                                    e
                                            );
                                        } catch (Exception ex) {
                                            log.error(
                                                    "Error deleting file from storage. Key: {}. Error: {}",
                                                    objectName,
                                                    ex
                                            );
                                        }
                                    }).then(Mono.error(e));
                                })
                );
    }

    public Flux<DataBuffer> filesWithMeta(List<String> objectName) {
        return Flux.fromIterable(objectName)
                .concatMap(this::singleFileWithMeta);
    }

    public Flux<DataBuffer> filesWithMeta(UUID externalId, String boundary) {
        return multipleFilesWithMeta(externalId, boundary);
    }

    private Flux<DataBuffer> singleFileWithMeta(String objectName) {
        Mono<ResponseInputStream<GetObjectResponse>> s3ObjectMono =
                Mono.fromCallable(() ->
                                storageService.getObject(
                                        GetObjectRequest.builder()
                                                .bucket(minIOProperties.getImg().getBucketName())
                                                .key(objectName)
                                                .build()
                                )
                        )
                        .subscribeOn(Schedulers.boundedElastic())
                        .timeout(Duration.ofSeconds(10))
                        .doOnError(ex ->
                                log.error(
                                        "Error fetching file {} from S3: {}",
                                        objectName,
                                        ex.getMessage()
                                )
                        );
        return s3ObjectMono.flatMapMany(s3Object -> {
            long fileSize = s3Object.response().contentLength();
            String metaData = String.format("{\"name\":\"%s\",\"size\":%d}\n", objectName, fileSize);

            Flux<DataBuffer> fileFlux = DataBufferUtils.readInputStream(
                    () -> s3Object,
                    new DefaultDataBufferFactory(),
                    4096
            );

            return Flux.concat(
                            Flux.just(
                                    new DefaultDataBufferFactory().wrap(metaData.getBytes())),
                            fileFlux
                    )
                    .onErrorResume(ex -> {
                        log.error("Error streaming file {}: {}", objectName, ex.getMessage());
                        String errorMeta = String.format("{\"name\":\"%s\",\"error\":\"%s\"}\n", objectName, ex.getMessage());
                        return Flux.just(new DefaultDataBufferFactory().wrap(errorMeta.getBytes()));
                    });
        });
    }

    private Flux<DataBuffer> multipleFilesWithMeta(UUID externalId, String boundary) {
        DefaultDataBufferFactory factory = new DefaultDataBufferFactory();

        return mediaReactiveRepository.findAllByExternalId(externalId)
                .flatMap(mediaEntity -> {
                    String objectName = mediaEntity.getObjectName();

                    return Mono.fromCallable(() ->
                                    storageService.getObject(GetObjectRequest.builder()
                                            .bucket(minIOProperties.getImg().getBucketName())
                                            .key(objectName)
                                            .build())
                            )
                            .subscribeOn(Schedulers.boundedElastic())
                            .timeout(Duration.ofSeconds(10))
                            .flatMapMany(s3Stream -> {
                                long fileSize = s3Stream.response().contentLength();

                                String headers = "--" + boundary + "\r\n" +
                                        "Content-Disposition: form-data; name=\"file\"; filename=\"" + objectName + "\"\r\n" +
                                        "Content-Type: " + (s3Stream.response().contentType() != null ?
                                        s3Stream.response().contentType() : "application/octet-stream") + "\r\n\r\n";

                                DataBuffer headerBuffer = factory.wrap(headers.getBytes(StandardCharsets.UTF_8));
                                DataBuffer footerBuffer = factory.wrap("\r\n".getBytes(StandardCharsets.UTF_8));

                                Flux<DataBuffer> contentFlux = DataBufferUtils.readInputStream(
                                        () -> s3Stream,
                                        factory,
                                        4096
                                ).doFinally(signal -> {
                                    try {
                                        s3Stream.close();
                                    } catch (IOException e) {
                                        log.error("Failed to close S3 stream: {}", e.getMessage());
                                    }
                                });

                                return Flux.concat(Mono.just(headerBuffer), contentFlux, Mono.just(footerBuffer))
                                        .onErrorResume(ex -> {
                                            log.error("Error streaming file {}: {}", objectName, ex.getMessage());
                                            String errorPart = "--" + boundary + "\r\n" +
                                                    "Content-Disposition: form-data; name=\"file\"; filename=\"" + objectName + "\"\r\n" +
                                                    "Content-Type: text/plain\r\n\r\n" +
                                                    "ERROR: " + ex.getMessage() + "\r\n";
                                            return Mono.just(factory.wrap(errorPart.getBytes(StandardCharsets.UTF_8)));
                                        });
                            });
                })
                .concatWith(Mono.defer(() -> {
                    String closing = "--" + boundary + "--\r\n";
                    return Mono.just(factory.wrap(closing.getBytes(StandardCharsets.UTF_8)));
                }));
    }
}