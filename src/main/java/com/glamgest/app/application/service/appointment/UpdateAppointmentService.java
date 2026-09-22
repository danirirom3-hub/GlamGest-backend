package com.glamgest.app.application.service.appointment;

import com.glamgest.app.application.dto.appointment.AppointmentResponseDTO;
import com.glamgest.app.application.dto.appointment.AppointmentUpdateDTO;
import com.glamgest.app.application.usecase.appointment.UpdateAppointmentUseCase;
import com.glamgest.app.common.exception.ResourceNotFoundException;
import com.glamgest.app.domain.model.Appointment;
import com.glamgest.app.domain.repository.AppointmentRepository;
import com.glamgest.app.infrastructure.persistence.repository.JpaClientRepository;
import com.glamgest.app.infrastructure.persistence.repository.JpaEmployeeRepository;
import com.glamgest.app.infrastructure.persistence.repository.JpaServiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import com.glamgest.app.domain.repository.ClientRepository;
import com.glamgest.app.domain.repository.UserRepository;
import com.glamgest.app.common.constant.AppointmentStatusRules;

@Service
public class UpdateAppointmentService implements UpdateAppointmentUseCase {

    private final AppointmentRepository appointmentRepository;
    private final JpaClientRepository jpaClientRepository;
    private final JpaEmployeeRepository jpaEmployeeRepository;
    private final JpaServiceRepository jpaServiceRepository;
    private final ClientRepository clientRepository;
    private final UserRepository userRepository;

    public UpdateAppointmentService(AppointmentRepository appointmentRepository,
                                   JpaClientRepository jpaClientRepository,
                                   JpaEmployeeRepository jpaEmployeeRepository,
                                   JpaServiceRepository jpaServiceRepository,
                                   ClientRepository clientRepository, UserRepository userRepository) {
        this.appointmentRepository = appointmentRepository;
        this.jpaClientRepository = jpaClientRepository;
        this.jpaEmployeeRepository = jpaEmployeeRepository;
        this.jpaServiceRepository = jpaServiceRepository;
        this.clientRepository = clientRepository;
        this.userRepository = userRepository;
    }

    @Override
    public AppointmentResponseDTO execute(AppointmentUpdateDTO appointmentUpdateDTO) {
        Appointment existingAppointment = appointmentRepository.findById(appointmentUpdateDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id " + appointmentUpdateDTO.getId()));
        if (isClient() && !ownsAppointment(existingAppointment)) {
            throw new AccessDeniedException("No puede modificar esta cita");
        }

        // Validate relationships
        if (jpaClientRepository.findById(appointmentUpdateDTO.getClientId())
                .filter(client -> !Boolean.FALSE.equals(client.getActive())).isEmpty()) {
            throw new ResourceNotFoundException("Client not found with id " + appointmentUpdateDTO.getClientId());
        }
        if (jpaEmployeeRepository.findById(appointmentUpdateDTO.getEmployeeId())
                .filter(employee -> !Boolean.FALSE.equals(employee.getActive())).isEmpty()) {
            throw new ResourceNotFoundException("Employee not found with id " + appointmentUpdateDTO.getEmployeeId());
        }
        if (jpaServiceRepository.findById(appointmentUpdateDTO.getServiceId())
                .filter(service -> !Boolean.FALSE.equals(service.getActive())).isEmpty()) {
            throw new ResourceNotFoundException("Service not found with id " + appointmentUpdateDTO.getServiceId());
        }

        // Update appointment
        Integer clientId = isClient() ? existingAppointment.getClientId() : appointmentUpdateDTO.getClientId();
        String status = isClient() ? existingAppointment.getStatus()
                : appointmentUpdateDTO.getStatus() != null ? appointmentUpdateDTO.getStatus() : existingAppointment.getStatus();
        if (status != null) {
            status = AppointmentStatusRules.validate(status);
            AppointmentStatusRules.ensureTransition(existingAppointment.getStatus(), status);
        }
        Appointment updatedAppointment = new Appointment(
                appointmentUpdateDTO.getId(),
                appointmentUpdateDTO.getAppointmentDatetime(),
                status,
                appointmentUpdateDTO.getNotes(),
                clientId,
                appointmentUpdateDTO.getEmployeeId(),
                appointmentUpdateDTO.getServiceId(),
                existingAppointment.getUserId()
        );

        Appointment saved = appointmentRepository.save(updatedAppointment);

        return new AppointmentResponseDTO(
                saved.getId(),
                saved.getAppointmentDatetime(),
                saved.getStatus(),
                saved.getNotes(),
                saved.getClientId(),
                saved.getEmployeeId(),
                saved.getServiceId(),
                saved.getUserId()
        );
    }

    private boolean isClient() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(a -> "CLIENT".equals(a.getAuthority()));
    }

    private boolean ownsAppointment(Appointment appointment) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Integer userId = userRepository.findByEmail(email).orElseThrow().getId();
        return clientRepository.findByUserId(userId).map(c -> c.getId().equals(appointment.getClientId())).orElse(false);
    }
}
