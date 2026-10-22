package com.glamgest.app.application.dto.schedule;

import java.time.LocalDateTime;

public record EmployeeUnavailabilityRequestDTO(
        Integer employeeId,
        LocalDateTime startsAt,
        LocalDateTime endsAt,
        String reason) {
}
