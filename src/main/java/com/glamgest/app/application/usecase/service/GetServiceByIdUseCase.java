package com.glamgest.app.application.usecase.service;

import com.glamgest.app.application.dto.service.ServiceResponseDTO;

public interface GetServiceByIdUseCase {

    ServiceResponseDTO execute(Integer id);
}
