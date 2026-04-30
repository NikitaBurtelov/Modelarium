package org.modelarium.user.dto;

import java.util.List;
import java.util.UUID;

public record UserGetRequest(
        List<UUID> userIds
) { }
