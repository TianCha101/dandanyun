package com.dandanyun.auth;

public record AuthenticatedUser(
        long id,
        String username,
        String displayName,
        UserRole role
) {
}
