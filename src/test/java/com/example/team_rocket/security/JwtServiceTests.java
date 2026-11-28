package com.example.team_rocket.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtServiceTests {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(
                "VGZ6c2h4QW5kTWVzc2FnaW5nQXBwU2VjcmV0S2V5Rm9yVGVhbVJvY2tldDIwMjY=");
        ReflectionTestUtils.setField(jwtService, "expirationMs", 3600000L);
    }

    @Test
    void generatedTokenContainsExpectedUsername() {
        String token = jwtService.generateToken("alice");

        assertEquals("alice", jwtService.extractUsername(token));
        assertTrue(jwtService.isTokenValid(token, "alice"));
    }

    @Test
    void tokenIsInvalidForDifferentUsername() {
        String token = jwtService.generateToken("alice");

        assertFalse(jwtService.isTokenValid(token, "bob"));
    }

    @Test
    void malformedTokenIsInvalid() {
        assertFalse(jwtService.isTokenValid("not-a-jwt", "alice"));
    }
}
