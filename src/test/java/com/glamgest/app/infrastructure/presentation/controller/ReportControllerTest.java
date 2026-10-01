package com.glamgest.app.infrastructure.presentation.controller;

import com.glamgest.app.application.service.report.ReportService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ReportControllerTest {

    private final ReportService reportService = mock(ReportService.class);
    private final ReportController controller = new ReportController(reportService);

    @Test
    void executivePdf_returnsDownloadHeaders() {
        byte[] content = "pdf".getBytes();
        when(reportService.executivePdf(LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 30)))
                .thenReturn(content);

        var response = controller.executivePdf(LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 30));

        assertEquals(MediaType.APPLICATION_PDF, response.getHeaders().getContentType());
        assertEquals("attachment; filename=\"reporte-ejecutivo.pdf\"",
                response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION));
        assertArrayEquals(content, response.getBody().getByteArray());
    }

    @Test
    void salesExcel_returnsExcelContentType() {
        byte[] content = "excel".getBytes();
        when(reportService.salesExcel(null, null)).thenReturn(content);

        var response = controller.salesExcel(null, null);

        assertEquals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                response.getHeaders().getContentType().toString());
        assertEquals("attachment; filename=\"detalle-ventas.xlsx\"",
                response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION));
    }
}
