package com.glamgest.app.application.dto.appointment;

import jakarta.validation.constraints.NotBlank;

public record AppointmentStatusRequestDTO(
        @NotBlank(message = "El estado de la cita es obligatorio") String status) {
}
