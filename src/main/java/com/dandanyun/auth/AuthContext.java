package com.dandanyun.auth;

import com.dandanyun.common.AuthenticationException;

public final class AuthContext {

    private static final ThreadLocal<AuthenticatedUser> CURRENT = new ThreadLocal<>();

    private AuthContext() {
    }

    public static void set(AuthenticatedUser user) {
        CURRENT.set(user);
    }

    public static AuthenticatedUser require() {
        AuthenticatedUser user = CURRENT.get();
        if (user == null) {
            throw new AuthenticationException("请先登录");
        }
        return user;
    }

    public static void clear() {
        CURRENT.remove();
    }
}
