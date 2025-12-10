package org.modelarium.post.service;

import lombok.RequiredArgsConstructor;
import org.modelarium.post.model.dto.response.MediaUploadResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MediaServiceImpl implements MediaService {
    private final WebClient webClient;

    @Override
    public Mono<ResponseEntity<MediaUploadResponse>> upload(Flux<FilePart> files) {
        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
        bodyBuilder.part("files", files);

        return webClient.post()
                .uri("/img")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData("files", files))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ResponseEntity<MediaUploadResponse>>() {
                });
    }

    //TODO подумать стоит ли делать проксирование
    @Override
    public Mono<ResponseEntity<Flux<DataBuffer>>> downloadMultipleByExternalId(UUID externalId) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/img/multiple/by-external-id")
                        .queryParam("externalId", externalId)
                        .build()
                )
                .accept(MediaType.APPLICATION_OCTET_STREAM)
                .retrieve()
                .toEntityFlux(DataBuffer.class);
    }
}