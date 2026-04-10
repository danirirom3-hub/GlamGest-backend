package com.glamgest.app.application.service.client;

import com.glamgest.app.application.dto.client.ClientResponseDTO;
import com.glamgest.app.application.usecase.client.GetClientByIdUseCase;
import com.glamgest.app.common.exception.ResourceNotFoundException;
import com.glamgest.app.domain.model.Client;
import com.glamgest.app.domain.repository.ClientRepository;
import org.springframework.stereotype.Service;

@Service
public class GetClientByIdService implements GetClientByIdUseCase {

    private final ClientRepository clientRepository;

    public GetClientByIdService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public ClientResponseDTO execute(Integer id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id " + id));

        return new ClientResponseDTO(
                client.getId(),
                client.getName(),
                client.getEmail(),
                client.getPhone(),
                client.getRegistrationDate()
        );
    }
}