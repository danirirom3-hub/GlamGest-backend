package com.glamgest.app.application.usecase;

import com.glamgest.app.application.dto.ClientResponseDTO;

public interface GetClientByIdUseCase {

    ClientResponseDTO execute(Integer id);
}