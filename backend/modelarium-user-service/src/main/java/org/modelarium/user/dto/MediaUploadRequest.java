package org.modelarium.user.dto;

import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public record MediaUploadRequest(
        String authorId,
        UUID externalId
) {
}
