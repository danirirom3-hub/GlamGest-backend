package com.glamgest.app.application.usecase.client;

import com.glamgest.app.application.dto.client.ClientRequestDTO;
import com.glamgest.app.application.dto.client.ClientResponseDTO;

public interface CreateClientUseCase {

    ClientResponseDTO execute(ClientRequestDTO clientRequestDTO);
}