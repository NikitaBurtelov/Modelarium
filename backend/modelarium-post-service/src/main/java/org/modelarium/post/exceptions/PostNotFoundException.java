package org.modelarium.post.exceptions;

import java.util.UUID;

public class PostNotFoundException extends RuntimeException {
    public PostNotFoundException(UUID id) {
        super("Post not found, postId: " + id);
    }

    public PostNotFoundException() {
        super("Post not found");
    }
}
