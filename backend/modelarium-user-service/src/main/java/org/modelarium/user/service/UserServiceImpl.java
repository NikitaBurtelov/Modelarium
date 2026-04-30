package org.modelarium.user.service;

import lombok.RequiredArgsConstructor;
import org.modelarium.user.dto.*;
import org.modelarium.user.exceptions.UserNotFoundException;
import org.modelarium.user.model.UserEntity;
import org.modelarium.user.repository.UserRepository;
import org.modelarium.user.service.validation.ValidationPipeline;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final MediaService mediaService;
    private final ValidationPipeline<UserCreateRequest> pipeline;
    @Override
    @Transactional
    public void update(UserUpdateRequest request) {
        var id = request.id();
        var user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        user.setBio(request.bio());
        user.setEmailVerified(request.emailVerified());
        user.setDisplayName(request.displayName());
        user.setUserName(request.userName());
    }

    @Override
    @Transactional
    public void create(UserCreateRequest request, MultipartFile file) {
        pipeline.validate(request);

        var user = UserEntity.builder()
                .userName(request.userName())
                .email(request.email())
                .passwordHash(request.passwordHash())
                .displayName(request.displayName())
                .avatarKey(request.avatarKey())
                .bio(request.bio())
                .emailVerified(request.emailVerified())
                .build();
        var mediaUploadResponse = mediaService.uploadMedia(
                new MediaUploadRequest(
                        request.userName(),
                        user.getId()
                ),
                file
        );

        user.setAvatarKey(
                mediaUploadResponse
                        .getMediaData()
                        .get(user.getId())
                        .getFirst()
                        .getObjectKey()
        );

        userRepository.save(user);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        userRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public UserGetResponse users(UserGetRequest request) {
        var userIds = request.userIds();

        if (userIds.isEmpty()) {
            throw new UserNotFoundException();
        } else {
            var users = userRepository.findAllByIdIn(userIds);

            if (users.isEmpty()) {
                throw new UserNotFoundException();
            } else {
                var mediaResponse = mediaService.getMediaUrlsByKeys(
                        users.stream()
                                .map(UserEntity::getAvatarKey)
                                .toList()
                );

                return mapperUserGetResponse(mediaResponse, users);
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public UserGetResponse user(UUID id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        var mediaResponse = mediaService.getMediaUrlsByKeys(Collections.singletonList(user.getAvatarKey()));

        return mapperUserGetResponse(mediaResponse, List.of(user));
    }

    private UserGetResponse mapperUserGetResponse(MediaGetResponse mediaResponse, List<UserEntity> users) {
        return UserGetResponse.builder()
                .userData(
                        users.stream()
                                .map(
                                        user -> UserData.builder()
                                                .id(user.getId())
                                                .userName(user.getUserName())
                                                .bio(user.getBio())
                                                .displayName(user.getDisplayName())
                                                .mediaData(mediaResponse.mediaData().get(user.getId()))
                                                .build()
                                ).toList()
                ).build();
    }
}