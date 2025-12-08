package app.service;

import app.config.properties.MinIOProperties;
import app.model.entity.MediaEntity;
import app.repository.MediaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.InputStream;
import java.util.Objects;
import java.util.UUID;

@Service
@Log4j2
@RequiredArgsConstructor
public class MediaServiceImpl implements MediaService {
    private final MediaRepository mediaRepository;
    private final S3Client s3Client;
    private final MinIOProperties minIOProperties;

    private final TransactionalOperator transactionalOperator;

    @Override
    public Mono<MediaEntity> upload(FilePart filePart) {
        UUID id = UUID.randomUUID();
        String objectName = id + "_" + filePart.filename();
        String contentType = Objects.requireNonNull(filePart.headers().getContentType()).toString();

        return Mono.fromCallable(() -> {
                    InputStream stream =
                            DataBufferUtils.join(filePart.content())
                                    .map(DataBuffer::asInputStream)
                                    .block();
                    s3Client.putObject(
                            PutObjectRequest.builder()
                                    .bucket(minIOProperties.getBucket())
                                    .key(objectName)
                                    .contentType(contentType)
                                    .build(),
                            RequestBody.fromInputStream(stream, stream.available())
                    );
                    log.info(
                            "File uploaded to storage. Key: {}",
                            objectName
                    );
                    return stream;
                })
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(stream -> {
                    MediaEntity entity = MediaEntity.builder()
                            .id(id)
                            .objectName(objectName)
                            .contentType(contentType)
                            .size(filePart.headers().getContentLength())
                            .build();
                    return mediaRepository.save(entity)
                            .as(transactionalOperator::transactional)
                            .onErrorResume(e -> {
                                return Mono.fromRunnable(() -> {
                                    try {
                                        s3Client.deleteObject(
                                                DeleteObjectRequest.builder()
                                                        .bucket(minIOProperties.getBucket())
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
                            });
                });
    }
}
