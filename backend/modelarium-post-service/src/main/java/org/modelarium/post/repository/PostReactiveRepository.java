package org.modelarium.post.repository;

import org.modelarium.post.model.entity.PostEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface PostReactiveRepository extends ReactiveCrudRepository<PostEntity, UUID>, PostRepository {
    Flux<PostEntity> findAllByAuthorId(UUID authorId);
}

