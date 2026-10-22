package com.glamgest.app.application.dto.schedule;

import java.time.LocalDateTime;

public record EmployeeUnavailabilityDTO(
        Long id,
        Integer employeeId,
        String employeeName,
        LocalDateTime startsAt,
        LocalDateTime endsAt,
        String reason) {
}
