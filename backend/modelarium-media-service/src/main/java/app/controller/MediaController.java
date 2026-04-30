package app.controller;

import app.model.dto.MediaData;
import app.model.dto.MediaResponse;
import app.service.MediaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Log4j2
@RestController
@RequestMapping("api/media")
@RequiredArgsConstructor
public class MediaController {
    private final MediaService mediaService;

    @PostMapping("/img")
    public Mono<ResponseEntity<MediaResponse>> upload(
            @RequestPart("files") Flux<FilePart> files,
            @RequestPart("id") String externalId) {
        log.info("Multiple file upload request received");
        var externalIdUuid =  UUID.fromString(externalId);
        return files.flatMap(filePart ->
                        mediaService.upload(filePart, externalIdUuid)
                                .doOnSuccess(
                                        mediaUploadResult -> log.info(
                                                "File {} uploaded successfully",
                                                mediaUploadResult.mediaEntity().getObjectName()
                                        )
                                )
                                .doOnError(
                                        ex -> {
                                            log.error(
                                                    "Error while trying to load image. Key: {}. Error: {}",
                                                    filePart.filename(),
                                                    ex
                                            );
                                        })
                                .onErrorResume(ex -> Mono.empty())
                )
                .collectList()
                .map(mediaUploadResult ->
                        mediaUploadResult.stream()
                                .collect(Collectors.groupingBy(
                                        mediaUploadResultItem -> mediaUploadResultItem.mediaEntity().getExternalId(),
                                        Collectors.mapping(mediaUploadResultItem -> {
                                                    var mediaEntity = mediaUploadResultItem.mediaEntity();
                                                    return MediaData.builder()
                                                            .id(mediaEntity.getId())
                                                            .objectName(mediaEntity.getObjectName())
                                                            .mediaUrl(mediaUploadResultItem.mediaUrls().get(mediaEntity.getObjectName()))
                                                            .build();
                                                },
                                                Collectors.toList())
                                ))
                )
                .map(response -> ResponseEntity
                        .status(HttpStatus.OK)
                        .body(MediaResponse.builder()
                                .mediaData(response)
                                .build())
                );
    }

    @PostMapping("/img/urls/key")
    public Mono<ResponseEntity<MediaResponse>> getMediaUrlsByName(@RequestPart("keys") List<String> keys) {
        log.info("A request was received to obtain URLs of uploaded files by key.");

        return mediaService.getMediaUrlsByNama(keys)
                .map(response -> ResponseEntity
                        .status(HttpStatus.OK)
                        .body(response));
    }

    @PostMapping("/img/urls/id")
    public Mono<ResponseEntity<MediaResponse>> getMediaUrlsById(@RequestPart("id") List<UUID> externalId) {
        log.info("A request was received to obtain URLs of uploaded files by id.");

        return mediaService.getMediaUrlsById(externalId)
                .map(response -> ResponseEntity
                        .status(HttpStatus.OK)
                        .body(response));
    }

    @PostMapping("/img/source/multiple/name")
    public Mono<ResponseEntity<Flux<DataBuffer>>> downloadMultipleByName(@RequestPart List<String> objectName) {
        log.info("Request to download multiple files has been received.");

        Flux<DataBuffer> flux = mediaService.filesWithMeta(objectName);

        return Mono.just(
                ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_TYPE, "application/octet-stream")
                        .body(flux)
        );
    }

    @CrossOrigin(origins = "*") //TODO потом убрать
    @GetMapping("/img/source/multiple/by-external-id")
    public Mono<ResponseEntity<Flux<DataBuffer>>> downloadMultipleByExternalId(@RequestParam("externalId") UUID externalId) {
        log.info("Request to download multiple files has been received.");
        String boundary = "boundary-" + UUID.randomUUID();
        Flux<DataBuffer> multipartFlux = mediaService.filesWithMeta(externalId, boundary);

        return Mono.just(
                ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_TYPE, "multipart/x-mixed-replace; boundary=" + boundary)
                        .body(multipartFlux)
        );
    }

    @DeleteMapping("img/{postId}")
    public Mono<ResponseEntity> deletePost(@PathVariable UUID postId) {
        return mediaService.deleteObject(postId).then(Mono.just(ResponseEntity.ok().build()));
    }
}