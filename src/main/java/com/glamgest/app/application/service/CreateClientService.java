package com.glamgest.app.application.service;

import com.glamgest.app.application.dto.ClientRequestDTO;
import com.glamgest.app.application.dto.ClientResponseDTO;
import com.glamgest.app.application.usecase.CreateClientUseCase;
import com.glamgest.app.common.exception.DuplicateClientEmailException;
import com.glamgest.app.common.exception.DuplicateClientPhoneException;
import com.glamgest.app.domain.model.Client;
import com.glamgest.app.domain.repository.ClientRepository;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class CreateClientService implements CreateClientUseCase {

    private final ClientRepository clientRepository;

    public CreateClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public ClientResponseDTO execute(ClientRequestDTO clientRequestDTO) {
        if (clientRepository.existsByEmail(clientRequestDTO.getEmail())) {
            throw new DuplicateClientEmailException("Client email already exists: " + clientRequestDTO.getEmail());
        }

        if (clientRepository.existsByPhone(clientRequestDTO.getPhone())) {
            throw new DuplicateClientPhoneException("Client phone already exists: " + clientRequestDTO.getPhone());
        }

        Client client = new Client(
                null,
                clientRequestDTO.getName(),
                clientRequestDTO.getEmail(),
                clientRequestDTO.getPhone(),
                new Date()
        );

        Client savedClient = clientRepository.save(client);

        return new ClientResponseDTO(
                savedClient.getId(),
                savedClient.getName(),
                savedClient.getEmail(),
                savedClient.getPhone(),
                savedClient.getRegistrationDate()
        );
    }
}