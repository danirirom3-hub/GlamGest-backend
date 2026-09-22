package com.glamgest.app.application.service.appointment;

import com.glamgest.app.common.constant.Constant;
import com.glamgest.app.application.dto.appointment.AppointmentRequestDTO;
import com.glamgest.app.application.dto.appointment.AppointmentResponseDTO;
import com.glamgest.app.application.dto.email.EmailRequestDTO;
import com.glamgest.app.application.usecase.appointment.CreateAppointmentUseCase;
import com.glamgest.app.common.exception.ResourceNotFoundException;
import com.glamgest.app.common.exception.ScheduleConflictException;
import com.glamgest.app.common.validation.DurationRules;
import com.glamgest.app.domain.model.Appointment;
import com.glamgest.app.domain.model.Client;
import com.glamgest.app.domain.repository.AppointmentRepository;
import com.glamgest.app.domain.repository.ClientRepository;
import com.glamgest.app.domain.repository.EmployeeRepository;
import com.glamgest.app.domain.repository.ServiceRepository;
import com.glamgest.app.domain.repository.UserRepository;
import com.glamgest.app.application.service.email.EmailClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;

@org.springframework.stereotype.Service
public class CreateAppointmentService implements CreateAppointmentUseCase {

    private static final Logger logger = LoggerFactory.getLogger(CreateAppointmentService.class);

    private final AppointmentRepository appointmentRepository;
    private final ClientRepository clientRepository;
    private final EmployeeRepository employeeRepository;
    private final ServiceRepository serviceRepository;
    private final UserRepository userRepository;
    private final EmailClientService emailClientService;

    public CreateAppointmentService(AppointmentRepository appointmentRepository,
            ClientRepository clientRepository,
            EmployeeRepository employeeRepository,
            ServiceRepository serviceRepository,
            UserRepository userRepository,
            EmailClientService emailClientService) {
        this.appointmentRepository = appointmentRepository;
        this.clientRepository = clientRepository;
        this.employeeRepository = employeeRepository;
        this.serviceRepository = serviceRepository;
        this.userRepository = userRepository;
        this.emailClientService = emailClientService;
    }

    @Override
    @Transactional
    public AppointmentResponseDTO execute(AppointmentRequestDTO appointmentRequestDTO) {
        Integer clientId = appointmentRequestDTO.getClientId();
        Integer employeeId = appointmentRequestDTO.getEmployeeId();
        Integer serviceId = appointmentRequestDTO.getServiceId();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new ResourceNotFoundException("Authenticated user not found");
        }
        Integer userId = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email " + authentication.getName()))
                .getId();
        boolean isClient = authentication.getAuthorities().stream().anyMatch(a -> "CLIENT".equals(a.getAuthority()));
        Client authenticatedClient = clientRepository.findByUserId(userId).orElse(null);
        if (isClient) {
            if (authenticatedClient == null) {
                throw new ResourceNotFoundException("El usuario no tiene perfil de cliente");
            }
            if (clientId != null && !clientId.equals(authenticatedClient.getId())) {
                throw new AccessDeniedException("No puede crear una cita para otro cliente");
            }
            clientId = authenticatedClient.getId();
        }
        if (clientId == null) {
            throw new ResourceNotFoundException("Client not found with id null");
        }
        final Integer selectedClientId = clientId;
        Client client = clientRepository.findById(selectedClientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id " + selectedClientId));
        var employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id " + employeeId));
        var service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with id " + serviceId));
        if (Boolean.FALSE.equals(employee.getActive())) {
            throw new IllegalArgumentException("El empleado no está activo");
        }
        if (Boolean.FALSE.equals(service.getActive())) {
            throw new IllegalArgumentException("El servicio no está activo");
        }
        if (appointmentRequestDTO.getAppointmentDatetime().before(new Date())) {
            throw new IllegalArgumentException("La fecha de la cita debe ser futura");
        }
        Integer durationMinutes = appointmentRequestDTO.getDurationMinutes() != null
                ? DurationRules.validate(appointmentRequestDTO.getDurationMinutes())
                : DurationRules.validate(service.getDurationMinutes());
        ensureNoOverlap(employeeId, appointmentRequestDTO.getAppointmentDatetime(), durationMinutes, null);

        Appointment appointment = new Appointment();
        appointment.setAppointmentDatetime(appointmentRequestDTO.getAppointmentDatetime());
        appointment.setStatus(Constant.APPOINTMENT_STATUS_PENDING);
        appointment.setNotes(appointmentRequestDTO.getNotes());
        appointment.setClientId(clientId);
        appointment.setEmployeeId(employeeId);
        appointment.setServiceId(serviceId);
        appointment.setUserId(userId);
        appointment.setDurationMinutes(durationMinutes);

        Appointment saved = appointmentRepository.save(appointment);

        // Enviar correo al cliente
        try {
            sendConfirmationEmail(client, saved);
        } catch (Exception e) {
            logger.error("Error al enviar correo de confirmación al cliente: {}", e.getMessage(), e);
            // No lanzamos excepción para que la cita se cree aún si hay error en email
        }

        AppointmentResponseDTO response = new AppointmentResponseDTO();
        response.setId(saved.getId());
        response.setAppointmentDatetime(saved.getAppointmentDatetime());
        response.setStatus(saved.getStatus());
        response.setNotes(saved.getNotes());
        response.setClientId(saved.getClientId());
        response.setEmployeeId(saved.getEmployeeId());
        response.setServiceId(saved.getServiceId());
        response.setUserId(saved.getUserId());
        response.setDurationMinutes(saved.getDurationMinutes());

        return response;
    }

