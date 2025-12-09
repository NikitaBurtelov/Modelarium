package app.repository;


import app.model.entity.MediaEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
class MediaRepositoryImpl implements MediaRepository {
    private final R2dbcEntityTemplate template;

    @Override
    public Mono<MediaEntity> insert(MediaEntity entity) {
        return template.insert(MediaEntity.class).using(entity);
    }
}
