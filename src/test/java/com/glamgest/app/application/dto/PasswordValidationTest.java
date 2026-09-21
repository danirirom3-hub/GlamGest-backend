package com.glamgest.app.application.dto;

import com.glamgest.app.application.dto.auth.RegisterRequestDTO;
import com.glamgest.app.application.dto.user.UserRequestDTO;
import com.glamgest.app.application.dto.user.UserUpdateDTO;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

class PasswordValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @AfterAll
    static void tearDown() {
        validator = null;
    }

    @Test
    void acceptsSecurePasswordOnPublicRegistration() {
        RegisterRequestDTO request = new RegisterRequestDTO("Test User", "test@example.com", "Segura123!", "+34123456789");

        assertTrue(validator.validateProperty(request, "password").isEmpty());
    }

    @Test
    void rejectsPasswordWithoutRequiredCharactersOnPublicRegistration() {
        RegisterRequestDTO request = new RegisterRequestDTO("Test User", "test@example.com", "password", "+34123456789");

        Set<String> messages = validator.validateProperty(request, "password").stream()
                .map(error -> error.getMessage())
                .collect(Collectors.toSet());

        assertFalse(messages.contains("La contraseña debe tener al menos 8 caracteres"));
        assertTrue(messages.contains("La contraseña debe contener al menos una letra mayúscula"));
        assertTrue(messages.contains("La contraseña debe contener al menos un número"));
        assertTrue(messages.contains("La contraseña debe contener al menos un carácter especial"));
    }

    @Test
    void appliesSameRulesToAdministrativeUserDtos() {
        UserRequestDTO createRequest = new UserRequestDTO();
        createRequest.setPassword("Segura123!");
        UserUpdateDTO updateRequest = new UserUpdateDTO();
        updateRequest.setPassword("Segura123!");

        assertTrue(validator.validateProperty(createRequest, "password").isEmpty());
        assertTrue(validator.validateProperty(updateRequest, "password").isEmpty());
    }
}
