package org.modelarium.user.service;

import lombok.RequiredArgsConstructor;
import org.modelarium.user.dto.MediaGetResponse;
import org.modelarium.user.dto.MediaUploadRequest;
import org.modelarium.user.dto.MediaUploadResponse;
import org.modelarium.user.exceptions.MediaUploadException;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MediaServiceImpl implements MediaService{
    private final WebClient mediaWebClient;

    @Override
    public MediaUploadResponse uploadMedia(
            MediaUploadRequest request,
            MultipartFile file
    ) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        body.add("id", request.externalId().toString());
        body.add("files", file.getResource());

        return mediaWebClient.post()
                .uri("/api/media/img")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .header("author-id", request.authorId())
                .body(BodyInserters.fromMultipartData(body))
                .retrieve()
                .onStatus(
                        HttpStatusCode::isError,
                        result -> result.bodyToMono(String.class)
                                .map(body1 -> new MediaUploadException(file.getOriginalFilename()))
                )
                .bodyToMono(MediaUploadResponse.class)
                .block();
    }

    @Override
    public MediaGetResponse getMediaUrlsByKeys(List<String> keys) {
        return mediaWebClient.post()
                .uri("/img/urls/name")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(keys)
                .retrieve()
                .onStatus(
                        HttpStatusCode::isError,
                        result -> result.bodyToMono(String.class)
                                .map(body -> new RuntimeException("Error " + body))
                )
                .bodyToMono(MediaGetResponse.class)
                .block();
    }

    @Override
    public MediaGetResponse getMediaUrlsByIds(List<UUID> ids) {
        return mediaWebClient.post()
                .uri("/img/urls/id")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(ids)
                .retrieve()
                .onStatus(
                        HttpStatusCode::isError,
                        result -> result.bodyToMono(String.class)
                                .map(body -> new RuntimeException("Error " + body))
                )
                .bodyToMono(MediaGetResponse.class)
                .block();
    }
}
