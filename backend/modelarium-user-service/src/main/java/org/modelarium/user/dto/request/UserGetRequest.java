package org.modelarium.user.dto.request;

import java.util.List;
import java.util.UUID;

public record UserGetRequest(
        List<UUID> userIds
) { }
