package com.glamgest.app.application.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(
        @NotBlank(message = "El email es obligatorio") String email,
        @NotBlank(message = "La contraseña es obligatoria") String password,
        @NotBlank(message = "La verificación reCAPTCHA es obligatoria") String recaptchaToken) {

    public LoginRequestDTO(String email, String password) {
        this(email, password, null);
    }
}
