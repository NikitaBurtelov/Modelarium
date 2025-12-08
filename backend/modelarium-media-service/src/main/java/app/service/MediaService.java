package app.service;

import app.model.entity.MediaEntity;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

public interface MediaService {
    public Mono<MediaEntity> upload(FilePart filePart);
}
