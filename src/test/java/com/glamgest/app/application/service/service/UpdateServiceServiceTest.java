package com.glamgest.app.application.service.service;

import com.glamgest.app.application.dto.service.ServiceResponseDTO;
import com.glamgest.app.application.dto.service.ServiceUpdateDTO;
import com.glamgest.app.domain.model.Service;
import com.glamgest.app.domain.repository.ServiceRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UpdateServiceServiceTest {

    @Test
    void execute_whenInactiveServiceIsActivated_updatesExistingRecord() {
        ServiceRepository repository = mock(ServiceRepository.class);
        UpdateServiceService service = new UpdateServiceService(repository);
        Service inactive = new Service(8, "Cepillado", "Antiguo", 40000, 60, false);
        ServiceUpdateDTO request = new ServiceUpdateDTO();
        request.setActive(true);

        when(repository.findByIdIncludingInactive(8)).thenReturn(Optional.of(inactive));
        when(repository.save(any(Service.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ServiceResponseDTO response = service.execute(8, request);

        assertEquals(8, response.getId());
        assertEquals(true, response.getActive());
        assertEquals(60, response.getDurationMinutes());
    }

    @Test
    void execute_whenCategoryIsExplicitlyNull_clearsCategoryWithoutLookup() {
        ServiceRepository repository = mock(ServiceRepository.class);
        UpdateServiceService service = new UpdateServiceService(repository);
        Service existing = new Service(8, "Cepillado", "Actual", 40000, 60, true, 3, "Cabello");
        ServiceUpdateDTO request = new ServiceUpdateDTO();
        request.setCategoryId(null);

        when(repository.findByIdIncludingInactive(8)).thenReturn(Optional.of(existing));
        when(repository.save(any(Service.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ServiceResponseDTO response = service.execute(8, request);

        assertEquals(null, response.getCategoryId());
    }
}
