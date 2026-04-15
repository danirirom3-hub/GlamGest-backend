package com.glamgest.app.application.service.appointment;

import com.glamgest.app.common.constant.Constant;
import com.glamgest.app.application.dto.appointment.AppointmentRequestDTO;
import com.glamgest.app.application.dto.appointment.AppointmentResponseDTO;
import com.glamgest.app.application.usecase.appointment.CreateAppointmentUseCase;
import com.glamgest.app.common.exception.ResourceNotFoundException;
import com.glamgest.app.domain.model.Appointment;
import com.glamgest.app.domain.repository.AppointmentRepository;
import com.glamgest.app.domain.repository.ClientRepository;
import com.glamgest.app.domain.repository.EmployeeRepository;
import com.glamgest.app.domain.repository.ServiceRepository;
import com.glamgest.app.domain.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@org.springframework.stereotype.Service
public class CreateAppointmentService implements CreateAppointmentUseCase {

    private final AppointmentRepository appointmentRepository;
    private final ClientRepository clientRepository;
    private final EmployeeRepository employeeRepository;
    private final ServiceRepository serviceRepository;
    private final UserRepository userRepository;

    public CreateAppointmentService(AppointmentRepository appointmentRepository,
            ClientRepository clientRepository,
            EmployeeRepository employeeRepository,
            ServiceRepository serviceRepository,
            UserRepository userRepository) {
        this.appointmentRepository = appointmentRepository;
        this.clientRepository = clientRepository;
        this.employeeRepository = employeeRepository;
        this.serviceRepository = serviceRepository;
        this.userRepository = userRepository;
    }

    @Override
    public AppointmentResponseDTO execute(AppointmentRequestDTO appointmentRequestDTO) {
        Integer clientId = appointmentRequestDTO.getClientId();
        Integer employeeId = appointmentRequestDTO.getEmployeeId();
        Integer serviceId = appointmentRequestDTO.getServiceId();

        clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id " + clientId));
        employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id " + employeeId));
        serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with id " + serviceId));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new ResourceNotFoundException("Authenticated user not found");
        }

        String userEmail = authentication.getName();
        Integer userId = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email " + userEmail))
                .getId();

        Appointment appointment = new Appointment();
        appointment.setAppointmentDatetime(appointmentRequestDTO.getAppointmentDatetime());
        appointment.setStatus(Constant.APPOINTMENT_STATUS_PENDING);
        appointment.setNotes(appointmentRequestDTO.getNotes());
        appointment.setClientId(clientId);
        appointment.setEmployeeId(employeeId);
        appointment.setServiceId(serviceId);
        appointment.setUserId(userId);

        Appointment saved = appointmentRepository.save(appointment);

        AppointmentResponseDTO response = new AppointmentResponseDTO();
        response.setId(saved.getId());
        response.setAppointmentDatetime(saved.getAppointmentDatetime());
        response.setStatus(saved.getStatus());
        response.setNotes(saved.getNotes());
        response.setClientId(saved.getClientId());
        response.setEmployeeId(saved.getEmployeeId());
        response.setServiceId(saved.getServiceId());
        response.setUserId(saved.getUserId());

        return response;
    }
}
