package com.dandanyun.auth;

public record LoginResponse(
        String accessToken,
        String tokenType,
        long expiresInSeconds,
        LoginUser user
) {
    public record LoginUser(long id, String username, String displayName, UserRole role) {
    }
}
