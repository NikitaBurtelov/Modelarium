package org.modelarium.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class FollowerCacheEntity extends CacheEntity{
    private UUID userId;
    private UUID subscriberId;
}
