package org.modelarium.post.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelarium.post.exceptions.PostNotFoundException;
import org.modelarium.post.model.dto.request.PostCreateRequest;
import org.modelarium.post.model.dto.request.PostUpdateRequest;
import org.modelarium.post.model.dto.response.CursorPageResponse;
import org.modelarium.post.model.dto.response.FeedCursor;
import org.modelarium.post.model.dto.response.MediaData;
import org.modelarium.post.model.dto.response.PostDataResponse;
import org.modelarium.post.model.entity.PostEntity;
import org.modelarium.post.repository.PostReactiveRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Log4j2
@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final MediaService mediaService;
    private final PostReactiveRepository postReactiveRepository;
    private final TransactionalOperator transactionalOperator;

    @Override
    public Mono<PostDataResponse> createPost(
            PostCreateRequest request,
            UUID authorId,
            UUID workId,
            Flux<FilePart> files
    ) {
        return mediaService.upload(workId, authorId, files)
                .map(ResponseEntity::getBody)
                .flatMap(response -> {
                            PostEntity postEntity = PostEntity.builder()
                                    .id(workId)
                                    .authorId(authorId)
                                    .tags(request.tags())
                                    .description(request.description())
                                    .mentions(request.mentions())
                                    .mediaIds(response.getMediaData()
                                            .get(workId)
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
                    return toResponse(tuple.getT1(), tuple.getT2());
                });
    }

    public Mono<CursorPageResponse<PostDataResponse>> getPostsByCursor(FeedCursor cursor, int size) {
        Flux<PostEntity> postEntityFlux;

        if (cursor == null) {
            postEntityFlux = postReactiveRepository.findLatest(size + 1);
        } else {
            postEntityFlux = postReactiveRepository.findNextPage(cursor.createdAt(), cursor.sequenceId(), size + 1);
        }

        return postEntityFlux
                .collectList()
                .flatMap(posts -> {
                    var mediaIds = posts.stream()
                            .flatMap(p -> Optional.ofNullable(p.getMediaIds())
                                    .orElse(List.of())
                                    .stream())
                            .toList();

                    var mediaGetResponse = mediaService.getMediaUrlsById(mediaIds)
                            .map(response -> {
                                assert response.getBody() != null;
                                return response.getBody().mediaData();
                            });

                    return mediaGetResponse.map(response -> {
                        var items = posts.stream()
                                .map(post -> {
                                    return new PostDataResponse(
                                            post.getId(),
                                            post.getAuthorId().toString(),
                                            post.getDescription(),
                                            post.getTags(),
                                            post.getMentions(),
                                            response.get(post.getId()),
                                            post.getCreatedAt()
                                    );
                                }).toList();

                        return new CursorPageResponse<>(
                                items,
                                posts.isEmpty()
                                        ? null
                                        : new FeedCursor(
                                        posts.getLast().getCreatedAt(),
                                        posts.getLast().getSequenceId()
                                ),
                                posts.size() > 20
                        );
                    });
                });
    }

    @Override
    public Mono<PostDataResponse> getPost(UUID postId) {
        return postReactiveRepository.findById(postId)
                .switchIfEmpty(Mono.error(new PostNotFoundException(postId)))
                .flatMap(this::enrich);
    }

    @Override
    public Flux<PostDataResponse> getPostsByUser(UUID userId) {
        return postReactiveRepository.findAllByAuthorId(userId)
                .flatMap(this::enrich);
    }

    @Override
    public Mono<PostDataResponse> updatePost(Long postId, PostUpdateRequest request, String authorId) {
        return null;
    }

    @Override
    public Mono<Void> deletePost(UUID postId) {
        postReactiveRepository.deleteById(postId);
        return null;
    }

    private Mono<PostDataResponse> enrich(PostEntity postEntity) {
        List<UUID> mediaIds = Optional.ofNullable(postEntity.getMediaIds())
                .orElse(List.of());

        return mediaService.getMediaUrlsById(mediaIds)
                .map(response -> {
                    var mediaMap = response.getBody().mediaData();
                    return toResponse(postEntity, mediaMap);
                });
    }

    private PostDataResponse toResponse(PostEntity entity, Map<UUID, List<MediaData>> mediaDataResponse) {
        return new PostDataResponse(
                entity.getId(),
                entity.getAuthorId().toString(),
                entity.getDescription(),
                entity.getTags(),
                entity.getMentions(),
                mediaDataResponse.get(entity.getId()),
                entity.getCreatedAt()
        );
    }

}
