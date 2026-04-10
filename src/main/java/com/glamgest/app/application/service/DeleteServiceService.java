package com.glamgest.app.application.service;

import com.glamgest.app.application.usecase.DeleteServiceUseCase;
import com.glamgest.app.common.exception.ServiceNotFoundException;
import com.glamgest.app.domain.repository.ServiceRepository;
import org.springframework.stereotype.Service;

@Service
public class DeleteServiceService implements DeleteServiceUseCase {

    private final ServiceRepository serviceRepository;

    public DeleteServiceService(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    @Override
    public void execute(Integer id) {
        if (!serviceRepository.findById(id).isPresent()) {
            throw new ServiceNotFoundException("Service not found with id: " + id);
        }
        serviceRepository.deleteById(id);
    }
}
