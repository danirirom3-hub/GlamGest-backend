package com.glamgest.app.application.usecase;

import com.glamgest.app.application.dto.ServiceResponseDTO;

public interface GetServiceByIdUseCase {

    ServiceResponseDTO execute(Integer id);
}
