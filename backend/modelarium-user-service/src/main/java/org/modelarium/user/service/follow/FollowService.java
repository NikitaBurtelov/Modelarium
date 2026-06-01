package org.modelarium.user.service.follow;

import java.util.UUID;

public interface FollowService {
    void follow(UUID userId, UUID subscriberId);

    void unfollow(UUID userId, UUID subscriberId);
}
