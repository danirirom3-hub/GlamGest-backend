package com.glamgest.app.application.service.appointment;

import com.glamgest.app.application.dto.appointment.AppointmentResponseDTO;
import com.glamgest.app.application.usecase.appointment.ChangeAppointmentStatusUseCase;
import com.glamgest.app.common.constant.AppointmentStatusRules;
import com.glamgest.app.common.exception.ResourceNotFoundException;
import com.glamgest.app.domain.model.Appointment;
import com.glamgest.app.domain.repository.AppointmentRepository;
import com.glamgest.app.domain.repository.ClientRepository;
import com.glamgest.app.domain.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class ChangeAppointmentStatusService implements ChangeAppointmentStatusUseCase {

    private final AppointmentRepository appointmentRepository;
    private final ClientRepository clientRepository;
    private final UserRepository userRepository;

    public ChangeAppointmentStatusService(AppointmentRepository appointmentRepository,
                                          ClientRepository clientRepository,
                                          UserRepository userRepository) {
        this.appointmentRepository = appointmentRepository;
        this.clientRepository = clientRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public AppointmentResponseDTO execute(Integer id, String requestedStatus) {
        String status = AppointmentStatusRules.validate(requestedStatus);

        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id " + id));
        ensureClientOwnsAppointment(appointment);

        AppointmentStatusRules.ensureTransition(appointment.getStatus(), status);

        appointment.setStatus(status);
        return toResponse(appointmentRepository.save(appointment));
    }

    private void ensureClientOwnsAppointment(Appointment appointment) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getAuthorities().stream()
                .anyMatch(authority -> "CLIENT".equals(authority.getAuthority()))) {
            Integer userId = userRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario autenticado no encontrado"))
                    .getId();
            boolean owner = clientRepository.findByUserId(userId)
                    .map(client -> client.getId().equals(appointment.getClientId()))
                    .orElse(false);
            if (!owner) {
                throw new AccessDeniedException("No puede cambiar el estado de esta cita");
            }
        }
    }

    private AppointmentResponseDTO toResponse(Appointment appointment) {
        AppointmentResponseDTO response = new AppointmentResponseDTO(appointment.getId(), appointment.getAppointmentDatetime(),
                appointment.getStatus(), appointment.getNotes(), appointment.getClientId(),
                appointment.getEmployeeId(), appointment.getServiceId(), appointment.getUserId());
        response.setDurationMinutes(appointment.getDurationMinutes());
        return response;
    }
}
