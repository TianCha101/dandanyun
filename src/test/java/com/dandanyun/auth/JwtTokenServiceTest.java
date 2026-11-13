package com.dandanyun.auth;

import com.dandanyun.common.AuthenticationException;
import com.dandanyun.config.SecurityProperties;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JwtTokenServiceTest {

    @Test
    void issuesAndVerifiesToken() {
        JwtTokenService service = service();
        AuthenticatedUser user = new AuthenticatedUser(1, "admin", "系统管理员", UserRole.ADMIN);

        JwtTokenService.IssuedToken issued = service.issue(user);
        JwtTokenService.ParsedToken parsed = service.parse(issued.value());

        assertEquals(user.id(), parsed.id());
        assertEquals(user.username(), parsed.username());
        assertEquals(user.role(), parsed.role());
        assertEquals(3600, issued.expiresInSeconds());
    }

    @Test
    void rejectsTamperedToken() {
        JwtTokenService service = service();
        String token = service.issue(
                new AuthenticatedUser(1, "admin", "系统管理员", UserRole.ADMIN)
        ).value();
        String[] parts = token.split("\\.");
        parts[1] = (parts[1].startsWith("A") ? "B" : "A") + parts[1].substring(1);
        String tampered = String.join(".", parts);

        assertThrows(AuthenticationException.class, () -> service.parse(tampered));
    }

    private JwtTokenService service() {
        SecurityProperties properties = new SecurityProperties();
        properties.getJwt().setIssuer("dandanyun-test");
        properties.getJwt().setSecret("test-secret-must-be-at-least-32-bytes-long");
        properties.getJwt().setExpirationSeconds(3600);
        Clock clock = Clock.fixed(Instant.parse("2026-11-13T08:00:00Z"), ZoneOffset.UTC);
        return new JwtTokenService(properties, new ObjectMapper(), clock);
    }
}
