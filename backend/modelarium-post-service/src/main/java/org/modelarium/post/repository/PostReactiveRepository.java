package org.modelarium.post.repository;

import org.modelarium.post.model.entity.PostEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.time.Instant;
import java.util.UUID;

@Repository
public interface PostReactiveRepository extends ReactiveCrudRepository<PostEntity, UUID>, PostRepository {
    Flux<PostEntity> findAllByAuthorId(UUID authorId);

    @Query("""
            SELECT *
            FROM posts
            ORDER BY created_at DESC, sequence_id DESC
            LIMIT :limit
            """)
    Flux<PostEntity> findLatest(
            int limit
    );

    @Query("""
            SELECT *
            FROM posts
            WHERE
                created_at < :createdAt
                OR (
                    created_at = :createdAt
                    AND sequence_id < :sequenceId
                )
            ORDER BY created_at DESC, sequence_id DESC
            LIMIT :limit
            """)
    Flux<PostEntity> findNextPage(
            Instant createdAt,
            Long sequenceId,
            int limit
    );

    Flux<PostEntity> findAllByCreatedAtOrderByCreatedAtDesc(Instant createdAt);
            
    Flux<PostEntity> findByIdLessThanOrderByIdDesc(UUID idIsLessThan, Pageable pageable);

    Flux<PostEntity> findByCreatedAtLessThanOrderByCreatedAt(Instant createdAtIsLessThan, Pageable pageable);
}

