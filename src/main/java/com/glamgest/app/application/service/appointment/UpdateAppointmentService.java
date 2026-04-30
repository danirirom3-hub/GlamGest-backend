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

@Service
public class UpdateAppointmentService implements UpdateAppointmentUseCase {

    private final AppointmentRepository appointmentRepository;
    private final JpaClientRepository jpaClientRepository;
    private final JpaEmployeeRepository jpaEmployeeRepository;
    private final JpaServiceRepository jpaServiceRepository;

    public UpdateAppointmentService(AppointmentRepository appointmentRepository,
                                   JpaClientRepository jpaClientRepository,
                                   JpaEmployeeRepository jpaEmployeeRepository,
                                   JpaServiceRepository jpaServiceRepository) {
        this.appointmentRepository = appointmentRepository;
        this.jpaClientRepository = jpaClientRepository;
        this.jpaEmployeeRepository = jpaEmployeeRepository;
        this.jpaServiceRepository = jpaServiceRepository;
    }

    @Override
    public AppointmentResponseDTO execute(AppointmentUpdateDTO appointmentUpdateDTO) {
        Appointment existingAppointment = appointmentRepository.findById(appointmentUpdateDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id " + appointmentUpdateDTO.getId()));

        // Validate relationships
        if (!jpaClientRepository.existsById(appointmentUpdateDTO.getClientId())) {
            throw new ResourceNotFoundException("Client not found with id " + appointmentUpdateDTO.getClientId());
        }
        if (!jpaEmployeeRepository.existsById(appointmentUpdateDTO.getEmployeeId())) {
            throw new ResourceNotFoundException("Employee not found with id " + appointmentUpdateDTO.getEmployeeId());
        }
        if (!jpaServiceRepository.existsById(appointmentUpdateDTO.getServiceId())) {
            throw new ResourceNotFoundException("Service not found with id " + appointmentUpdateDTO.getServiceId());
        }

        // Update appointment
        Appointment updatedAppointment = new Appointment(
                appointmentUpdateDTO.getId(),
                appointmentUpdateDTO.getAppointmentDatetime(),
                appointmentUpdateDTO.getStatus() != null ? appointmentUpdateDTO.getStatus() : existingAppointment.getStatus(),
                appointmentUpdateDTO.getNotes(),
                appointmentUpdateDTO.getClientId(),
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
}
