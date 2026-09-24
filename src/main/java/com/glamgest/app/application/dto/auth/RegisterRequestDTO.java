package com.glamgest.app.application.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.AssertTrue;

public record RegisterRequestDTO(
        @NotBlank(message = "El nombre es obligatorio") String name,
        @NotBlank(message = "El email es obligatorio") @Email(message = "El email no es válido") String email,
        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
        @Pattern(regexp = ".*[A-Z].*", message = "La contraseña debe contener al menos una letra mayúscula")
        @Pattern(regexp = ".*[a-z].*", message = "La contraseña debe contener al menos una letra minúscula")
        @Pattern(regexp = ".*[0-9].*", message = "La contraseña debe contener al menos un número")
        @Pattern(regexp = ".*[^A-Za-z0-9\\s].*", message = "La contraseña debe contener al menos un carácter especial")
        @Pattern(regexp = "^\\S+$", message = "La contraseña no debe contener espacios") String password,
        @NotBlank(message = "El teléfono es obligatorio") @Pattern(regexp = "^\\+?[0-9\\-\\s]{7,20}$", message = "El teléfono no es válido") String phone,
        @AssertTrue(message = "Debe aceptar la política de tratamiento de datos") boolean privacyPolicyAccepted) {

    public RegisterRequestDTO(String name, String email, String password, String phone) {
        this(name, email, password, phone, false);
    }
}
