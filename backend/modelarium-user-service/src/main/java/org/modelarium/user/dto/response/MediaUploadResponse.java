package org.modelarium.user.dto.response;

import lombok.*;
import org.modelarium.user.dto.MediaData;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Setter
@Getter
@Builder
public class MediaUploadResponse {
    @NonNull
    private Map<UUID, List<MediaData>> mediaData;
}

