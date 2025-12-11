package app.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Builder
public class MediaUploadResponse {
    private List<MediaData> mediaData;

}