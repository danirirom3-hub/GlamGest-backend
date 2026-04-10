package com.glamgest.app.application.service;

import com.glamgest.app.application.dto.ClientResponseDTO;
import com.glamgest.app.application.dto.ClientUpdateDTO;
import com.glamgest.app.application.usecase.UpdateClientUseCase;
import com.glamgest.app.common.exception.DuplicateClientEmailException;
import com.glamgest.app.common.exception.DuplicateClientPhoneException;
import com.glamgest.app.common.exception.ResourceNotFoundException;
import com.glamgest.app.domain.model.Client;
import com.glamgest.app.domain.repository.ClientRepository;
import org.springframework.stereotype.Service;

@Service
public class UpdateClientService implements UpdateClientUseCase {

    private final ClientRepository clientRepository;

    public UpdateClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public ClientResponseDTO execute(ClientUpdateDTO clientUpdateDTO) {
        Client existingClient = clientRepository.findById(clientUpdateDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id " + clientUpdateDTO.getId()));

        if (!existingClient.getEmail().equals(clientUpdateDTO.getEmail())
                && clientRepository.existsByEmail(clientUpdateDTO.getEmail())) {
            throw new DuplicateClientEmailException("Client email already exists: " + clientUpdateDTO.getEmail());
        }

        if (!existingClient.getPhone().equals(clientUpdateDTO.getPhone())
                && clientRepository.existsByPhone(clientUpdateDTO.getPhone())) {
            throw new DuplicateClientPhoneException("Client phone already exists: " + clientUpdateDTO.getPhone());
        }

        Client clientToUpdate = new Client(
                clientUpdateDTO.getId(),
                clientUpdateDTO.getName(),
                clientUpdateDTO.getEmail(),
                clientUpdateDTO.getPhone(),
                existingClient.getRegistrationDate()
        );

        Client updatedClient = clientRepository.save(clientToUpdate);

        return new ClientResponseDTO(
                updatedClient.getId(),
                updatedClient.getName(),
                updatedClient.getEmail(),
                updatedClient.getPhone(),
                updatedClient.getRegistrationDate()
        );
    }
}