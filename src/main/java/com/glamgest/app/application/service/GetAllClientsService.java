package com.glamgest.app.application.service;

import com.glamgest.app.application.dto.ClientResponseDTO;
import com.glamgest.app.application.usecase.GetAllClientsUseCase;
import com.glamgest.app.domain.model.Client;
import com.glamgest.app.domain.repository.ClientRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GetAllClientsService implements GetAllClientsUseCase {

    private final ClientRepository clientRepository;

    public GetAllClientsService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public List<ClientResponseDTO> execute() {
        return clientRepository.findAll().stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    private ClientResponseDTO toResponseDTO(Client client) {
        return new ClientResponseDTO(
                client.getId(),
                client.getName(),
                client.getEmail(),
                client.getPhone(),
                client.getRegistrationDate()
        );
    }
}