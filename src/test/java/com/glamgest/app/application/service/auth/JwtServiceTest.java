package com.glamgest.app.application.service.auth;

import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtServiceTest {

    @Test
    void generateToken_andValidateToken_shouldReturnValidToken() {
        JwtService jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret", "mySecretKeyForJwtServiceAtLeast32Chars!");
        ReflectionTestUtils.setField(jwtService, "expiration", 3600000L);

        UserDetails userDetails = new User("user@example.com", "password123", List.of());

        String token = jwtService.generateToken(userDetails);

        assertNotNull(token);
        assertEquals("user@example.com", jwtService.extractUsername(token));
        assertTrue(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    void isTokenValid_whenUsernameMismatch_returnsFalse() {
        JwtService jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret", "mySecretKeyForJwtServiceAtLeast32Chars!");
        ReflectionTestUtils.setField(jwtService, "expiration", 3600000L);

        UserDetails userDetails = new User("user@example.com", "password123", List.of());
        UserDetails otherUser = new User("other@example.com", "password123", List.of());

        String token = jwtService.generateToken(userDetails);

        assertFalse(jwtService.isTokenValid(token, otherUser));
    }

    @Test
    void isTokenValid_whenTokenExpired_throwsExpiredJwtException() {
        JwtService jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret", "mySecretKeyForJwtServiceAtLeast32Chars!");
        ReflectionTestUtils.setField(jwtService, "expiration", -1000L);

        UserDetails userDetails = new User("user@example.com", "password123", List.of());

        String token = jwtService.generateToken(userDetails);

        assertThrows(ExpiredJwtException.class, () -> jwtService.isTokenValid(token, userDetails));
    }
}
