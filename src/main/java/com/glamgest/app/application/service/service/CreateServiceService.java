package com.glamgest.app.application.service.service;

import com.glamgest.app.application.dto.service.ServiceRequestDTO;
import com.glamgest.app.application.dto.service.ServiceResponseDTO;
import com.glamgest.app.application.usecase.service.CreateServiceUseCase;
import com.glamgest.app.common.exception.DuplicateServiceNameException;
import com.glamgest.app.domain.model.Service;
import com.glamgest.app.domain.repository.ServiceRepository;


@org.springframework.stereotype.Service
public class CreateServiceService implements CreateServiceUseCase {

    private final ServiceRepository serviceRepository;

    public CreateServiceService(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    @Override
    public ServiceResponseDTO execute(ServiceRequestDTO serviceRequestDTO) {
        if (serviceRepository.existsByName(serviceRequestDTO.getName())) {
            throw new DuplicateServiceNameException("Service name already exists: " + serviceRequestDTO.getName());
        }

        Service service = new Service();
        service.setName(serviceRequestDTO.getName());
        service.setDescription(serviceRequestDTO.getDescription());
        service.setPrice(serviceRequestDTO.getPrice());
        service.setDurationMinutes(serviceRequestDTO.getDurationMinutes());
        service.setActive(true);

        Service savedService = serviceRepository.save(service);

        ServiceResponseDTO response = new ServiceResponseDTO();
        response.setId(savedService.getId());
        response.setName(savedService.getName());
        response.setDescription(savedService.getDescription());
        response.setPrice(savedService.getPrice());
        response.setDurationMinutes(savedService.getDurationMinutes());
        response.setActive(savedService.getActive());

        return response;
    }
}
