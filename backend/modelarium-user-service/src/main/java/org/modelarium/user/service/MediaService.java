package org.modelarium.user.service;

import org.modelarium.user.dto.MediaGetResponse;
import org.modelarium.user.dto.MediaUploadRequest;
import org.modelarium.user.dto.MediaUploadResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface MediaService {
    public MediaUploadResponse uploadMedia(MediaUploadRequest request, MultipartFile file);
    public MediaGetResponse getMediaUrlsByIds(List<UUID> ids);
    public MediaGetResponse getMediaUrlsByKeys(List<String> keys);
}
