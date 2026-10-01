package com.glamgest.app.application.service.report;

import com.glamgest.app.application.dto.dashboard.DashboardCountDTO;
import com.glamgest.app.application.dto.dashboard.DashboardEmployeeMetricDTO;
import com.glamgest.app.application.dto.dashboard.DashboardServiceMetricDTO;
import com.glamgest.app.application.dto.dashboard.DashboardSummaryDTO;
import com.glamgest.app.application.service.dashboard.DashboardService;
import com.glamgest.app.infrastructure.persistence.repository.JpaAppointmentRepository;
import com.glamgest.app.infrastructure.persistence.repository.JpaSaleDetailsRepository;
import com.glamgest.app.infrastructure.persistence.repository.JpaSalesRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class ReportServiceTest {

    private final DashboardService dashboardService = mock(DashboardService.class);
    private final JpaSalesRepository salesRepository = mock(JpaSalesRepository.class);
    private final JpaSaleDetailsRepository saleDetailsRepository = mock(JpaSaleDetailsRepository.class);
    private final JpaAppointmentRepository appointmentRepository = mock(JpaAppointmentRepository.class);
    private final ReportService reportService = new ReportService(dashboardService, salesRepository,
            saleDetailsRepository, appointmentRepository);

    @Test
    void executivePdf_generatesPdfBytes() {
        LocalDate from = LocalDate.of(2026, 9, 1);
        LocalDate to = LocalDate.of(2026, 9, 30);
        when(dashboardService.summary(from, to)).thenReturn(new DashboardSummaryDTO(from, to, 1000, 2,
                java.math.BigDecimal.valueOf(500), 3, 4, List.of(new DashboardCountDTO("COMPLETED", 4))));
        when(dashboardService.services(from, to)).thenReturn(List.of(new DashboardServiceMetricDTO(1, "Corte", 2, 1000)));
        when(dashboardService.employees(from, to)).thenReturn(List.of(new DashboardEmployeeMetricDTO(1, "Ana", 4, 1000)));

        byte[] result = reportService.executivePdf(from, to);

        assertTrue(new String(result, 0, 4).equals("%PDF"));
    }

    @Test
    void salesExcel_generatesXlsxBytes() {
        when(salesRepository.activeDetailBetween(any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(List.of());

        byte[] result = reportService.salesExcel(null, null);

        assertEquals('P', result[0]);
        assertEquals('K', result[1]);
    }
}
