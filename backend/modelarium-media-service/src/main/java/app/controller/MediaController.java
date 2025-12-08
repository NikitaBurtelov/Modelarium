package app.controller;

import app.model.entity.MediaEntity;
import app.service.MediaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Log4j2
@RestController
@RequestMapping("api/media")
@RequiredArgsConstructor
public class MediaController {
    private final MediaService mediaService;

    @PostMapping("/img")
    public Mono<ResponseEntity<MediaEntity>> upload(@RequestPart("file") FilePart filePart) {
        log.info(
                "File upload request was received: {}",
                filePart.filename()
        );
        return mediaService.upload(filePart)
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
                .map(ResponseEntity::ok);
    }
}
