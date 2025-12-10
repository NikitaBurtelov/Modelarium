package org.modelarium.post.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelarium.post.model.dto.request.PostCreateRequest;
import org.modelarium.post.model.dto.response.PostDataResponse;
import org.modelarium.post.service.MediaService;
import org.modelarium.post.service.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Log4j2
@RestController
@RequestMapping("api/post")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final MediaService mediaService;

    @PostMapping("/create")
    public Mono<ResponseEntity<PostDataResponse>> createPost(
            @RequestHeader("author-id") UUID authorId,
            @RequestPart("request") PostCreateRequest request,
            @RequestPart("files") Flux<FilePart> files
    ) {
        return postService.createPost(
                request,
                authorId,
                mediaService.upload(files)
        ).map(ResponseEntity::ok);
    }

    @GetMapping("/{postId}")
    public Mono<ResponseEntity<PostDataResponse>> getPost(@PathVariable UUID postId) {
        return postService.getPost(postId).map(ResponseEntity::ok);
    }

    @DeleteMapping("/{postId}")
    public Mono<ResponseEntity> deletePost(@PathVariable UUID postId) {
        //TODO
        return null;
    }
}
