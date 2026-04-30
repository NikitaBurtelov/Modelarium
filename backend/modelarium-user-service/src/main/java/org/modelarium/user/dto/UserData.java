package org.modelarium.user.dto;

import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record UserData(
        UUID id,
        String userName,
        String displayName,
        String bio,
        List<MediaData> mediaData
) {
}
