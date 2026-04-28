package org.modelarium.user.service;

import lombok.RequiredArgsConstructor;
import org.modelarium.user.dto.UserCreateRequest;
import org.modelarium.user.dto.UserUpdateRequest;
import org.modelarium.user.model.UserEntity;
import org.modelarium.user.repository.UserRepository;
import org.modelarium.user.service.validation.ValidationPipeline;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ValidationPipeline<UserCreateRequest> pipeline;

    @Override
    public boolean validation() {
        return false;
    }

    @Override
    @Transactional
    public UserEntity update(UserUpdateRequest request) {
        var id = request.id();
        var user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found" + id));

        user.setBio(request.bio());
        user.setEmailVerified(request.emailVerified());
        user.setDisplayName(request.displayName());
        user.setUserName(request.userName());

        return user;
    }

    @Override
    @Transactional
    public UserEntity create(UserCreateRequest request) {
        pipeline.validate(request);

        UserEntity user = UserEntity.builder()
                .userName(request.userName())
                .email(request.email())
                .passwordHash(request.passwordHash())
                .displayName(request.displayName())
                .avatarKey(request.avatarKey())
                .bio(request.bio())
                .emailVerified(request.emailVerified())
                .build();

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException(id.toString());
        }
        userRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserEntity> users(List<UUID> ids) {
        return userRepository.findAllById(ids);
    }

    @Override
    @Transactional(readOnly = true)
    public UserEntity user(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));
    }

    private DataBuffer userMediaData(UUID id) {
        return null;
    }
}
