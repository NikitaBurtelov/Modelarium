package org.modelarium.post.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelarium.post.model.dto.request.PostCreateRequest;
import org.modelarium.post.model.dto.response.CursorPageResponse;
import org.modelarium.post.model.dto.response.FeedCursor;
import org.modelarium.post.model.dto.response.PostDataResponse;
import org.modelarium.post.service.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

@Log4j2
@RestController
@RequestMapping("api/post")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @CrossOrigin(origins = "*") //TODO потом убрать
    @PostMapping("/create")
    public Mono<ResponseEntity<PostDataResponse>> createPost(
            @RequestHeader("author-id") UUID authorId,
            @RequestPart("request") PostCreateRequest request,
            @RequestPart("files") Flux<FilePart> files
    ) {
        UUID workId = UUID.randomUUID();

        return postService.createPost(
                request,
                authorId,
                workId,
                files
        ).map(ResponseEntity::ok);
    }

    @CrossOrigin(origins = "*") //TODO потом убрать
    @GetMapping
    public Mono<CursorPageResponse<PostDataResponse>> getPostsByCursor(
            @RequestParam(required = false)
            Long sequenceId,
            @RequestParam(required = false)
            Instant createdAt,
            @RequestParam(defaultValue = "20")
            int size
    ) {
        FeedCursor cursor = null;

        if (createdAt != null && sequenceId != null) {
            cursor = new FeedCursor(
                    createdAt,
                    sequenceId
            );
        }

        return postService.getPostsByCursor(cursor, size);
    }

    @GetMapping("/{postId}")
    public Mono<ResponseEntity<PostDataResponse>> getPostById(@PathVariable UUID postId) {
        return postService.getPost(postId).map(ResponseEntity::ok);
    }

    @GetMapping("user/{userId}")
    public Flux<ResponseEntity<PostDataResponse>> getPostsByUserId(@PathVariable UUID userId) {
        return postService.getPostsByUser(userId).map(ResponseEntity::ok);
    }

    @DeleteMapping("/{postId}")
    public Mono<ResponseEntity> deletePost(@PathVariable UUID postId) {
        return postService.deletePost(postId)
                .then(Mono.just(ResponseEntity.ok().build()));
    }
}
