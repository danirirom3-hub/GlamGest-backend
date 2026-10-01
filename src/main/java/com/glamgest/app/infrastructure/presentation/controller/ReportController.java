package com.glamgest.app.infrastructure.presentation.controller;

import com.glamgest.app.application.service.report.ReportService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private static final MediaType PDF = MediaType.APPLICATION_PDF;
    private static final MediaType EXCEL = MediaType.parseMediaType(
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping(value = "/executive/pdf", produces = "application/pdf")
    public ResponseEntity<ByteArrayResource> executivePdf(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                                          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return file(reportService.executivePdf(from, to), PDF, "reporte-ejecutivo", "pdf");
    }

    @GetMapping(value = "/appointments/pdf", produces = "application/pdf")
    public ResponseEntity<ByteArrayResource> appointmentsPdf(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                                             @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return file(reportService.appointmentsPdf(from, to), PDF, "reporte-citas", "pdf");
    }

    @GetMapping(value = "/sales/excel", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public ResponseEntity<ByteArrayResource> salesExcel(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return file(reportService.salesExcel(from, to), EXCEL, "detalle-ventas", "xlsx");
    }

    @GetMapping(value = "/services/excel", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public ResponseEntity<ByteArrayResource> servicesExcel(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                                           @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return file(reportService.servicesExcel(from, to), EXCEL, "servicios-mas-vendidos", "xlsx");
    }

    @GetMapping(value = "/employees/excel", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public ResponseEntity<ByteArrayResource> employeesExcel(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                                            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return file(reportService.employeesExcel(from, to), EXCEL, "rendimiento-empleados", "xlsx");
    }

    private ResponseEntity<ByteArrayResource> file(byte[] content, MediaType mediaType, String name, String extension) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);
        headers.setContentDisposition(ContentDisposition.attachment().filename(name + "." + extension).build());
        headers.setContentLength(content.length);
        return ResponseEntity.ok().headers(headers).body(new ByteArrayResource(content));
    }
}
