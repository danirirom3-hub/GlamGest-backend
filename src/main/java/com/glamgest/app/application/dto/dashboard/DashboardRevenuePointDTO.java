package com.glamgest.app.application.dto.dashboard;

import java.time.LocalDate;

public record DashboardRevenuePointDTO(LocalDate date, long sales, long revenue) {
}
