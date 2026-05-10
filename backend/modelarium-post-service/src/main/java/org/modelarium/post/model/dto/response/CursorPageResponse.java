package org.modelarium.post.model.dto.response;

import java.util.List;

public record CursorPageResponse<T>(
        List<T> items,
        FeedCursor nextCursor,
        boolean hasNext
) {
}
