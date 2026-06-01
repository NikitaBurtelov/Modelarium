package org.modelarium.user.service.follow;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jspecify.annotations.NonNull;
import org.modelarium.user.model.FollowEntity;
import org.modelarium.user.repository.FollowRepository;
import org.modelarium.user.repository.UserRepository;
import org.modelarium.user.service.cache.CacheCounterService;
import org.modelarium.user.service.cache.CacheKeyPrefix;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Log4j2
public class FollowServiceImpl implements FollowService{
    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final CacheCounterService cacheCounterService;

    public static final String USERS_FOLLOW_KEY_PREFIX = CacheKeyPrefix.USERS_FOLLOW_KEY_PREFIX.getValue();

    @Override
    public void follow(UUID userId, UUID subscriberId) {
        if (followRepository.existsByUserIdAndSubscriberId(userId, subscriberId)) {
            return;
        }

        var followEntity = FollowEntity.builder()
                .userId(userId)
                .subscriberId(subscriberId)
                .build();

        followRepository.save(followEntity);

        cacheCounterService.increment(key(subscriberId));

        log.info("Follow request processed. userId={};subId={}",
                userId, subscriberId );
    }

    @Override
    public void unfollow(UUID userId, UUID subscriberId) {
        if (!followRepository.existsByUserIdAndSubscriberId(userId, subscriberId)) {
            return;
        }

        followRepository.deleteByUserIdAndSubscriberId(userId, subscriberId);

        cacheCounterService.decrement(key(subscriberId));

        log.info("Unfollow request processed. userId={};subId={}",
                userId, subscriberId );
    }

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void flushFollowerCounters() {
        Set<String> keysByPrefix = cacheCounterService.keysByPrefix(USERS_FOLLOW_KEY_PREFIX + "*");

        if (keysByPrefix.isEmpty()) return;

        keysByPrefix.forEach(keyByPrefix -> {
            var key = keyByPrefix.replace(USERS_FOLLOW_KEY_PREFIX, "");
            var followerCount = cacheCounterService.getValueAndDelete(key);

            userRepository.incrementFollow(UUID.fromString(key), followerCount);
        });

        log.info("Subscriber counts for users have been updated");
    }

    private @NonNull String key(UUID id) {
        return USERS_FOLLOW_KEY_PREFIX + id;
    }
}
