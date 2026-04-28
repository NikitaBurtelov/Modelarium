package org.modelarium.user.service;

import org.modelarium.user.dto.UserCreateRequest;
import org.modelarium.user.dto.UserUpdateRequest;
import org.modelarium.user.model.UserEntity;

import java.util.List;
import java.util.UUID;

public interface UserService {
    public boolean validation();
    public UserEntity update(UserUpdateRequest user);
    public UserEntity create(UserCreateRequest user);
    public void delete(UUID id);
    public List<UserEntity> users(List<UUID> id);
    public UserEntity user(UUID user);
}
