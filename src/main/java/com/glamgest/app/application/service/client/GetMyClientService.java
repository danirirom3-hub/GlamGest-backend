package com.glamgest.app.application.service.client;

import com.glamgest.app.application.dto.client.ClientResponseDTO;
import com.glamgest.app.application.usecase.client.GetMyClientUseCase;
import com.glamgest.app.common.exception.ResourceNotFoundException;
import com.glamgest.app.domain.model.Client;
import com.glamgest.app.domain.repository.ClientRepository;
import com.glamgest.app.domain.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class GetMyClientService implements GetMyClientUseCase {
    private final UserRepository userRepository;
    private final ClientRepository clientRepository;

    public GetMyClientService(UserRepository userRepository, ClientRepository clientRepository) {
        this.userRepository = userRepository;
        this.clientRepository = clientRepository;
    }

    @Override
    public ClientResponseDTO execute() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Integer userId = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario autenticado no encontrado")).getId();
        Client client = clientRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("El usuario no tiene perfil de cliente"));
        return new ClientResponseDTO(client.getId(), client.getName(), client.getEmail(), client.getPhone(),
                client.getRegistrationDate());
    }
}
