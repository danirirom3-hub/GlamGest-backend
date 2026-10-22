package com.glamgest.app.application.service.appointment;

import com.glamgest.app.application.dto.appointment.AppointmentBatchRequestDTO;
import com.glamgest.app.application.dto.appointment.AppointmentRequestDTO;
import com.glamgest.app.application.dto.appointment.AppointmentResponseDTO;
import com.glamgest.app.application.usecase.appointment.CreateAppointmentUseCase;
import com.glamgest.app.domain.model.Service;
import com.glamgest.app.domain.repository.ServiceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateAppointmentBatchServiceTest {

    @Mock private CreateAppointmentUseCase createAppointmentUseCase;
    @Mock private ServiceRepository serviceRepository;

    @Test
    void createsOneAppointmentPerServiceInConsecutiveDurationBasedSlots() {
        when(serviceRepository.findById(10)).thenReturn(Optional.of(new Service(10, "Corte", "", 20000, 30, true)));
        when(serviceRepository.findById(11)).thenReturn(Optional.of(new Service(11, "Peinado", "", 30000, 45, true)));
        when(createAppointmentUseCase.execute(any(AppointmentRequestDTO.class))).thenAnswer(invocation -> {
            AppointmentRequestDTO request = invocation.getArgument(0);
            AppointmentResponseDTO response = new AppointmentResponseDTO();
            response.setAppointmentDatetime(request.getAppointmentDatetime());
            response.setServiceId(request.getServiceId());
            return response;
        });
        CreateAppointmentBatchService batchService = new CreateAppointmentBatchService(createAppointmentUseCase, serviceRepository);
        Date start = Date.from(LocalDateTime.of(2030, 1, 7, 9, 0)
                .atZone(ZoneId.of("America/Bogota")).toInstant());

        List<AppointmentResponseDTO> result = batchService.create(
                new AppointmentBatchRequestDTO(5, start, 3, List.of(10, 11), "Reserva combinada"));

        ArgumentCaptor<AppointmentRequestDTO> requests = ArgumentCaptor.forClass(AppointmentRequestDTO.class);
        verify(createAppointmentUseCase, times(2)).execute(requests.capture());
        assertEquals(2, result.size());
        assertEquals(30 * 60_000L, requests.getAllValues().get(1).getAppointmentDatetime().getTime()
                - requests.getAllValues().get(0).getAppointmentDatetime().getTime());
        assertEquals(List.of(10, 11), requests.getAllValues().stream().map(AppointmentRequestDTO::getServiceId).toList());
    }
}
