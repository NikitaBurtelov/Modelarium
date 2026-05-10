package org.modelarium.post.model.dto.response;

import java.time.Instant;

public record FeedCursor(
        Instant createdAt,
        Long sequenceId
) {
}
