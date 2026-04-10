package com.glamgest.app.application.usecase;

import com.glamgest.app.application.dto.ServiceRequestDTO;
import com.glamgest.app.application.dto.ServiceResponseDTO;

public interface CreateServiceUseCase {

    ServiceResponseDTO execute(ServiceRequestDTO serviceRequestDTO);
}
