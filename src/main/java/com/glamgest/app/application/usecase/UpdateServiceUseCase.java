package com.glamgest.app.application.usecase;

import com.glamgest.app.application.dto.ServiceResponseDTO;
import com.glamgest.app.application.dto.ServiceUpdateDTO;

public interface UpdateServiceUseCase {

    ServiceResponseDTO execute(Integer id, ServiceUpdateDTO serviceUpdateDTO);
}
