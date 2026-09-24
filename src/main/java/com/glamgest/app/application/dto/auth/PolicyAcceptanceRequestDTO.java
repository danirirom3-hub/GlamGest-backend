package com.glamgest.app.application.dto.auth;

import jakarta.validation.constraints.AssertTrue;

public record PolicyAcceptanceRequestDTO(
        @AssertTrue(message = "Debe aceptar la política de tratamiento de datos") boolean accepted) {
}