    private void ensureNoOverlap(Integer employeeId, Date start, Integer durationMinutes, Integer appointmentId) {
        long endMillis = start.getTime() + durationMinutes.longValue() * 60_000L;
        for (Appointment existing : appointmentRepository.findAllByEmployeeId(employeeId)) {
            if (appointmentId != null && appointmentId.equals(existing.getId())) {
                continue;
            }
            if ("CANCELLED".equalsIgnoreCase(existing.getStatus())
                    || "NO_SHOW".equalsIgnoreCase(existing.getStatus())) {
                continue;
            }
            Integer existingDuration = existing.getDurationMinutes();
            if (existingDuration == null && existing.getServiceId() != null) {
                existingDuration = serviceRepository.findById(existing.getServiceId())
                        .map(com.glamgest.app.domain.model.Service::getDurationMinutes)
                        .orElse(null);
            }
            if (existingDuration == null) {
                continue;
            }
            long existingStart = existing.getAppointmentDatetime().getTime();
            long existingEnd = existingStart + existingDuration.longValue() * 60_000L;
            if (start.getTime() < existingEnd && endMillis > existingStart) {
                throw new ScheduleConflictException("El empleado ya tiene una cita en ese horario.");
            }
        }
    }

    /**
     * Envía un correo de confirmación al cliente cuando se crea una cita
     */
    private void sendConfirmationEmail(Client client, Appointment appointment) {
        if (client == null || client.getEmail() == null || client.getEmail().isEmpty()) {
            logger.warn("No se puede enviar correo: cliente sin email");
            return;
        }

        String subject = "Confirmación de tu cita en GlamGest";
        String body = String.format(
                "Hola %s,\n\n" +
                        "Tu cita ha sido registrada exitosamente en GlamGest.\n\n" +
                        "Fecha y Hora: %s\n" +
                        "Estado: %s\n" +
                        "Notas: %s\n\n" +
                        "Gracias por elegirnos.\n" +
                        "GlamGest Team",
                client.getName(),
                appointment.getAppointmentDatetime(),
                appointment.getStatus(),
                appointment.getNotes() != null ? appointment.getNotes() : "N/A"
        );

        EmailRequestDTO emailRequest = new EmailRequestDTO(
                client.getEmail(),
                subject,
                body
        );

        emailClientService.sendEmail(emailRequest);
    }
}
