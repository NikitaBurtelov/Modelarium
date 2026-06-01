package org.modelarium.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.modelarium.user.dto.DataObject;

public record UserCreateRequest(
        @NotBlank(message = "Username cannot be blank")
        @Size(min = 3, max = 32)
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
