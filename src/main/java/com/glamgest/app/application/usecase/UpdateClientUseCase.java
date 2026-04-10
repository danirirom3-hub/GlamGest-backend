package com.glamgest.app.application.usecase;

import com.glamgest.app.application.dto.ClientResponseDTO;
import com.glamgest.app.application.dto.ClientUpdateDTO;

public interface UpdateClientUseCase {

    ClientResponseDTO execute(ClientUpdateDTO clientUpdateDTO);
}