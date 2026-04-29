package app.service;

import app.model.dto.MediaResponse;
import app.model.dto.MediaUploadResult;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

public interface MediaService {
    Mono<MediaUploadResult> upload(FilePart filePart, UUID id);

    Mono<Void> deleteObject(UUID externalId);

    Flux<DataBuffer> filesWithMeta(List<String> objectName);

    Flux<DataBuffer> filesWithMeta(UUID externalId, String boundary);

    Mono<MediaResponse> getMediaUrlsByNama(List<String> keys);
    Mono<MediaResponse> getMediaUrlsById(List<UUID> id);
}
