package org.modelarium.user.service;

import org.modelarium.user.dto.UserCreateRequest;
import org.modelarium.user.dto.UserGetRequest;
import org.modelarium.user.dto.UserGetResponse;
import org.modelarium.user.dto.UserUpdateRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface UserService {
    public void update(UserUpdateRequest user);
    public void create(UserCreateRequest user, MultipartFile file);
    public void delete(UUID id);
    public UserGetResponse users(UserGetRequest request);
    public UserGetResponse user(UUID userId);
}
