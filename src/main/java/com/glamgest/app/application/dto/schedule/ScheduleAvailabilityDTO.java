package com.glamgest.app.application.dto.schedule;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record ScheduleAvailabilityDTO(
        LocalDate date,
        boolean closed,
        LocalTime opensAt,
        LocalTime closesAt,
        Integer durationMinutes,
        List<EmployeeAvailableSlotsDTO> employees) {
}
