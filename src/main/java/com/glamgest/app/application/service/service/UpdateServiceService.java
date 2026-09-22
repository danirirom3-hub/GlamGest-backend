package com.glamgest.app.application.service.service;

import com.glamgest.app.application.dto.service.ServiceResponseDTO;
import com.glamgest.app.application.dto.service.ServiceUpdateDTO;
import com.glamgest.app.application.usecase.service.UpdateServiceUseCase;
import com.glamgest.app.common.exception.DuplicateServiceNameException;
import com.glamgest.app.common.exception.ServiceNotFoundException;
import com.glamgest.app.domain.model.Service;
import com.glamgest.app.domain.repository.ServiceRepository;
import com.glamgest.app.domain.repository.CategoryRepository;
import com.glamgest.app.common.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import com.glamgest.app.common.validation.DurationRules;

@org.springframework.stereotype.Service
public class UpdateServiceService implements UpdateServiceUseCase {

    private final ServiceRepository serviceRepository;
    private final CategoryRepository categoryRepository;

    @Autowired
    public UpdateServiceService(ServiceRepository serviceRepository, CategoryRepository categoryRepository) {
        this.serviceRepository = serviceRepository;
        this.categoryRepository = categoryRepository;
    }

    public UpdateServiceService(ServiceRepository serviceRepository) {
        this(serviceRepository, null);
    }

    @Override
    public ServiceResponseDTO execute(Integer id, ServiceUpdateDTO serviceUpdateDTO) {
        Service existingService = serviceRepository.findByIdIncludingInactive(id)
                .orElseThrow(() -> new ServiceNotFoundException("Service not found with id: " + id));

        if (serviceUpdateDTO.getName() != null && !serviceUpdateDTO.getName().equals(existingService.getName())) {
            Service sameName = serviceRepository.findByNameIncludingInactive(serviceUpdateDTO.getName()).orElse(null);
            if (sameName != null && !sameName.getId().equals(id)) {
                throw new DuplicateServiceNameException("Ya existe un servicio con ese nombre.");
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
            DurationRules.validate(serviceUpdateDTO.getDurationMinutes());
            existingService.setDurationMinutes(serviceUpdateDTO.getDurationMinutes());
        }

        if (serviceUpdateDTO.isCategoryIdSet() && serviceUpdateDTO.getCategoryId() != null) {
            if (categoryRepository == null) {
                throw new ResourceNotFoundException("Category repository is not available");
            }
            existingService.setCategoryId(categoryRepository.findById(serviceUpdateDTO.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + serviceUpdateDTO.getCategoryId()))
                    .getId());
        } else if (serviceUpdateDTO.isCategoryIdSet()) {
            existingService.setCategoryId(null);
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
        response.setCategoryId(updatedService.getCategoryId());
        response.setCategoryName(updatedService.getCategoryName());

        return response;
    }
}
