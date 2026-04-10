package com.glamgest.app.application.service.service;

import com.glamgest.app.application.dto.service.ServiceResponseDTO;
import com.glamgest.app.application.usecase.service.GetServiceByIdUseCase;
import com.glamgest.app.common.exception.ServiceNotFoundException;
import com.glamgest.app.domain.model.Service;
import com.glamgest.app.domain.repository.ServiceRepository;

@org.springframework.stereotype.Service
public class GetServiceByIdService implements GetServiceByIdUseCase {

    private final ServiceRepository serviceRepository;

    public GetServiceByIdService(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    @Override
    public ServiceResponseDTO execute(Integer id) {
        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new ServiceNotFoundException("Service not found with id: " + id));

        ServiceResponseDTO dto = new ServiceResponseDTO();
        dto.setId(service.getId());
        dto.setName(service.getName());
        dto.setDescription(service.getDescription());
        dto.setPrice(service.getPrice());
        dto.setDurationMinutes(service.getDurationMinutes());
        dto.setActive(service.getActive());
        return dto;
    }
}
    