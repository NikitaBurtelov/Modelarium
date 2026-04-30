package org.modelarium.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Setter
@Getter
@Builder
public class MediaUploadResponse {
    @NonNull
    private Map<UUID, List<MediaData>> mediaData;
}

