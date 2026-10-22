package com.glamgest.app.application.dto.schedule;

import java.time.LocalTime;
import java.util.List;

public record EmployeeAvailableSlotsDTO(
        Integer employeeId,
        String employeeName,
        List<LocalTime> startsAt) {
}
