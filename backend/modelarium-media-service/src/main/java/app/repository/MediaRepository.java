package app.repository;

import app.model.entity.MediaEntity;
import reactor.core.publisher.Mono;

interface MediaRepository {
    public Mono<MediaEntity> insert(MediaEntity entity);
}
