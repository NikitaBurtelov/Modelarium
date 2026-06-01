package org.modelarium.user.service.cache;

import lombok.Getter;

@Getter
public enum CacheKeyPrefix {
    USERS_FOLLOW_KEY_PREFIX("users:follow:count");

    private final String value;

    CacheKeyPrefix(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
