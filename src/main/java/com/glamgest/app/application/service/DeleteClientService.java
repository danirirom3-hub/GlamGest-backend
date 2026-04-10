package com.glamgest.app.application.service;

import com.glamgest.app.application.usecase.DeleteClientUseCase;
import com.glamgest.app.common.exception.ResourceNotFoundException;
import com.glamgest.app.domain.repository.ClientRepository;
import org.springframework.stereotype.Service;

@Service
public class DeleteClientService implements DeleteClientUseCase {

    private final ClientRepository clientRepository;

    public DeleteClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public void execute(Integer id) {
        if (clientRepository.findById(id).isEmpty()) {
            throw new ResourceNotFoundException("Client not found with id " + id);
        }
        clientRepository.deleteById(id);
    }
}