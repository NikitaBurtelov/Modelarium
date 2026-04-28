package org.modelarium.user.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record UserUpdateRequest(
        UUID id,
        @NotBlank
        String userName,
        @NotBlank
        String displayName,
        @NotBlank
        String bio,
        boolean emailVerified
) implements DataObject {

}
