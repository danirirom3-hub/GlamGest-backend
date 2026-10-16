package com.glamgest.app.application.usecase.client;

import java.util.List;

import com.glamgest.app.application.dto.client.ClientResponseDTO;
import com.glamgest.app.application.dto.client.ClientPageResponseDTO;

public interface GetAllClientsUseCase {

    List<ClientResponseDTO> execute();

    ClientPageResponseDTO execute(String search, String sort, int page, int size);
}
