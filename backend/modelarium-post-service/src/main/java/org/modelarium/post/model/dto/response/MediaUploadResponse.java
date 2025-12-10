package org.modelarium.post.model.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.modelarium.post.model.dto.util.MediaData;

import java.util.List;

@Setter
@Getter
@Builder
public class MediaUploadResponse {
    private List<MediaData> mediaData;
}