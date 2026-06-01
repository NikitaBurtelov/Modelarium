package org.modelarium.user.exceptions;

import java.util.UUID;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(UUID id) {
        super("User not found, userId: " + id);
    }

    public UserNotFoundException() {
        super("User not found");
    }
}
