package com.glamgest.app.application.usecase.client;

import java.util.List;

import com.glamgest.app.application.dto.client.ClientResponseDTO;

public interface GetAllClientsUseCase {

    List<ClientResponseDTO> execute();
}