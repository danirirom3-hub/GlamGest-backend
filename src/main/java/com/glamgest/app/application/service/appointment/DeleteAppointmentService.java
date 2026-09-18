package com.glamgest.app.application.service.appointment;

import com.glamgest.app.application.usecase.appointment.DeleteAppointmentUseCase;
import com.glamgest.app.common.exception.ResourceNotFoundException;
import com.glamgest.app.domain.repository.AppointmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import com.glamgest.app.domain.repository.ClientRepository;
import com.glamgest.app.domain.repository.UserRepository;

@Service
public class DeleteAppointmentService implements DeleteAppointmentUseCase {

    private final AppointmentRepository appointmentRepository;
    private final ClientRepository clientRepository;
    private final UserRepository userRepository;

    public DeleteAppointmentService(AppointmentRepository appointmentRepository, ClientRepository clientRepository,
                                    UserRepository userRepository) {
        this.appointmentRepository = appointmentRepository;
        this.clientRepository = clientRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void execute(Integer id) {
        var appointment = appointmentRepository.findById(id);
        if (appointment.isEmpty()) {
            throw new ResourceNotFoundException("Appointment not found with id " + id);
        }
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getAuthorities().stream().anyMatch(a -> "CLIENT".equals(a.getAuthority()))) {
            Integer userId = userRepository.findByEmail(authentication.getName()).orElseThrow().getId();
            boolean owner = clientRepository.findByUserId(userId)
                    .map(client -> client.getId().equals(appointment.get().getClientId())).orElse(false);
            if (!owner) {
                throw new AccessDeniedException("No puede eliminar esta cita");
            }
        }
        appointmentRepository.deleteById(id);
    }
}
