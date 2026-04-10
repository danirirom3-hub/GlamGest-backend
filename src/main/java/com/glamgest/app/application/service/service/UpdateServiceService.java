package com.glamgest.app.application.service.service;

import com.glamgest.app.application.dto.service.ServiceResponseDTO;
import com.glamgest.app.application.dto.service.ServiceUpdateDTO;
import com.glamgest.app.application.usecase.service.UpdateServiceUseCase;
import com.glamgest.app.common.exception.DuplicateServiceNameException;
import com.glamgest.app.common.exception.ServiceNotFoundException;
import com.glamgest.app.domain.model.Service;
import com.glamgest.app.domain.repository.ServiceRepository;

@org.springframework.stereotype.Service
public class UpdateServiceService implements UpdateServiceUseCase {

    private final ServiceRepository serviceRepository;

    public UpdateServiceService(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    @Override
    public ServiceResponseDTO execute(Integer id, ServiceUpdateDTO serviceUpdateDTO) {
        Service existingService = serviceRepository.findById(id)
                .orElseThrow(() -> new ServiceNotFoundException("Service not found with id: " + id));

        if (serviceUpdateDTO.getName() != null && !serviceUpdateDTO.getName().equals(existingService.getName())) {
            if (serviceRepository.existsByName(serviceUpdateDTO.getName())) {
                throw new DuplicateServiceNameException("Service name already exists: " + serviceUpdateDTO.getName());
            }
            existingService.setName(serviceUpdateDTO.getName());
        }

        if (serviceUpdateDTO.getDescription() != null) {
            existingService.setDescription(serviceUpdateDTO.getDescription());
        }

        if (serviceUpdateDTO.getPrice() != null) {
            existingService.setPrice(serviceUpdateDTO.getPrice());
        }

        if (serviceUpdateDTO.getDurationMinutes() != null) {
            existingService.setDurationMinutes(serviceUpdateDTO.getDurationMinutes());
        }

        if (serviceUpdateDTO.getActive() != null) {
            existingService.setActive(serviceUpdateDTO.getActive());
        }

        Service updatedService = serviceRepository.save(existingService);

        ServiceResponseDTO response = new ServiceResponseDTO();
        response.setId(updatedService.getId());
        response.setName(updatedService.getName());
        response.setDescription(updatedService.getDescription());
        response.setPrice(updatedService.getPrice());
        response.setDurationMinutes(updatedService.getDurationMinutes());
        response.setActive(updatedService.getActive());

        return response;
    }
}