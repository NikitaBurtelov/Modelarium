package app.model.dto;

import lombok.Builder;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Builder
public record MediaResponse(
        Map<UUID, List<MediaData>> mediaData
) { }
