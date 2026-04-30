package app.model.dto;

import app.model.entity.MediaEntity;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Builder
public record MediaUploadResult(
        Map<String, String> mediaUrls,
        MediaEntity mediaEntity
) {
}
