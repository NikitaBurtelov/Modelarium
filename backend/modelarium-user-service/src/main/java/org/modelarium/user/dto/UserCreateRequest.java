package org.modelarium.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserCreateRequest(
        @NotBlank
        String userName,
        @Email
        @NotBlank
        String email,
        @NotBlank
        String passwordHash,
        @NotBlank
        String displayName,
        @NotBlank
        String avatarKey,
        @NotBlank
        String bio,
        boolean emailVerified
) implements DataObject {
}
