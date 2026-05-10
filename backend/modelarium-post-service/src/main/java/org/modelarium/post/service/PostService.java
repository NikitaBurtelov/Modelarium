package org.modelarium.post.service;

import org.modelarium.post.model.dto.request.PostCreateRequest;
import org.modelarium.post.model.dto.request.PostUpdateRequest;
import org.modelarium.post.model.dto.response.CursorPageResponse;
import org.modelarium.post.model.dto.response.FeedCursor;
import org.modelarium.post.model.dto.response.PostDataResponse;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface PostService {
    Mono<PostDataResponse> createPost(
            PostCreateRequest request,
            UUID authorId,
            UUID workId,
            Flux<FilePart> files
    );

    Mono<CursorPageResponse<PostDataResponse>> getPostsByCursor(FeedCursor cursor, int size);

    Mono<PostDataResponse> getPost(UUID postId);

    Flux<PostDataResponse> getPostsByUser(UUID userId);

    Mono<PostDataResponse> updatePost(Long postId, PostUpdateRequest request, String authorId);

    Mono<Void> deletePost(UUID postId);
}

