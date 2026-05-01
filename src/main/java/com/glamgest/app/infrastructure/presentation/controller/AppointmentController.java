package com.glamgest.app.infrastructure.presentation.controller;

import com.glamgest.app.application.dto.appointment.AppointmentRequestDTO;
import com.glamgest.app.application.dto.appointment.AppointmentResponseDTO;
import com.glamgest.app.application.dto.appointment.AppointmentUpdateDTO;
import com.glamgest.app.application.usecase.appointment.CreateAppointmentUseCase;
import com.glamgest.app.application.usecase.appointment.DeleteAppointmentUseCase;
import com.glamgest.app.application.usecase.appointment.GetAllAppointmentsUseCase;
import com.glamgest.app.application.usecase.appointment.UpdateAppointmentUseCase;
import com.glamgest.app.infrastructure.presentation.helper.BuilderHelper;
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

    public AppointmentController(CreateAppointmentUseCase createAppointmentUseCase,
                                 GetAllAppointmentsUseCase getAllAppointmentsUseCase,
                                 UpdateAppointmentUseCase updateAppointmentUseCase,
                                 DeleteAppointmentUseCase deleteAppointmentUseCase) {
        this.createAppointmentUseCase = createAppointmentUseCase;
        this.getAllAppointmentsUseCase = getAllAppointmentsUseCase;
        this.updateAppointmentUseCase = updateAppointmentUseCase;
        this.deleteAppointmentUseCase = deleteAppointmentUseCase;
    }

    @PostMapping
    public ResponseEntity<?> createAppointment(@Valid @RequestBody AppointmentRequestDTO appointmentRequestDTO,
                                               BindingResult result) {
        if (result.hasFieldErrors()) {
            return this.validation(result);
        }

        AppointmentResponseDTO response = createAppointmentUseCase.execute(appointmentRequestDTO);
        return BuilderHelper.buildResponse(response, "Cita creada", HttpStatus.CREATED, true);
    }

    @GetMapping
    public ResponseEntity<?> getAllAppointments() {
        List<AppointmentResponseDTO> appointments = getAllAppointmentsUseCase.execute();
        return BuilderHelper.buildResponse(appointments, "Citas obtenidas", HttpStatus.OK, true);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateAppointment(@PathVariable Integer id,
                                               @Valid @RequestBody AppointmentUpdateDTO appointmentUpdateDTO,
                                               BindingResult result) {
        if (result.hasFieldErrors()) {
            return this.validation(result);
        }

        appointmentUpdateDTO.setId(id);
        AppointmentResponseDTO response = updateAppointmentUseCase.execute(appointmentUpdateDTO);
        return BuilderHelper.buildResponse(response, "Cita actualizada", HttpStatus.OK, true);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAppointment(@PathVariable Integer id) {
        deleteAppointmentUseCase.execute(id);
        return BuilderHelper.buildResponse(null, "Cita eliminada", HttpStatus.NO_CONTENT, true);
    }

    private ResponseEntity<?> validation(BindingResult result) {
        Map<String, Object> errors = new HashMap<>();
        result.getFieldErrors().forEach(error -> errors.put(error.getField(),
                "El campo " + error.getField() + " " + error.getDefaultMessage()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
}
