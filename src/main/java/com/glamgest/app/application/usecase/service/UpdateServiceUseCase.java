package com.glamgest.app.application.usecase.service;

import com.glamgest.app.application.dto.service.ServiceResponseDTO;
import com.glamgest.app.application.dto.service.ServiceUpdateDTO;

public interface UpdateServiceUseCase {

    ServiceResponseDTO execute(Integer id, ServiceUpdateDTO serviceUpdateDTO);
}
