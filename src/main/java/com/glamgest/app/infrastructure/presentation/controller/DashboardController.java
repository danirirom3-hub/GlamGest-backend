package com.glamgest.app.infrastructure.presentation.controller;

import com.glamgest.app.application.dto.dashboard.DashboardEmployeeMetricDTO;
import com.glamgest.app.application.dto.dashboard.DashboardCountDTO;
import com.glamgest.app.application.dto.dashboard.DashboardRevenuePointDTO;
import com.glamgest.app.application.dto.dashboard.DashboardServiceMetricDTO;
import com.glamgest.app.application.dto.dashboard.DashboardSummaryDTO;
import com.glamgest.app.application.service.dashboard.DashboardService;
import com.glamgest.app.infrastructure.presentation.helper.BuilderHelper;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/summary")
    public ResponseEntity<?> summary(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                     @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        DashboardSummaryDTO response = dashboardService.summary(from, to);
        return BuilderHelper.buildResponse(response, "Resumen del dashboard obtenido", HttpStatus.OK, true);
    }

    @GetMapping("/revenue")
    public ResponseEntity<?> revenue(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                     @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        List<DashboardRevenuePointDTO> response = dashboardService.revenue(from, to);
        return BuilderHelper.buildResponse(response, "Ingresos del dashboard obtenidos", HttpStatus.OK, true);
    }

    @GetMapping("/services")
    public ResponseEntity<?> services(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        List<DashboardServiceMetricDTO> response = dashboardService.services(from, to);
        return BuilderHelper.buildResponse(response, "Servicios del dashboard obtenidos", HttpStatus.OK, true);
    }

    @GetMapping("/appointments")
    public ResponseEntity<?> appointments(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        List<DashboardCountDTO> response = dashboardService.appointments(from, to);
        return BuilderHelper.buildResponse(response, "Citas del dashboard obtenidas", HttpStatus.OK, true);
    }

    @GetMapping("/employees")
    public ResponseEntity<?> employees(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        List<DashboardEmployeeMetricDTO> response = dashboardService.employees(from, to);
        return BuilderHelper.buildResponse(response, "Rendimiento de empleados obtenido", HttpStatus.OK, true);
    }
}
