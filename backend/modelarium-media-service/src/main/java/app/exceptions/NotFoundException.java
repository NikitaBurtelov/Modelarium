package app.exceptions;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

public class NotFoundException extends RuntimeException {
    @Autowired
    public NotFoundException(UUID id) {
        super("User not found, userId: " + id);
    }

    public NotFoundException() {
        super("User not found");
    }
}
