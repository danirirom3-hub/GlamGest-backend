package com.glamgest.app.application.service.appointment;

import com.glamgest.app.application.dto.appointment.AppointmentResponseDTO;
import com.glamgest.app.application.usecase.appointment.GetAllAppointmentsUseCase;
import com.glamgest.app.domain.repository.AppointmentRepository;
import com.glamgest.app.domain.repository.ClientRepository;
import com.glamgest.app.domain.repository.UserRepository;
import com.glamgest.app.common.exception.ResourceNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GetAllAppointmentsService implements GetAllAppointmentsUseCase {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final ClientRepository clientRepository;

    public GetAllAppointmentsService(AppointmentRepository appointmentRepository, UserRepository userRepository,
                                     ClientRepository clientRepository) {
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
        this.clientRepository = clientRepository;
    }

    @Override
    public List<AppointmentResponseDTO> execute() {
        return toResponse(appointmentRepository.findAll());
    }

    @Override
    public List<AppointmentResponseDTO> executeForCurrentClient() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Integer userId = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario autenticado no encontrado")).getId();
        Integer clientId = clientRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("El usuario no tiene perfil de cliente")).getId();
        return toResponse(appointmentRepository.findAllByClientId(clientId));
    }

    private List<AppointmentResponseDTO> toResponse(List<com.glamgest.app.domain.model.Appointment> appointments) {
        return appointments.stream()
                .map(appointment -> {
                    AppointmentResponseDTO response = new AppointmentResponseDTO(
                            appointment.getId(),
                            appointment.getAppointmentDatetime(),
                            appointment.getStatus(),
                            appointment.getNotes(),
                            appointment.getClientId(),
                            appointment.getEmployeeId(),
                            appointment.getServiceId(),
                            appointment.getUserId());
                    response.setDurationMinutes(appointment.getDurationMinutes());
                    return response;
                })
                .collect(Collectors.toList());
    }
}
