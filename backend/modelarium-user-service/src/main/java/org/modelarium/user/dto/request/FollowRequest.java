package org.modelarium.user.dto.request;

import java.util.UUID;

public record FollowRequest(
        UUID userId,
        UUID subscriberId
) {
}
