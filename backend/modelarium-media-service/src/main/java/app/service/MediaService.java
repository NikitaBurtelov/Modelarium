package app.service;

import app.model.entity.MediaEntity;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

public interface MediaService {
    Mono<MediaEntity> upload(FilePart filePart, UUID id);

    Flux<DataBuffer> filesWithMeta(List<String> objectName);

    Flux<DataBuffer> filesWithMeta(UUID externalId, String boundary);
}
