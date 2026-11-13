package com.dandanyun.auth;

import com.dandanyun.config.SecurityProperties;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordServiceTest {

    @Test
    void hashesAndVerifiesPassword() {
        SecurityProperties properties = new SecurityProperties();
        properties.getPassword().setIterations(120_000);
        PasswordService service = new PasswordService(properties);

        String encoded = service.hash("secret-value");

        assertTrue(service.matches("secret-value", encoded));
        assertFalse(service.matches("wrong-value", encoded));
    }

    @Test
    void verifiesSeededAdminPassword() {
        SecurityProperties properties = new SecurityProperties();
        properties.getPassword().setIterations(120_000);
        PasswordService service = new PasswordService(properties);
        String seededHash = "pbkdf2$120000$bNCyhBgsW5d/vmVndLMeeQ==$pxeuzpoO6lDTKZ+2z2ep/gFpKkLt9a5G114S8294wB8=";

        assertTrue(service.matches("Admin@123", seededHash));
    }
}
