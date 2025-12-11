package app.controller;

import app.model.dto.MediaData;
import app.model.dto.MediaUploadResponse;
import app.service.MediaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Log4j2
@RestController
@RequestMapping("api/media")
@RequiredArgsConstructor
public class MediaController {
    private final MediaService mediaService;

    @PostMapping("/img")
    public Mono<ResponseEntity<MediaUploadResponse>> upload(
            @RequestPart("files") Flux<FilePart> files,
            @RequestPart("id") String externalId) {
        log.info("Multiple file upload request received");
        return files.flatMap(filePart ->
                        mediaService.upload(filePart, UUID.fromString(externalId))
                                .doOnSuccess(
                                        mediaEntity -> log.info(
                                                "File {} uploaded successfully",
                                                mediaEntity.getObjectName()
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
                                //TODO подумать откатывать ли всю загрузку при ошибке ?
                                //Пока пусть будет возможность частичной загрузки
                                .onErrorResume(ex -> Mono.empty())
                )
                .collectList()
                .map(mediaEntities -> {
                    List<MediaData> mediaDataList = mediaEntities.stream()
                            .map(me -> {
                                MediaData md = new MediaData();
                                md.setId(me.getId());
                                md.setObjectName(me.getObjectName());
                                return md;
                            })
                            .toList();
                    MediaUploadResponse response = MediaUploadResponse.builder()
                            .mediaData(mediaDataList).build();

                    return ResponseEntity.ok(response);
                });
    }

    @GetMapping("/img/multiple/by-name")
    public Mono<ResponseEntity<Flux<DataBuffer>>> downloadMultipleByName(@RequestParam List<String> objectName) {
        log.info("Request to download multiple files has been received.");

        Flux<DataBuffer> flux = mediaService.filesWithMeta(objectName);

        return Mono.just(
                ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_TYPE, "application/octet-stream")
                        .body(flux)
        );
    }

    @CrossOrigin(origins = "*") //TODO потом убрать
    @GetMapping("/img/multiple/by-external-id")
    public Mono<ResponseEntity<Flux<DataBuffer>>> downloadMultipleByExternalId(@RequestParam("externalId") UUID externalId) {
        log.info("Request to download multiple files has been received.");
        String boundary = "boundary-" + UUID.randomUUID();
        Flux<DataBuffer> multipartFlux = mediaService.filesWithMeta(externalId, boundary);

        log.info("GO GO");

        return Mono.just(
                ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_TYPE, "multipart/x-mixed-replace; boundary=" + boundary)
                        .body(multipartFlux)
        );
    }
}