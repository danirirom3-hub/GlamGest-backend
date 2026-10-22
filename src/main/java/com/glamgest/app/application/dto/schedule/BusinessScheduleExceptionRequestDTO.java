package com.glamgest.app.application.dto.schedule;

import java.time.LocalDate;
import java.time.LocalTime;

public record BusinessScheduleExceptionRequestDTO(
        LocalDate date,
        boolean closed,
        LocalTime opensAt,
        LocalTime closesAt,
        String reason) {
}
