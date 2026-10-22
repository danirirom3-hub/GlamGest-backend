package com.glamgest.app.application.dto.sale;

import java.time.LocalDateTime;

public record CashierAppointmentDTO(
        Integer appointmentId,
        LocalDateTime appointmentDatetime,
        String status,
        Integer serviceId,
        String serviceName,
        Integer scheduledEmployeeId,
        String scheduledEmployeeName,
        Integer durationMinutes,
        Integer defaultPrice) {
}
