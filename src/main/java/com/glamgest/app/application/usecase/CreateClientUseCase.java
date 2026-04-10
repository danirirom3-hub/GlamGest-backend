package com.glamgest.app.application.usecase;

import com.glamgest.app.application.dto.ClientRequestDTO;
import com.glamgest.app.application.dto.ClientResponseDTO;

public interface CreateClientUseCase {

    ClientResponseDTO execute(ClientRequestDTO clientRequestDTO);
}