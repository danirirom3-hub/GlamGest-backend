package com.glamgest.app.application.dto.appointment;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Date;
import java.util.List;

public record AppointmentBatchRequestDTO(
        Integer clientId,
        @NotNull(message = "La fecha de la cita es obligatoria") Date appointmentDatetime,
        @NotNull(message = "El id del empleado es obligatorio") Integer employeeId,
        @NotEmpty(message = "Debes seleccionar al menos un servicio") List<Integer> serviceIds,
        String notes) {
}
