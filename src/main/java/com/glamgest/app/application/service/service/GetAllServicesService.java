package com.glamgest.app.application.service.service;

import com.glamgest.app.application.dto.service.ServiceResponseDTO;
import com.glamgest.app.application.usecase.service.GetAllServicesUseCase;
import com.glamgest.app.domain.model.Service;
import com.glamgest.app.domain.repository.ServiceRepository;

import java.util.List;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
public class GetAllServicesService implements GetAllServicesUseCase {

    private final ServiceRepository serviceRepository;

    public GetAllServicesService(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    @Override
    public List<ServiceResponseDTO> execute() {
        List<Service> services = serviceRepository.findAll();
        return services.stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    private ServiceResponseDTO toResponseDTO(Service service) {
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
