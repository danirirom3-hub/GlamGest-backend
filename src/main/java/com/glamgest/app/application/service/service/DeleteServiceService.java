package com.glamgest.app.application.service.service;

import com.glamgest.app.application.usecase.service.DeleteServiceUseCase;
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
    public String execute(Integer id) {
        var service = serviceRepository.findById(id)
                .orElseThrow(() -> new ServiceNotFoundException("Service not found with id: " + id));
        serviceRepository.deleteById(id);
        return service.getName();
    }
}
