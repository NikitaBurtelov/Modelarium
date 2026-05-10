package org.modelarium.post.exceptions;

public class MediaUploadException extends RuntimeException {
    public MediaUploadException(String message) {
        super("Error while downloading file" + message);
    }
}
