package org.modelarium.user.security.model;

import java.util.Set;
import java.util.UUID;

public record AuthUserData(
        UUID userId,
        String username,
        Set<String> roles
) {
}
