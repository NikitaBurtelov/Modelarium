package org.modelarium.user.exceptions;

public class WeakPasswordException extends RuntimeException {
    public WeakPasswordException() {
        super("Weak Password");
    };
}
