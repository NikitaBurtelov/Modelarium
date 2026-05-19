package org.modelarium.user.service;

import org.modelarium.user.dto.request.TopUserGetRequest;
import org.modelarium.user.dto.request.UserCreateRequest;
import org.modelarium.user.dto.request.UserGetRequest;
import org.modelarium.user.dto.response.TopUserGetResponse;
import org.modelarium.user.dto.response.UserGetResponse;
import org.modelarium.user.dto.request.UserUpdateRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface UserService {
    void update(UserUpdateRequest user);
    void create(UserCreateRequest user, MultipartFile file);
    void delete(UUID id);
    UserGetResponse users(UserGetRequest request);
    UserGetResponse user(UUID userId);
    TopUserGetResponse getTopUsersByCursor(TopUserGetRequest request);
}
