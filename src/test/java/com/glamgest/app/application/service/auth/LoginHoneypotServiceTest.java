package com.glamgest.app.application.service.auth;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LoginHoneypotServiceTest {

    private final LoginHoneypotService service = new LoginHoneypotService();

    @Test
    void validate_whenFieldIsEmpty_doesNotThrow() {
        assertDoesNotThrow(() -> service.validate(""));
        assertDoesNotThrow(() -> service.validate(null));
        assertDoesNotThrow(() -> service.validate("   "));
    }

    @Test
    void validate_whenFieldContainsText_throwsBadCredentialsException() {
        assertThrows(BadCredentialsException.class, () -> service.validate("https://bot.example"));
    }
}
