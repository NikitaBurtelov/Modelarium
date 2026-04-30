package app.service;

import app.config.properties.MinIOProperties;
import app.model.dto.MediaData;
import app.model.dto.MediaResponse;
import app.model.dto.MediaUploadResult;
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
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class MediaServiceImpl implements MediaService {
    private final MediaReactiveRepository mediaReactiveRepository;
    private final StorageService storageService;
    private final MinIOProperties minIOProperties;

    private final TransactionalOperator transactionalOperator;

    @Override
    public Mono<MediaUploadResult> upload(FilePart filePart, UUID externalId) {
        var id = UUID.randomUUID();
        var key = id + "_" + filePart.filename();
        var contentType = Objects.requireNonNull(filePart.headers().getContentType()).toString();

        return DataBufferUtils.join(filePart.content())
                .map(DataBuffer::asInputStream)
                .flatMap(stream ->
                        Mono.fromCallable(() -> {
                                    storageService.putObject(
                                            PutObjectRequest.builder()
                                                    .bucket(minIOProperties.getImg().getBucketName())
                                                    .key(key)
                                                    .contentType(contentType)
                                                    .build(),
                                            RequestBody.fromInputStream(stream, stream.available())
                                    );
                                    log.info("File uploaded to storage. Key: {}", key);

                                    var mediaUrls = storageService.getMediaUrls(minIOProperties.getImg().getBucketName(), List.of(key));
                                    var mediaEntity = MediaEntity.builder()
                                            .id(id)
                                            .externalId(externalId)
                                            .objectName(key)
                                            .contentType(contentType)
                                            .build();

                                    return MediaUploadResult.builder()
                                            .mediaEntity(mediaEntity)
                                            .mediaUrls(mediaUrls)
                                            .build();
                                })
                                .subscribeOn(Schedulers.boundedElastic()))
                .flatMap(mediaUploadResult ->
                        mediaReactiveRepository.insert(mediaUploadResult.mediaEntity())
                                .as(transactionalOperator::transactional)
                                .map(saved -> Map.entry(saved, mediaUploadResult.mediaUrls()))
                )
                .map(entry -> {
                    MediaEntity entity = entry.getKey();
                    Map<String, String> urls = entry.getValue();

                    return MediaUploadResult.builder()
                            .mediaEntity(entity)
                            .mediaUrls(urls)
                            .build();
                })
                .onErrorResume(e -> {
                    return Mono.fromRunnable(() -> {
                        try {
                            storageService.deleteObject(
                                    DeleteObjectRequest.builder()
                                            .bucket(minIOProperties.getImg().getBucketName())
                                            .key(key)
                                            .build()
                            );
                            log.info(
                                    "Error saving entity, deleting file from S3. Key: {}. Error: {}",
                                    key,
                                    e
                            );
                        } catch (Exception ex) {
                            log.error(
                                    "Error deleting file from storage. Key: {}. Error: {}",
                                    key,
                                    ex
                            );
                        }
                    }).then(Mono.error(e));
                });
    }

    @Override
    public Mono<Void> deleteObject(UUID externalId) {
        return mediaReactiveRepository.findAllByExternalId(externalId)
                .flatMap(mediaEntity ->
                        Mono.fromRunnable(() -> {
                                    storageService.deleteObject(
                                            DeleteObjectRequest.builder()
                                                    .bucket(minIOProperties.getImg().getBucketName())
                                                    .key(mediaEntity.getObjectName())
                                                    .build()
                                    );
                                })
                                .subscribeOn(Schedulers.boundedElastic())
                )
                .then();
    }

    public Flux<DataBuffer> filesWithMeta(List<String> objectName) {
        return Flux.fromIterable(objectName)
                .concatMap(this::singleFileWithMeta);
    }

    public Flux<DataBuffer> filesWithMeta(UUID externalId, String boundary) {
        return multipleFilesWithMeta(externalId, boundary);
    }

    @Override
    public Mono<MediaResponse> getMediaUrlsByNama(List<String> keys) {
        return mediaReactiveRepository
                .findAllByObjectNameIn(keys)
                .collectList()
                .flatMap(entities -> Mono.fromCallable(
                                () -> storageService.getMediaUrls(
                                        minIOProperties.getImg().getBucketName(),
                                        keys
                                )
                        ).subscribeOn(Schedulers.boundedElastic())
                        .map(mediaUrls -> toMediaGetResponse(entities, mediaUrls)));
    }

    @Override
    public Mono<MediaResponse> getMediaUrlsById(List<UUID> ids) {
        return mediaReactiveRepository.findAllByExternalIdIn(ids)
                .collectList()
                .flatMap(entities -> {

                    List<String> keys = entities.stream()
                            .map(MediaEntity::getObjectName)
                            .toList();

                    return Mono.fromCallable(() ->
                                    storageService.getMediaUrls(
                                            minIOProperties.getImg().getBucketName(),
                                            keys
                                    )
                            )
                            .subscribeOn(Schedulers.boundedElastic())
                            .map(mediaUrls -> toMediaGetResponse(entities, mediaUrls));
                });
    }

    private Flux<DataBuffer> singleFileWithMeta(String key) {
        Mono<ResponseInputStream<GetObjectResponse>> s3ObjectMono =
                Mono.fromCallable(() ->
                                storageService.getObject(
                                        GetObjectRequest.builder()
                                                .bucket(minIOProperties.getImg().getBucketName())
                                                .key(key)
                                                .build()
                                )
                        )
                        .subscribeOn(Schedulers.boundedElastic())
                        .timeout(Duration.ofSeconds(10))
                        .doOnError(ex ->
                                log.error(
                                        "Error fetching file {} from S3: {}",
                                        key,
                                        ex.getMessage()
                                )
                        );
        return s3ObjectMono.flatMapMany(s3Object -> {
            long fileSize = s3Object.response().contentLength();
            String metaData = String.format("{\"name\":\"%s\",\"size\":%d}\n", key, fileSize);

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
                        log.error("Error streaming file {}: {}", key, ex.getMessage());
                        String errorMeta = String.format("{\"name\":\"%s\",\"error\":\"%s\"}\n", key, ex.getMessage());
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

    private MediaResponse toMediaGetResponse(List<MediaEntity> mediaEntities, Map<String, String> mediaUrls) {
        Map<UUID, List<MediaData>> mediaData = mediaEntities.stream()
                .collect(
                        Collectors.groupingBy(
                                MediaEntity::getExternalId,
                                Collectors.mapping(
                                        entity -> MediaData.builder()
                                                .id(entity.getId())
                                                .objectName(entity.getObjectName())
                                                .mediaUrl(mediaUrls.get(entity.getObjectName()))
                                                .build(),
                                        Collectors.toList()
                                )

                        ));

        return MediaResponse.builder()
                .mediaData(mediaData)
                .build();
    }
}