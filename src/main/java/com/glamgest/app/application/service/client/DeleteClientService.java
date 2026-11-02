package com.glamgest.app.application.service.client;

import com.glamgest.app.application.usecase.client.DeleteClientUseCase;
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
    public String execute(Integer id) {
        var client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id " + id));
        if (client.isSystemClient()) {
            throw new com.glamgest.app.common.exception.OperationNotAllowedException(
                    "El cliente Consumidor final no puede eliminarse.");
        }
        clientRepository.deleteById(id);
        return client.getName();
    }
}
