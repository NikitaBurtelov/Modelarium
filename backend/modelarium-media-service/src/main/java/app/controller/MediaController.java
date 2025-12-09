package app.controller;

import app.model.entity.MediaEntity;
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

@Log4j2
@RestController
@RequestMapping("api/media")
@RequiredArgsConstructor
public class MediaController {
    private final MediaService mediaService;

    @PostMapping("/img")
    public Mono<ResponseEntity<List<MediaEntity>>> upload(@RequestPart("files") Flux<FilePart> files) {
        log.info("Multiple file upload request received");
        return files.flatMap(filePart ->
                        mediaService.upload(filePart)
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
                .map(ResponseEntity::ok);
    }

    @GetMapping("/img/multiple/with-mata")
    public Mono<ResponseEntity<Flux<DataBuffer>>> downloadMultipleWithMeta(@RequestParam List<String> objectName) {
        log.info("Request to download multiple files has been received.");

        Flux<DataBuffer> flux = mediaService.filesWithMeta(objectName);

        return Mono.just(
                ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_TYPE, "application/octet-stream")
                        .body(flux)
        );
    }
}
