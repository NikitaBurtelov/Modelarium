package org.modelarium.user.exceptions;

public class UserNameAlreadyExistsException extends RuntimeException {
    public UserNameAlreadyExistsException(String userName) {
        super("Username already exists: " + userName);
    }
}
