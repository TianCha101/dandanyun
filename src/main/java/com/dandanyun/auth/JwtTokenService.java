package com.dandanyun.auth;

import com.dandanyun.common.AuthenticationException;
import com.dandanyun.config.SecurityProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.time.Clock;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class JwtTokenService {

    private static final Base64.Encoder ENCODER = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder DECODER = Base64.getUrlDecoder();
    private static final String HEADER_JSON = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";

    private final ObjectMapper objectMapper;
    private final byte[] secret;
    private final String issuer;
    private final long expirationSeconds;
    private final Clock clock;

    @Autowired
    public JwtTokenService(SecurityProperties properties, ObjectMapper objectMapper) {
        this(properties, objectMapper, Clock.systemUTC());
    }

    JwtTokenService(SecurityProperties properties, ObjectMapper objectMapper, Clock clock) {
        this.objectMapper = objectMapper;
        this.secret = properties.getJwt().getSecret().getBytes(StandardCharsets.UTF_8);
        this.issuer = properties.getJwt().getIssuer();
        this.expirationSeconds = properties.getJwt().getExpirationSeconds();
        this.clock = clock;
        if (secret.length < 32) {
            throw new IllegalArgumentException("JWT secret 至少需要 32 字节");
        }
        if (expirationSeconds <= 0) {
            throw new IllegalArgumentException("JWT 有效期必须大于 0");
        }
    }

    public IssuedToken issue(AuthenticatedUser user) {
        Instant issuedAt = clock.instant();
        Instant expiresAt = issuedAt.plusSeconds(expirationSeconds);
        Map<String, Object> claims = new LinkedHashMap<>();
        claims.put("iss", issuer);
        claims.put("sub", Long.toString(user.id()));
        claims.put("username", user.username());
        claims.put("name", user.displayName());
        claims.put("role", user.role().name());
        claims.put("iat", issuedAt.getEpochSecond());
        claims.put("exp", expiresAt.getEpochSecond());

        try {
            String header = ENCODER.encodeToString(HEADER_JSON.getBytes(StandardCharsets.UTF_8));
            String payload = ENCODER.encodeToString(objectMapper.writeValueAsBytes(claims));
            String signingInput = header + "." + payload;
            String signature = ENCODER.encodeToString(sign(signingInput));
            return new IssuedToken(signingInput + "." + signature, expiresAt, expirationSeconds);
        } catch (Exception e) {
            throw new IllegalStateException("无法签发 token", e);
        }
    }

    public ParsedToken parse(String token) {
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            throw new AuthenticationException("token 格式不正确");
        }
        try {
            String signingInput = parts[0] + "." + parts[1];
            byte[] expected = sign(signingInput);
            byte[] actual = DECODER.decode(parts[2]);
            if (!MessageDigest.isEqual(expected, actual)) {
                throw new AuthenticationException("token 签名无效");
            }

            JsonNode header = objectMapper.readTree(DECODER.decode(parts[0]));
            if (!"HS256".equals(header.path("alg").asString(""))) {
                throw new AuthenticationException("token 算法无效");
            }
            JsonNode payload = objectMapper.readTree(DECODER.decode(parts[1]));
            if (!issuer.equals(payload.path("iss").asString(""))) {
                throw new AuthenticationException("token 签发方无效");
            }
            if (payload.path("exp").asLong() <= clock.instant().getEpochSecond()) {
                throw new AuthenticationException("token 已过期");
            }

            return new ParsedToken(
                    Long.parseLong(payload.path("sub").asString("")),
                    payload.path("username").asString(""),
                    payload.path("name").asString(""),
                    UserRole.valueOf(payload.path("role").asString("")),
                    Instant.ofEpochSecond(payload.path("exp").asLong())
            );
        } catch (AuthenticationException e) {
            throw e;
        } catch (Exception e) {
            throw new AuthenticationException("token 无法解析");
        }
    }

    private byte[] sign(String input) throws GeneralSecurityException {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret, "HmacSHA256"));
        return mac.doFinal(input.getBytes(StandardCharsets.UTF_8));
    }

    public record IssuedToken(String value, Instant expiresAt, long expiresInSeconds) {
    }

    public record ParsedToken(
            long id,
            String username,
            String displayName,
            UserRole role,
            Instant expiresAt
    ) {
        public AuthenticatedUser toUser() {
            return new AuthenticatedUser(id, username, displayName, role);
        }
    }
}
