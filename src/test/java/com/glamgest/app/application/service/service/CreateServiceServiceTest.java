package com.glamgest.app.application.service.service;

import com.glamgest.app.application.dto.service.ServiceRequestDTO;
import com.glamgest.app.application.dto.service.ServiceResponseDTO;
import com.glamgest.app.common.exception.DuplicateServiceNameException;
import com.glamgest.app.domain.model.Service;
import com.glamgest.app.domain.repository.ServiceRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CreateServiceServiceTest {

    @Test
    void execute_whenServiceNameIsUnique_returnsCreatedServiceResponse() {
        ServiceRepository serviceRepository = mock(ServiceRepository.class);
        CreateServiceService service = new CreateServiceService(serviceRepository);

        ServiceRequestDTO request = new ServiceRequestDTO();
        request.setName("Corte de cabello");
        request.setDescription("Corte de cabello para mujer");
        request.setPrice(20000);
        request.setDurationMinutes(45);

        when(serviceRepository.existsByName("Corte de cabello")).thenReturn(false);

        Service savedService = new Service(7, "Corte de cabello", "Corte de cabello para mujer", 20000, 45, true);
        when(serviceRepository.save(any(Service.class))).thenReturn(savedService);

        ServiceResponseDTO response = service.execute(request);

        assertEquals(7, response.getId());
        assertEquals("Corte de cabello", response.getName());
        assertEquals("Corte de cabello para mujer", response.getDescription());
        assertEquals(20000, response.getPrice());
        assertEquals(45, response.getDurationMinutes());
        assertEquals(true, response.getActive());
    }

    @Test
    void execute_whenServiceNameExists_throwsDuplicateServiceNameException() {
        ServiceRepository serviceRepository = mock(ServiceRepository.class);
        CreateServiceService service = new CreateServiceService(serviceRepository);

        ServiceRequestDTO request = new ServiceRequestDTO();
        request.setName("Corte de cabello");
        request.setDescription("Corte de cabello para mujer");
        request.setPrice(20000);
        request.setDurationMinutes(45);

        when(serviceRepository.existsByName("Corte de cabello")).thenReturn(true);

        assertThrows(DuplicateServiceNameException.class, () -> service.execute(request));
    }
}
