package org.modelarium.post.service;

import org.modelarium.post.model.dto.response.MediaUploadResponse;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface MediaService {
    Mono<ResponseEntity<MediaUploadResponse>> upload(
            UUID externalId,
            UUID authorId,
            Flux<FilePart> files);

    Mono<ResponseEntity<Flux<DataBuffer>>> downloadMultipleByExternalId(UUID externalId);
}
