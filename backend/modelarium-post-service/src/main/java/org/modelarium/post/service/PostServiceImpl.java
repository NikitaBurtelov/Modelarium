package org.modelarium.post.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelarium.post.model.dto.request.PostCreateRequest;
import org.modelarium.post.model.dto.request.PostUpdateRequest;
import org.modelarium.post.model.dto.response.MediaUploadResponse;
import org.modelarium.post.model.dto.response.PostDataResponse;
import org.modelarium.post.model.dto.util.MediaData;
import org.modelarium.post.model.entity.PostEntity;
import org.modelarium.post.repository.PostReactiveRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

import java.time.Instant;
import java.util.UUID;

@Log4j2
@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostReactiveRepository postReactiveRepository;

    private final TransactionalOperator transactionalOperator;

    @Override
    public Mono<PostDataResponse> createPost(
            PostCreateRequest request,
            UUID authorId,
            UUID workId,
            Mono<ResponseEntity<MediaUploadResponse>> mediaUploadResponse) {
        return mediaUploadResponse
                .map(ResponseEntity::getBody)
                .flatMap(response -> {
                            PostEntity postEntity = PostEntity.builder()
                                    .id(workId)
                                    .authorId(authorId)
                                    .tags(request.tags())
                                    .description(request.description())
                                    .mentions(request.mentions())
                                    .mediaIds(response.getMediaData()
                                            .stream()
                                            .map(MediaData::getId)
                                            .toList())
                                    .createdAt(Instant.now())
                                    .updatedAt(Instant.now())
                                    .build();
                            //TODO добавить транзакцию
                            return postReactiveRepository
                                    .insert(postEntity)
                                    .as(transactionalOperator::transactional)
                                    .map(postData -> Tuples.of(postData, response.getMediaData())); //TODO потом убрать
                        }
                )
                .map(tuple -> {
                    return toResponse(tuple.getT1());
                });
    }

    @Override
    public Mono<PostDataResponse> getPost(UUID postId) {
        return postReactiveRepository.findById(postId).map(this::toResponse);
    }

    @Override
    public Flux<PostDataResponse> getPostsByUser(UUID userId) {
        return postReactiveRepository.findAllByAuthorId(userId).map(this::toResponse);
    }

    @Override
    public Mono<PostDataResponse> updatePost(Long postId, PostUpdateRequest request, String authorId) {
        return null;
    }

    @Override
    public Mono<Void> deletePost(Long postId, String authorId) {
        return null;
    }

    private PostDataResponse toResponse(PostEntity entity) {
        return new PostDataResponse(
                entity.getId(),
                entity.getAuthorId().toString(),
                entity.getDescription(),
                entity.getTags(),
                entity.getMentions(),
                entity.getMediaIds(),
                entity.getCreatedAt()
        );
    }

}
