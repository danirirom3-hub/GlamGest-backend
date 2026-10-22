package com.glamgest.app.infrastructure.presentation.controller;

import com.glamgest.app.application.dto.appointment.AppointmentRequestDTO;
import com.glamgest.app.application.dto.appointment.AppointmentResponseDTO;
import com.glamgest.app.application.dto.appointment.AppointmentUpdateDTO;
import com.glamgest.app.application.dto.appointment.AppointmentStatusRequestDTO;
import com.glamgest.app.application.dto.appointment.AppointmentBatchRequestDTO;
import com.glamgest.app.application.usecase.appointment.CreateAppointmentUseCase;
import com.glamgest.app.application.usecase.appointment.DeleteAppointmentUseCase;
import com.glamgest.app.application.usecase.appointment.GetAllAppointmentsUseCase;
import com.glamgest.app.application.usecase.appointment.UpdateAppointmentUseCase;
import com.glamgest.app.application.usecase.appointment.ChangeAppointmentStatusUseCase;
import com.glamgest.app.application.service.audit.AuditLogResourceNameResolver;
import com.glamgest.app.application.service.appointment.CreateAppointmentBatchService;
import com.glamgest.app.infrastructure.presentation.helper.BuilderHelper;
import com.glamgest.app.infrastructure.configuration.AuditLogContext;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final CreateAppointmentUseCase createAppointmentUseCase;
    private final GetAllAppointmentsUseCase getAllAppointmentsUseCase;
    private final UpdateAppointmentUseCase updateAppointmentUseCase;
    private final DeleteAppointmentUseCase deleteAppointmentUseCase;
    private final ChangeAppointmentStatusUseCase changeAppointmentStatusUseCase;
    private final AuditLogContext auditLogContext;
    private final AuditLogResourceNameResolver auditLogResourceNameResolver;
    private final CreateAppointmentBatchService createAppointmentBatchService;

    public AppointmentController(CreateAppointmentUseCase createAppointmentUseCase,
                                 GetAllAppointmentsUseCase getAllAppointmentsUseCase,
                                 UpdateAppointmentUseCase updateAppointmentUseCase,
                                   DeleteAppointmentUseCase deleteAppointmentUseCase,
                                   ChangeAppointmentStatusUseCase changeAppointmentStatusUseCase,
                                   AuditLogContext auditLogContext,
                                   AuditLogResourceNameResolver auditLogResourceNameResolver,
                                   CreateAppointmentBatchService createAppointmentBatchService) {
        this.createAppointmentUseCase = createAppointmentUseCase;
        this.getAllAppointmentsUseCase = getAllAppointmentsUseCase;
        this.updateAppointmentUseCase = updateAppointmentUseCase;
        this.deleteAppointmentUseCase = deleteAppointmentUseCase;
        this.changeAppointmentStatusUseCase = changeAppointmentStatusUseCase;
        this.auditLogContext = auditLogContext;
        this.auditLogResourceNameResolver = auditLogResourceNameResolver;
        this.createAppointmentBatchService = createAppointmentBatchService;
    }

    @PostMapping
    public ResponseEntity<?> createAppointment(@Valid @RequestBody AppointmentRequestDTO appointmentRequestDTO,
                                               BindingResult result) {
        if (result.hasFieldErrors()) {
            return this.validation(result);
        }

        AppointmentResponseDTO response = createAppointmentUseCase.execute(appointmentRequestDTO);
        auditLogContext.setResource(response.getId().toString(), auditLogResourceNameResolver.appointment(response.getId()));
        return BuilderHelper.buildResponse(response, "Cita creada", HttpStatus.CREATED, true);
    }

    @PostMapping("/batch")
    public ResponseEntity<?> createAppointmentBatch(@Valid @RequestBody AppointmentBatchRequestDTO request) {
        List<AppointmentResponseDTO> responses = createAppointmentBatchService.create(request);
        String resourceIds = responses.stream().map(response -> response.getId().toString())
                .collect(java.util.stream.Collectors.joining(","));
        String resourceNames = responses.stream()
                .map(response -> auditLogResourceNameResolver.appointment(response.getId()))
                .collect(java.util.stream.Collectors.joining("; "));
        auditLogContext.setResource(resourceIds, resourceNames);
        return BuilderHelper.buildResponse(responses, "Citas creadas", HttpStatus.CREATED, true);
    }

    @GetMapping
    public ResponseEntity<?> getAllAppointments() {
        List<AppointmentResponseDTO> appointments = getAllAppointmentsUseCase.execute();
        return BuilderHelper.buildResponse(appointments, "Citas obtenidas", HttpStatus.OK, true);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMyAppointments() {
        return BuilderHelper.buildResponse(getAllAppointmentsUseCase.executeForCurrentClient(),
                "Mis citas obtenidas", HttpStatus.OK, true);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateAppointment(@PathVariable Integer id,
                                               @Valid @RequestBody AppointmentUpdateDTO appointmentUpdateDTO,
                                               BindingResult result) {
        // El id proviene de la URL y debe estar disponible antes de validar el DTO.
        appointmentUpdateDTO.setId(id);
        if (result.hasFieldErrors()) {
            return this.validation(result);
        }

        AppointmentResponseDTO response = updateAppointmentUseCase.execute(appointmentUpdateDTO);
        auditLogContext.setResource(response.getId().toString(), auditLogResourceNameResolver.appointment(response.getId()));
        return BuilderHelper.buildResponse(response, "Cita actualizada", HttpStatus.OK, true);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAppointment(@PathVariable Integer id) {
        deleteAppointmentUseCase.execute(id);
        auditLogContext.setResource(id.toString(), auditLogResourceNameResolver.appointment(id));
        return BuilderHelper.buildResponse(null, "Cita cancelada", HttpStatus.NO_CONTENT, true);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> changeStatus(@PathVariable Integer id,
                                          @Valid @RequestBody AppointmentStatusRequestDTO request) {
        AppointmentResponseDTO response = changeAppointmentStatusUseCase.execute(id, request.status());
        auditLogContext.setResource(response.getId().toString(), auditLogResourceNameResolver.appointment(response.getId()));
        return BuilderHelper.buildResponse(response, "Estado de cita actualizado", HttpStatus.OK, true);
    }

    private ResponseEntity<?> validation(BindingResult result) {
        Map<String, Object> errors = new HashMap<>();
        result.getFieldErrors().forEach(error -> errors.put(error.getField(),
                "El campo " + error.getField() + " " + error.getDefaultMessage()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
}
