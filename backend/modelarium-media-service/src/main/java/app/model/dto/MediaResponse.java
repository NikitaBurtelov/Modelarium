package app.model.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record MediaResponse(
    List<MediaData> mediaData
) { }
