package org.modelarium.user.exceptions;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

public class UserNotFoundException extends RuntimeException {
    @Autowired
    public UserNotFoundException(UUID id) {
        super("User not found, userId: " + id);
    }

    public UserNotFoundException() {
        super("User not found");
    }
}
