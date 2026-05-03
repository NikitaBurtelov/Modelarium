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

import java.util.List;
import java.util.Map;
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
        var user = getRequiredUser(request.id());
        applyUpdates(user, request);
    }

    @Override
    @Transactional
    public void create(UserCreateRequest request, MultipartFile file) {
        pipeline.validate(request);

        var userId = UUID.randomUUID();
        var user = buildUser(request, userId);
        var mediaUploadResponse = uploadAvatar(request.userName(), userId, file);

        user.setAvatarKey(extractUploadedAvatarKey(mediaUploadResponse, userId));

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
        }

        var users = userRepository.findAllByIdIn(userIds);
        if (users.isEmpty()) {
            throw new UserNotFoundException();
        }

        return buildUserGetResponse(users);
    }

    @Override
    @Transactional(readOnly = true)
    public UserGetResponse user(UUID id) {
        var user = getRequiredUser(id);

        return buildUserGetResponse(List.of(user));
    }

    private UserEntity getRequiredUser(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    private void applyUpdates(UserEntity user, UserUpdateRequest request) {
        user.setBio(request.bio());
        user.setEmailVerified(request.emailVerified());
        user.setDisplayName(request.displayName());
        user.setUserName(request.userName());
    }

    private UserEntity buildUser(UserCreateRequest request, UUID id) {
        return UserEntity.builder()
                .id(id)
                .userName(request.userName())
                .email(request.email())
                .passwordHash(request.passwordHash())
                .displayName(request.displayName())
                .avatarKey(request.avatarKey())
                .bio(request.bio())
                .emailVerified(request.emailVerified())
                .build();
    }

    private MediaUploadResponse uploadAvatar(String userName, UUID userId, MultipartFile file) {
        return mediaService.uploadMedia(new MediaUploadRequest(userName, userId), file);
    }

    private String extractUploadedAvatarKey(MediaUploadResponse mediaUploadResponse, UUID userId) {
        return mediaUploadResponse
                .getMediaData()
                .get(userId)
                .getFirst()
                .getObjectKey();
    }

    private UserGetResponse buildUserGetResponse(List<UserEntity> users) {
        var mediaResponse = mediaService.getMediaUrlsByKeys(getAvatarKeys(users));

        return mapToUserGetResponse(mediaResponse.mediaData(), users);
    }

    private List<String> getAvatarKeys(List<UserEntity> users) {
        return users.stream()
                .map(UserEntity::getAvatarKey)
                .toList();
    }

    private UserGetResponse mapToUserGetResponse(Map<UUID, List<MediaData>> mediaByUserId, List<UserEntity> users) {
        return UserGetResponse.builder()
                        .userData(users.stream()
                                .map(user -> mapToUserData(user, mediaByUserId))
                                .toList()
                        ).build();
    }

    private UserData mapToUserData(UserEntity user, Map<UUID, List<MediaData>> mediaByUserId) {
        return UserData.builder()
                .id(user.getId())
                .userName(user.getUserName())
                .bio(user.getBio())
                .displayName(user.getDisplayName())
                .mediaData(mediaByUserId.get(user.getId()))
                .build();
    }
}