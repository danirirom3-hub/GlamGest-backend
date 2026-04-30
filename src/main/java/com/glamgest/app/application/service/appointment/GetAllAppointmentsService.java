package com.glamgest.app.application.service.appointment;

import com.glamgest.app.application.dto.appointment.AppointmentResponseDTO;
import com.glamgest.app.application.usecase.appointment.GetAllAppointmentsUseCase;
import com.glamgest.app.domain.repository.AppointmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GetAllAppointmentsService implements GetAllAppointmentsUseCase {

    private final AppointmentRepository appointmentRepository;

    public GetAllAppointmentsService(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    @Override
    public List<AppointmentResponseDTO> execute() {
        return appointmentRepository.findAll().stream()
                .map(appointment -> new AppointmentResponseDTO(
                        appointment.getId(),
                        appointment.getAppointmentDatetime(),
                        appointment.getStatus(),
                        appointment.getNotes(),
                        appointment.getClientId(),
                        appointment.getEmployeeId(),
                        appointment.getServiceId(),
                        appointment.getUserId()))
                .collect(Collectors.toList());
    }
}
