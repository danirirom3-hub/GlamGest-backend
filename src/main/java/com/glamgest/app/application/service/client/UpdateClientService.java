package com.glamgest.app.application.service.client;

import com.glamgest.app.application.dto.client.ClientResponseDTO;
import com.glamgest.app.application.dto.client.ClientUpdateDTO;
import com.glamgest.app.application.usecase.client.UpdateClientUseCase;
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