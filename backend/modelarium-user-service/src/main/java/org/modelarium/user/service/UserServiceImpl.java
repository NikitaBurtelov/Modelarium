package org.modelarium.user.service;

import lombok.RequiredArgsConstructor;
import org.modelarium.user.config.properties.UserServiceProperties;
import org.modelarium.user.dto.FeedEntity;
import org.modelarium.user.dto.MediaData;
import org.modelarium.user.dto.UserData;
import org.modelarium.user.dto.DataObject;
import org.modelarium.user.dto.request.*;
import org.modelarium.user.dto.response.MediaUploadResponse;
import org.modelarium.user.dto.response.TopUserGetResponse;
import org.modelarium.user.dto.response.UserGetResponse;
import org.modelarium.user.exceptions.UserNotFoundException;
import org.modelarium.user.model.UserEntity;
import org.modelarium.user.repository.UserRepository;
import org.modelarium.user.service.cache.CacheService;
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
    private final CacheService<FeedEntity> cacheService;
    private final ValidationPipeline<DataObject> pipeline;
    private final UserServiceProperties userServiceProperties;

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

    @Override
    public TopUserGetResponse getTopUsersByCursor(TopUserGetRequest request) {
        var id = request.eventId();
        var size = request.size();
        var sequenceId = request.sequenceId();
        var key = key(id);

        if (!cacheService.exists(key)) {
            saveTopUsersInCache(id);
        }

        if (!cacheService.availableRange(key, sequenceId + size)) {
            var values = cacheService.getValues(
                            key,
                            sequenceId,
                            sequenceId + size
                    );

            updateTopUsersInCache(
                    key,
                    values.stream()
                            .map(FeedEntity::getId)
                            .toList(),
                    values.getLast().getSequenceId()
            );
        }

        var users = userRepository.findAllByIdIn(
                cacheService.getValues(
                                key,
                                sequenceId,
                                sequenceId + size
                        ).stream()
                        .map(FeedEntity::getId)
                        .toList()
        );

        return buildTopUserGetResponse(
                users,
                request.eventId(),
                sequenceId + size
        );
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

    private TopUserGetResponse buildTopUserGetResponse(List<UserEntity> users, UUID eventId, int sequenceId) {
        var mediaResponse = mediaService.getMediaUrlsByKeys(getAvatarKeys(users));

        return mapTopUserGetResponse(mediaResponse.mediaData(), users, eventId, sequenceId);
    }

    private List<String> getAvatarKeys(List<UserEntity> users) {
        return users.stream()
                .map(UserEntity::getAvatarKey)
                .toList();
    }

    private UserGetResponse mapToUserGetResponse(
            Map<UUID, List<MediaData>> mediaByUserId,
            List<UserEntity> users
    ) {
        return UserGetResponse.builder()
                        .userData(users.stream()
                                .map(user -> mapToUserData(user, mediaByUserId))
                                .toList()
                        ).build();
    }

    private TopUserGetResponse mapTopUserGetResponse(
            Map<UUID, List<MediaData>> mediaByUserId,
            List<UserEntity> users,
            UUID eventId,
            int sequenceId
    ) {
        return TopUserGetResponse.builder()
                .eventId(eventId)
                .sequenceId(sequenceId)
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

    private void saveTopUsersInCache(UUID id) {
        var maxTopUserIterationSize = userServiceProperties.getMaxTopUserIterationSize();
        var userIds = userRepository.findLatestTopUsers(maxTopUserIterationSize)
                .stream()
                .map(result -> FeedEntity.builder()
                        .id(result.getId())
                        .build()
                )
                .toList();

        cacheService.setValues(
                key(id),
                userIds
        );
    }

    private void updateTopUsersInCache(String key, List<UUID> userIds, Long sequenceId) {
        var maxTopUserIterationSize = userServiceProperties.getMaxTopUserIterationSize();

        cacheService.setValues(
                key,
                userRepository.findRefreshedTopUsers(
                                userIds,
                                sequenceId,
                                maxTopUserIterationSize
                        )
                        .stream()
                        .map(result -> FeedEntity.builder()
                                .id(result.getId())
                                .build()
                        ).toList()
        );
    }

    private String key(UUID id) {
        return "feed:users:" + id;
    }
}