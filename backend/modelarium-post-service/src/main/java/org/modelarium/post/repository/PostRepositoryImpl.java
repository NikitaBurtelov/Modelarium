package org.modelarium.post.repository;

import lombok.RequiredArgsConstructor;
import org.modelarium.post.model.entity.PostEntity;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
class PostRepositoryImpl implements PostRepository {
    private final R2dbcEntityTemplate template;

    @Override
    public Mono<PostEntity> insert(PostEntity entity) {
        return template.insert(PostEntity.class).using(entity);
    }
}
