package com.glamgest.app.application.usecase.service;

import java.util.List;

import com.glamgest.app.application.dto.service.ServiceResponseDTO;

public interface GetAllServicesUseCase {

    List<ServiceResponseDTO> execute();
}
