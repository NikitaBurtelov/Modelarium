package org.modelarium.post.model.dto.request;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record PostCreateRequest(
        String description,
        List<String> tags,
        List<String> mentions,
        List<MultipartFile> files
) {
}

