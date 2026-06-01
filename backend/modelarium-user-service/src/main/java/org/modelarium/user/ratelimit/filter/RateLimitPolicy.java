package org.modelarium.user.ratelimit.filter;

import lombok.Getter;

import java.time.Duration;

@Getter
public enum RateLimitPolicy {

    FOLLOW("/api/user/follow", 30, Duration.ofMinutes(1)),
    UNFOLLOW("/api/user/unfollow", 30, Duration.ofMinutes(1)),
    CREATE_POST("/api/user/create", 10, Duration.ofMinutes(1)),
    TOP("/api/user/top", 10, Duration.ofMinutes(1)),
    DEFAULT("", 30, Duration.ofMinutes(1));

    private final String path;
    private final long capacity;
    private final Duration duration;

    RateLimitPolicy(
            String path,
            long capacity,
            Duration duration
    ) {
        this.path = path;
        this.capacity = capacity;
        this.duration = duration;
    }
}

