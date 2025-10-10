package com.example.jwe.test;

import com.example.mcp.jwe.JweUtils;
import com.nimbusds.jwt.JWTClaimsSet;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JweUtilsTest {

    private static JweUtils jweUtils;

    @BeforeAll
    static void setup() throws Exception {
        // Load keys from resources folder
        RSAPublicKey publicKey = JweUtils.loadPublicKeyFromResources("keys/public.pem");
        RSAPrivateKey privateKey = JweUtils.loadPrivateKeyFromResources("keys/private.pem");

        jweUtils = new JweUtils(publicKey, privateKey);
    }

    @Test
    void testGenerateAndDecryptToken() throws Exception {
        // Arrange
        String subject = "test-user";
        String email = "test-user@example.com";
        List<String> roles = List.of("user");

        // Act: generate token
        String token = jweUtils.generateEncryptedToken(subject, email, roles, 6000);
        System.out.println("Generated Token: " + token);
        assertNotNull(token, "Token should not be null");

        // Act: decrypt token
        JWTClaimsSet claims = jweUtils.decryptToken(token);

        // Assert: claims should match
        assertEquals(subject, claims.getSubject());
        assertEquals(email, claims.getStringClaim("email"));
        assertTrue(claims.getStringListClaim("roles").contains("user"));

        // Assert: expiry should exist
        assertNotNull(claims.getExpirationTime());
    }

    @Test
    void testExpiredToken() throws Exception {
        // Arrange: create a token that expires immediately
        String token = jweUtils.generateEncryptedToken("expired-user", "expired@example.com",
                List.of("user"), 0);

        // Act: decrypt
        JWTClaimsSet claims = jweUtils.decryptToken(token);

        // Assert: we can still decrypt, but the exp is in the past
        assertNotNull(claims.getExpirationTime());
        assertTrue(claims.getExpirationTime().before(new java.util.Date()),
                "Token should be expired");
    }
}

