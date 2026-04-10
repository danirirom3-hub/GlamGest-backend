package com.glamgest.app.application.usecase.service;

import com.glamgest.app.application.dto.service.ServiceRequestDTO;
import com.glamgest.app.application.dto.service.ServiceResponseDTO;

public interface CreateServiceUseCase {

    ServiceResponseDTO execute(ServiceRequestDTO serviceRequestDTO);
}
