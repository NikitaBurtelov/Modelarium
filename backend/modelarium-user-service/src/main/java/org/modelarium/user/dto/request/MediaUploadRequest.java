package org.modelarium.user.dto.request;

import java.util.UUID;

public record MediaUploadRequest(
        String authorId,
        UUID externalId
) {
}
