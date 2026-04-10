package com.glamgest.app.application.usecase;

import com.glamgest.app.application.dto.ClientResponseDTO;

import java.util.List;

public interface GetAllClientsUseCase {

    List<ClientResponseDTO> execute();
}