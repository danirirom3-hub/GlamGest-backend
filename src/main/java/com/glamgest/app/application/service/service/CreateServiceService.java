package com.glamgest.app.application.service.service;

import com.glamgest.app.application.dto.service.ServiceRequestDTO;
import com.glamgest.app.application.dto.service.ServiceResponseDTO;
import com.glamgest.app.application.usecase.service.CreateServiceUseCase;
import com.glamgest.app.common.exception.DuplicateServiceNameException;
import com.glamgest.app.domain.model.Service;
import com.glamgest.app.domain.repository.ServiceRepository;
import com.glamgest.app.domain.repository.CategoryRepository;
import com.glamgest.app.common.exception.ResourceNotFoundException;
import com.glamgest.app.common.validation.DurationRules;
import org.springframework.beans.factory.annotation.Autowired;


@org.springframework.stereotype.Service
public class CreateServiceService implements CreateServiceUseCase {

    private final ServiceRepository serviceRepository;
    private final CategoryRepository categoryRepository;

    @Autowired
    public CreateServiceService(ServiceRepository serviceRepository, CategoryRepository categoryRepository) {
        this.serviceRepository = serviceRepository;
        this.categoryRepository = categoryRepository;
    }

    public CreateServiceService(ServiceRepository serviceRepository) {
        this(serviceRepository, null);
    }

    @Override
    public ServiceResponseDTO execute(ServiceRequestDTO serviceRequestDTO) {
        DurationRules.validate(serviceRequestDTO.getDurationMinutes());
        if (serviceRepository.existsByName(serviceRequestDTO.getName())) {
            throw new DuplicateServiceNameException("Ya existe un servicio activo con ese nombre.");
        }
        Service service = serviceRepository.findByNameIncludingInactive(serviceRequestDTO.getName()).orElse(null);
        if (service != null && Boolean.TRUE.equals(service.getActive())) {
            throw new DuplicateServiceNameException("Ya existe un servicio activo con ese nombre.");
        }
        if (service == null) {
            service = new Service();
        }
        service.setName(serviceRequestDTO.getName());
        service.setDescription(serviceRequestDTO.getDescription());
        service.setPrice(serviceRequestDTO.getPrice());
        service.setDurationMinutes(serviceRequestDTO.getDurationMinutes());
        service.setActive(true);
        if (serviceRequestDTO.getCategoryId() != null) {
            if (categoryRepository == null) {
                throw new ResourceNotFoundException("Category repository is not available");
            }
            service.setCategoryId(categoryRepository.findById(serviceRequestDTO.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + serviceRequestDTO.getCategoryId()))
                    .getId());
        }

        Service savedService = serviceRepository.save(service);

        ServiceResponseDTO response = new ServiceResponseDTO();
        response.setId(savedService.getId());
        response.setName(savedService.getName());
        response.setDescription(savedService.getDescription());
        response.setPrice(savedService.getPrice());
        response.setDurationMinutes(savedService.getDurationMinutes());
        response.setActive(savedService.getActive());
        response.setCategoryId(savedService.getCategoryId());
        response.setCategoryName(savedService.getCategoryName());

        return response;
    }
}
