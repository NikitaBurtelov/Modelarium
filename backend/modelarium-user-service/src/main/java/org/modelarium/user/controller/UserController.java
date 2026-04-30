package org.modelarium.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelarium.user.dto.UserCreateRequest;
import org.modelarium.user.dto.UserCreateResponse;
import org.modelarium.user.dto.UserGetRequest;
import org.modelarium.user.dto.UserGetResponse;
import org.modelarium.user.service.UserService;
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

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserCreateResponse> create(
            @RequestPart("request") UserCreateRequest request,
            @RequestPart("file") MultipartFile file) {
        log.info("");
        userService.create(request, file);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/{userId}")
    public ResponseEntity<UserGetResponse> getUserById(@PathVariable UUID userId) {
        log.info("");
        var user = userService.user(userId);
        return ResponseEntity.ok()
                .body(user);
    }

    @PostMapping(value = "/")
    public ResponseEntity<UserGetResponse> getUsers(@RequestPart("request") UserGetRequest request) {
        log.info("");
        var users = userService.users(request);
        return ResponseEntity.ok()
                .body(users);
    }
}
