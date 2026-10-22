package com.glamgest.app.application.service.appointment;

import com.glamgest.app.application.dto.appointment.AppointmentBatchRequestDTO;
import com.glamgest.app.application.dto.appointment.AppointmentRequestDTO;
import com.glamgest.app.application.dto.appointment.AppointmentResponseDTO;
import com.glamgest.app.application.usecase.appointment.CreateAppointmentUseCase;
import com.glamgest.app.common.exception.OperationNotAllowedException;
import com.glamgest.app.common.exception.ResourceNotFoundException;
import com.glamgest.app.common.validation.DurationRules;
import com.glamgest.app.domain.repository.ServiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class CreateAppointmentBatchService {

    private final CreateAppointmentUseCase createAppointmentUseCase;
    private final ServiceRepository serviceRepository;

    public CreateAppointmentBatchService(CreateAppointmentUseCase createAppointmentUseCase,
                                         ServiceRepository serviceRepository) {
        this.createAppointmentUseCase = createAppointmentUseCase;
        this.serviceRepository = serviceRepository;
    }

    @Transactional
    public List<AppointmentResponseDTO> create(AppointmentBatchRequestDTO request) {
        if (request.serviceIds() == null || request.serviceIds().isEmpty()) {
            throw new OperationNotAllowedException("Debes seleccionar al menos un servicio.");
        }

        List<AppointmentResponseDTO> created = new ArrayList<>();
        Date start = request.appointmentDatetime();
        for (Integer serviceId : request.serviceIds()) {
            var service = serviceRepository.findById(serviceId)
                    .orElseThrow(() -> new ResourceNotFoundException("Service not found with id " + serviceId));
            Integer duration = DurationRules.validate(service.getDurationMinutes());

            AppointmentRequestDTO item = new AppointmentRequestDTO();
            item.setClientId(request.clientId());
            item.setEmployeeId(request.employeeId());
            item.setServiceId(serviceId);
            item.setAppointmentDatetime(start);
            item.setDurationMinutes(duration);
            item.setNotes(request.notes());
            created.add(createAppointmentUseCase.execute(item));

            start = new Date(start.getTime() + duration.longValue() * 60_000L);
        }
        return created;
    }
}
