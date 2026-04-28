package org.modelarium.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelarium.user.dto.UserCreateRequest;
import org.modelarium.user.dto.UserCreateResponse;
import org.modelarium.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequestMapping("api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/create")
    public ResponseEntity<UserCreateResponse> create(@RequestBody UserCreateRequest request) {
        userService.create(request);
        return ResponseEntity.ok().build();
    }
}
