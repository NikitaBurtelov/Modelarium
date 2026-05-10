package org.modelarium.post.model.dto.response;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record MediaGetResponse(
        Map<UUID, List<MediaData>> mediaData
) { }

