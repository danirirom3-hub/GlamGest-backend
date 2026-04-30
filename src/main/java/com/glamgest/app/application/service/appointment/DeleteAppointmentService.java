package com.glamgest.app.application.service.appointment;

import com.glamgest.app.application.usecase.appointment.DeleteAppointmentUseCase;
import com.glamgest.app.common.exception.ResourceNotFoundException;
import com.glamgest.app.domain.repository.AppointmentRepository;
import org.springframework.stereotype.Service;

@Service
public class DeleteAppointmentService implements DeleteAppointmentUseCase {

    private final AppointmentRepository appointmentRepository;

    public DeleteAppointmentService(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    @Override
    public void execute(Integer id) {
        if (!appointmentRepository.findById(id).isPresent()) {
            throw new ResourceNotFoundException("Appointment not found with id " + id);
        }
        appointmentRepository.deleteById(id);
    }
}
