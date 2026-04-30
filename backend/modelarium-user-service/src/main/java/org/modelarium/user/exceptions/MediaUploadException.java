package org.modelarium.user.exceptions;

public class MediaUploadException extends RuntimeException {
    public MediaUploadException(String message) {
        super("Error while downloading file" + message);
    }
}
