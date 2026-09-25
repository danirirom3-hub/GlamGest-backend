package com.glamgest.app.application.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record UnlockRequestDTO(
        @NotBlank(message = "La contraseña es obligatoria") String password) {
}
