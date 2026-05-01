package com.glamgest.app.application.service.appointment;

import com.glamgest.app.application.dto.appointment.AppointmentRequestDTO;
import com.glamgest.app.application.dto.appointment.AppointmentResponseDTO;
import com.glamgest.app.common.constant.Constant;
import com.glamgest.app.common.exception.ResourceNotFoundException;
import com.glamgest.app.domain.model.Appointment;
import com.glamgest.app.domain.repository.AppointmentRepository;
import com.glamgest.app.domain.repository.ClientRepository;
import com.glamgest.app.domain.repository.EmployeeRepository;
import com.glamgest.app.domain.repository.ServiceRepository;
import com.glamgest.app.domain.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CreateAppointmentServiceTest {

    @AfterEach
    void cleanup() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void execute_whenAllDependenciesExist_returnsCreatedAppointmentResponse() {
        AppointmentRepository appointmentRepository = mock(AppointmentRepository.class);
        ClientRepository clientRepository = mock(ClientRepository.class);
        EmployeeRepository employeeRepository = mock(EmployeeRepository.class);
        ServiceRepository serviceRepository = mock(ServiceRepository.class);
        UserRepository userRepository = mock(UserRepository.class);

        CreateAppointmentService service = new CreateAppointmentService(
                appointmentRepository,
                clientRepository,
                employeeRepository,
                serviceRepository,
                userRepository
        );

        AppointmentRequestDTO request = new AppointmentRequestDTO();
        Date appointmentDate = new Date();
        request.setAppointmentDatetime(appointmentDate);
        request.setNotes("Test notes");
        request.setClientId(1);
        request.setEmployeeId(2);
        request.setServiceId(3);

        when(clientRepository.findById(1)).thenReturn(Optional.of(mock(com.glamgest.app.domain.model.Client.class)));
        when(employeeRepository.findById(2)).thenReturn(Optional.of(mock(com.glamgest.app.domain.model.Employee.class)));
        when(serviceRepository.findById(3)).thenReturn(Optional.of(mock(com.glamgest.app.domain.model.Service.class)));

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("user@example.com");
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        com.glamgest.app.domain.model.User user = mock(com.glamgest.app.domain.model.User.class);
        when(user.getId()).thenReturn(99);
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        Appointment savedAppointment = new Appointment();
        savedAppointment.setId(10);
        savedAppointment.setAppointmentDatetime(appointmentDate);
        savedAppointment.setStatus(Constant.APPOINTMENT_STATUS_PENDING);
        savedAppointment.setNotes("Test notes");
        savedAppointment.setClientId(1);
        savedAppointment.setEmployeeId(2);
        savedAppointment.setServiceId(3);
        savedAppointment.setUserId(99);
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(savedAppointment);

        AppointmentResponseDTO response = service.execute(request);

        assertEquals(10, response.getId());
        assertEquals(appointmentDate, response.getAppointmentDatetime());
        assertEquals(Constant.APPOINTMENT_STATUS_PENDING, response.getStatus());
        assertEquals("Test notes", response.getNotes());
        assertEquals(1, response.getClientId());
        assertEquals(2, response.getEmployeeId());
        assertEquals(3, response.getServiceId());
        assertEquals(99, response.getUserId());

        verify(clientRepository).findById(1);
        verify(employeeRepository).findById(2);
        verify(serviceRepository).findById(3);
        verify(userRepository).findByEmail("user@example.com");
        verify(appointmentRepository).save(any(Appointment.class));
    }

    @Test
    void execute_whenClientDoesNotExist_throwsResourceNotFoundException() {
        AppointmentRepository appointmentRepository = mock(AppointmentRepository.class);
        ClientRepository clientRepository = mock(ClientRepository.class);
        EmployeeRepository employeeRepository = mock(EmployeeRepository.class);
        ServiceRepository serviceRepository = mock(ServiceRepository.class);
        UserRepository userRepository = mock(UserRepository.class);

        CreateAppointmentService service = new CreateAppointmentService(
                appointmentRepository,
                clientRepository,
                employeeRepository,
                serviceRepository,
                userRepository
        );

        AppointmentRequestDTO request = new AppointmentRequestDTO();
        request.setAppointmentDatetime(new Date());
        request.setNotes("Test notes");
        request.setClientId(1);
        request.setEmployeeId(2);
        request.setServiceId(3);

        when(clientRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.execute(request));
    }

    @Test
    void execute_whenAuthenticationMissing_throwsResourceNotFoundException() {
        AppointmentRepository appointmentRepository = mock(AppointmentRepository.class);
        ClientRepository clientRepository = mock(ClientRepository.class);
        EmployeeRepository employeeRepository = mock(EmployeeRepository.class);
        ServiceRepository serviceRepository = mock(ServiceRepository.class);
        UserRepository userRepository = mock(UserRepository.class);

        CreateAppointmentService service = new CreateAppointmentService(
                appointmentRepository,
                clientRepository,
                employeeRepository,
                serviceRepository,
                userRepository
        );

        AppointmentRequestDTO request = new AppointmentRequestDTO();
        request.setAppointmentDatetime(new Date());
        request.setNotes("Test notes");
        request.setClientId(1);
        request.setEmployeeId(2);
        request.setServiceId(3);

        when(clientRepository.findById(1)).thenReturn(Optional.of(mock(com.glamgest.app.domain.model.Client.class)));
        when(employeeRepository.findById(2)).thenReturn(Optional.of(mock(com.glamgest.app.domain.model.Employee.class)));
        when(serviceRepository.findById(3)).thenReturn(Optional.of(mock(com.glamgest.app.domain.model.Service.class)));

        SecurityContextHolder.clearContext();

        assertThrows(ResourceNotFoundException.class, () -> service.execute(request));
    }
}
