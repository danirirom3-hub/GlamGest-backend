package com.glamgest.app.application.usecase.client;

import com.glamgest.app.application.dto.client.ClientResponseDTO;
import com.glamgest.app.application.dto.client.ClientUpdateDTO;

public interface UpdateClientUseCase {

    ClientResponseDTO execute(ClientUpdateDTO clientUpdateDTO);
}