package org.modelarium.user.security.provider;

import org.modelarium.user.security.model.AuthUserData;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;

@Component
public class AuthUserDataProvider {

    public UUID getAuthUserId() {
        return getAuthUserData().userId();
    }

    public String getAuthUsername() {
        return getAuthUserData().username();
    }

    public Set<String> getAuthRoles() {
        return getAuthUserData().roles();
    }

    private AuthUserData getAuthUserData() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null) {
            throw new IllegalStateException(
                    "No authentication found in SecurityContext"
            );
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof AuthUserData userData) {
            return userData;
        }

        throw new IllegalStateException(
                "Authentication principal is not AuthUserData"
        );
    }
}
