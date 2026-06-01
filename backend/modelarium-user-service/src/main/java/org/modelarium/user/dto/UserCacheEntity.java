package org.modelarium.user.dto;

import lombok.*;

import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserCacheEntity extends CacheEntity {
    private UUID id;
    private Long sequenceId;
}
