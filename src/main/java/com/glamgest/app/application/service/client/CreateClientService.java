package com.glamgest.app.application.service.client;

import com.glamgest.app.application.dto.client.ClientRequestDTO;
import com.glamgest.app.application.dto.client.ClientResponseDTO;
import com.glamgest.app.application.usecase.client.CreateClientUseCase;
import com.glamgest.app.common.exception.DuplicateClientEmailException;
import com.glamgest.app.common.exception.DuplicateClientPhoneException;
import com.glamgest.app.domain.model.Client;
import com.glamgest.app.domain.repository.ClientRepository;
import com.glamgest.app.domain.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

@Service
public class CreateClientService implements CreateClientUseCase {

    private static final Logger logger = LoggerFactory.getLogger(CreateClientService.class);

    private final ClientRepository clientRepository;
    private final UserRepository userRepository;

    public CreateClientService(ClientRepository clientRepository) {
        this(clientRepository, null);
    }

    @Autowired
    public CreateClientService(ClientRepository clientRepository, UserRepository userRepository) {
        this.clientRepository = clientRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ClientResponseDTO execute(ClientRequestDTO clientRequestDTO) {
        logger.info("Creando cliente: nombre={}, email={}", clientRequestDTO.getName(), clientRequestDTO.getEmail());

        if (clientRepository.existsByEmail(clientRequestDTO.getEmail())) {
            logger.warn("Intento de crear cliente con email duplicado: {}", clientRequestDTO.getEmail());
            throw new DuplicateClientEmailException("Client email already exists: " + clientRequestDTO.getEmail());
        }

        if (clientRepository.existsByPhone(clientRequestDTO.getPhone())) {
            logger.warn("Intento de crear cliente con teléfono duplicado: {}", clientRequestDTO.getPhone());
            throw new DuplicateClientPhoneException("Client phone already exists: " + clientRequestDTO.getPhone());
        }

        Integer userId = null;
        if (userRepository != null) {
            var existingUser = userRepository.findByEmail(clientRequestDTO.getEmail()).orElse(null);
            if (existingUser != null) {
                if (!"CLIENT".equals(existingUser.getRoleName())) {
                    throw new DuplicateClientEmailException("El email pertenece a un usuario interno");
                }
                userId = existingUser.getId();
            }
        }

        Client client = new Client(
                null,
                clientRequestDTO.getName(),
                clientRequestDTO.getEmail(),
                clientRequestDTO.getPhone(),
                new Date(), userId
        );

        Client savedClient = clientRepository.save(client);

        logger.info("Cliente creado exitosamente: ID={}, nombre={}", savedClient.getId(), savedClient.getName());

        return new ClientResponseDTO(
                savedClient.getId(),
                savedClient.getName(),
                savedClient.getEmail(),
                savedClient.getPhone(),
                savedClient.getRegistrationDate()
        );
    }
}
