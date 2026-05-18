package org.example.service;

import org.example.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {
    private final JwtService jwtService = new JwtService();

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(
                jwtService, "secret", "dGhpcyBpcyBhIHNlY3JldCBrZXkgZm9yIHNpZ25pbmcgSldUIHRva2VucyBmb3IgdGhlIHRvZG8gYXBw"
        );

        ReflectionTestUtils.setField(
                jwtService, "expiration", 900000L
        );
    }

    @Test
    public void testGenerateToken() {
        String token = jwtService.generateToken("test@email.com");

        assertEquals("test@email.com", jwtService.extractEmail(token));
    }

    @Test
    public void testValidTokenForCorrectValues() {
        String token = jwtService.generateToken("test@email.com");
        assertTrue(jwtService.isTokenValid(token, "test@email.com"));
    }

    @Test
    public void testInvalidTokenForInvalidValues() {
        String token = jwtService.generateToken("test@email.com");
        assertFalse(jwtService.isTokenValid(token, "incorrect@email.com"));
    }

    @Test
    public void testIsTokenValidExpired() {
        ReflectionTestUtils.setField(jwtService, "expiration", -1000L); // Set expiration to past
        String token = jwtService.generateToken("test@email.com");

        assertFalse(jwtService.isTokenValid(token, "test@email.com"));
    }
}
