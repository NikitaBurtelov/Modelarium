package app.model.dto;

import lombok.Builder;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Setter
@Builder
public class MediaUploadResponse {
    private List<MediaData> mediaData;

    @Setter
    public static class MediaData {
        private UUID id;
        private String objectName;
    }
}