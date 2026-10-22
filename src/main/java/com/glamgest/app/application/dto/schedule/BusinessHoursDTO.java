package com.glamgest.app.application.dto.schedule;

import java.time.LocalTime;

public record BusinessHoursDTO(
        Integer dayOfWeek,
        boolean closed,
        LocalTime opensAt,
        LocalTime closesAt) {
}
