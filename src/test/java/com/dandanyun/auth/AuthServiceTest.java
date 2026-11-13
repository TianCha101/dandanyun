package com.dandanyun.auth;

import com.dandanyun.common.AuthenticationException;
import com.dandanyun.config.SecurityProperties;
import com.dandanyun.system.SystemUser;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AuthServiceTest {

    @Test
    void logsInMatchingRole() {
        SecurityProperties properties = securityProperties();
        PasswordService passwordService = new PasswordService(properties);
        SystemUser user = user(passwordService.hash("secret-value"));
        AuthService service = new AuthService(
                username -> "admin".equals(username) ? user : null,
                passwordService,
                new JwtTokenService(properties, new ObjectMapper())
        );

        LoginResponse response = service.login(new LoginRequest("admin", "secret-value"), UserRole.ADMIN);

        assertEquals("Bearer", response.tokenType());
        assertEquals(UserRole.ADMIN, response.user().role());
    }

    @Test
    void rejectsWrongLoginEntry() {
        SecurityProperties properties = securityProperties();
        PasswordService passwordService = new PasswordService(properties);
        SystemUser user = user(passwordService.hash("secret-value"));
        AuthService service = new AuthService(
                username -> user,
                passwordService,
                new JwtTokenService(properties, new ObjectMapper())
        );

        assertThrows(
                AuthenticationException.class,
                () -> service.login(new LoginRequest("admin", "secret-value"), UserRole.USER)
        );
    }

    private SecurityProperties securityProperties() {
        SecurityProperties properties = new SecurityProperties();
        properties.getJwt().setIssuer("dandanyun-test");
        properties.getJwt().setSecret("test-secret-must-be-at-least-32-bytes-long");
        properties.getJwt().setExpirationSeconds(3600);
        properties.getPassword().setIterations(10_000);
        return properties;
    }

    private SystemUser user(String passwordHash) {
        SystemUser user = new SystemUser();
        user.setId(1L);
        user.setUsername("admin");
        user.setDisplayName("系统管理员");
        user.setPasswordHash(passwordHash);
        user.setRole(UserRole.ADMIN);
        user.setStatus(1);
        return user;
    }
}
