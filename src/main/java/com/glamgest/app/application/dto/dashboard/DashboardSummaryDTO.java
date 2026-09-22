package com.glamgest.app.application.dto.dashboard;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record DashboardSummaryDTO(
        LocalDate from,
        LocalDate to,
        long totalRevenue,
        long totalSales,
        BigDecimal averageTicket,
        long newClients,
        long totalAppointments,
        List<DashboardCountDTO> appointmentsByStatus) {
}
