package com.glamgest.app.infrastructure.presentation.controller;

import com.glamgest.app.application.dto.schedule.BusinessHoursDTO;
import com.glamgest.app.application.dto.schedule.BusinessScheduleExceptionRequestDTO;
import com.glamgest.app.application.dto.schedule.EmployeeUnavailabilityRequestDTO;
import com.glamgest.app.application.service.schedule.BusinessScheduleService;
import com.glamgest.app.infrastructure.presentation.helper.BuilderHelper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/schedule")
public class BusinessScheduleController {

    private final BusinessScheduleService scheduleService;

    public BusinessScheduleController(BusinessScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @GetMapping("/business-hours")
    public ResponseEntity<?> getBusinessHours() {
        return BuilderHelper.buildResponse(scheduleService.getBusinessHours(), "Business hours retrieved", HttpStatus.OK, true);
    }

    @PutMapping("/business-hours")
    public ResponseEntity<?> updateBusinessHours(@RequestBody List<BusinessHoursDTO> hours) {
        return BuilderHelper.buildResponse(scheduleService.updateBusinessHours(hours), "Business hours updated", HttpStatus.OK, true);
    }

    @GetMapping("/exceptions")
    public ResponseEntity<?> getExceptions(@RequestParam LocalDate from, @RequestParam LocalDate to) {
        return BuilderHelper.buildResponse(scheduleService.getExceptions(from, to), "Schedule exceptions retrieved", HttpStatus.OK, true);
    }

    @PutMapping("/exceptions")
    public ResponseEntity<?> saveException(@RequestBody BusinessScheduleExceptionRequestDTO request) {
        return BuilderHelper.buildResponse(scheduleService.saveException(request), "Schedule exception saved", HttpStatus.OK, true);
    }

    @DeleteMapping("/exceptions/{id}")
    public ResponseEntity<Void> deleteException(@PathVariable Long id) {
        scheduleService.deleteException(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/employee-blocks")
    public ResponseEntity<?> getEmployeeBlocks(@RequestParam LocalDateTime from, @RequestParam LocalDateTime to) {
        return BuilderHelper.buildResponse(scheduleService.getEmployeeUnavailability(from, to),
                "Employee unavailability retrieved", HttpStatus.OK, true);
    }

    @PostMapping("/employee-blocks")
    public ResponseEntity<?> createEmployeeBlock(@RequestBody EmployeeUnavailabilityRequestDTO request) {
        return BuilderHelper.buildResponse(scheduleService.createEmployeeUnavailability(request),
                "Employee availability blocked", HttpStatus.CREATED, true);
    }

    @DeleteMapping("/employee-blocks/{id}")
    public ResponseEntity<Void> deleteEmployeeBlock(@PathVariable Long id) {
        scheduleService.deleteEmployeeUnavailability(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/availability")
    public ResponseEntity<?> getAvailability(@RequestParam LocalDate date, @RequestParam List<Integer> serviceIds,
                                             @RequestParam(required = false) Integer ignoreAppointmentId,
                                             @RequestParam(required = false) Integer clientId) {
        return BuilderHelper.buildResponse(scheduleService.getAvailability(date, serviceIds, ignoreAppointmentId, clientId),
                "Availability retrieved", HttpStatus.OK, true);
    }
}
