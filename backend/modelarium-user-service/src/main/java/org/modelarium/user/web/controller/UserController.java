package org.modelarium.user.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelarium.user.dto.request.TopUserGetRequest;
import org.modelarium.user.dto.request.UserCreateRequest;
import org.modelarium.user.dto.request.UserGetRequest;
import org.modelarium.user.dto.response.TopUserGetResponse;
import org.modelarium.user.dto.response.UserCreateResponse;
import org.modelarium.user.dto.response.UserGetResponse;
import org.modelarium.user.service.follow.FollowService;
import org.modelarium.user.service.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Log4j2
@RestController
@RequestMapping("api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final FollowService followService;

    @PostMapping(
            value = "/create",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<UserCreateResponse> create(
            @Valid
            @RequestPart("request")
            UserCreateRequest request,
            @RequestPart("file")
            MultipartFile file
    ) {
        log.info("Creating user with username={}", request.userName());

        userService.create(request, file);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/top")
    public ResponseEntity<TopUserGetResponse> getUsersByCursor(
            @Valid
            @RequestParam(
                    value = "request",
                    required = false
            )
            TopUserGetRequest request
    ) {
        return ResponseEntity.ok()
                .body(userService.getTopUsersByCursor(request));
    }

    @GetMapping("/follow/{subId}")
    public ResponseEntity<?> follow(
            @RequestHeader("userId") UUID userId,
            @PathVariable UUID subId
    ) {
        followService.follow(userId, subId);

        log.info("User {} followed user {}", userId, subId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/unfollow/{subId}")
    public ResponseEntity<?> unfollow(
            @RequestHeader("userId") UUID userId,
            @PathVariable UUID subId
    ) {
        followService.unfollow(userId, subId);

        log.info("User {} unfollowed user {}", userId, subId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping(value = "/{userId}")
    public ResponseEntity<UserGetResponse> getUserById(@PathVariable UUID userId) {
        log.info("Fetching user by id={}", userId);

        var user = userService.user(userId);

        return ResponseEntity.ok()
                .body(user);
    }

    @PostMapping(value = "/")
    public ResponseEntity<UserGetResponse> getUsers(
            @Valid
            @RequestPart("request")
            UserGetRequest request
    ) {
        log.info("Fetching users");

        var users = userService.users(request);

        return ResponseEntity.ok()
                .body(users);
    }
}
