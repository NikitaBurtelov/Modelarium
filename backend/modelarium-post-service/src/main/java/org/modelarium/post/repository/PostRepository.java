package org.modelarium.post.repository;


import org.modelarium.post.model.entity.PostEntity;
import reactor.core.publisher.Mono;

interface PostRepository {
    Mono<PostEntity> insert(PostEntity entity);
}
