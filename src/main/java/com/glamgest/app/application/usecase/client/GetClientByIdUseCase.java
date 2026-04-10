package com.glamgest.app.application.usecase.client;

import com.glamgest.app.application.dto.client.ClientResponseDTO;

public interface GetClientByIdUseCase {

    ClientResponseDTO execute(Integer id);
}