package org.modelarium.post.model.dto.response;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record PostDataResponse(
        UUID id,
        String authorId,
        String description,
        List<String> tags,
        List<String> mentions,
        List<UUID> media,
        Instant createdAt
) {
}

