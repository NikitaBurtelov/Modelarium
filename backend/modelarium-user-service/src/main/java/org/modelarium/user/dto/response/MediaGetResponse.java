package org.modelarium.user.dto.response;

import org.modelarium.user.dto.MediaData;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record MediaGetResponse(
        Map<UUID, List<MediaData>> mediaData
) { }
