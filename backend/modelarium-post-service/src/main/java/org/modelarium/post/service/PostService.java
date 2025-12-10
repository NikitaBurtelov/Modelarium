package org.modelarium.post.service;

import org.modelarium.post.model.dto.request.PostCreateRequest;
import org.modelarium.post.model.dto.request.PostUpdateRequest;
import org.modelarium.post.model.dto.response.MediaUploadResponse;
import org.modelarium.post.model.dto.response.PostDataResponse;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface PostService {
    Mono<PostDataResponse> createPost(PostCreateRequest request, UUID authorId, Mono<ResponseEntity<MediaUploadResponse>> mediaUploadResponse);

    Mono<PostDataResponse> getPost(UUID postId);

    Flux<PostDataResponse> getPostsByUser(String userId);

    Mono<PostDataResponse> updatePost(Long postId, PostUpdateRequest request, String authorId);

    Mono<Void> deletePost(Long postId, String authorId);
}

