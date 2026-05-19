package org.modelarium.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import org.modelarium.user.dto.DataObject;

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
