package com.glamgest.app.application.service.client;

import com.glamgest.app.application.dto.client.ClientRequestDTO;
import com.glamgest.app.application.dto.client.ClientResponseDTO;
import com.glamgest.app.common.exception.DuplicateClientEmailException;
import com.glamgest.app.domain.model.Client;
import com.glamgest.app.domain.repository.ClientRepository;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CreateClientServiceTest {

    @Test
    void execute_whenClientIsNew_returnsCreatedClientResponse() {
        ClientRepository clientRepository = mock(ClientRepository.class);
        CreateClientService service = new CreateClientService(clientRepository);

        ClientRequestDTO request = new ClientRequestDTO();
        request.setName("Juan Perez");
        request.setEmail("juan@example.com");
        request.setPhone("+1234567890");

        when(clientRepository.existsByEmail("juan@example.com")).thenReturn(false);
        when(clientRepository.existsByPhone("+1234567890")).thenReturn(false);

        Client savedClient = new Client(42, "Juan Perez", "juan@example.com", "+1234567890", new Date());
        when(clientRepository.save(any(Client.class))).thenReturn(savedClient);

        ClientResponseDTO response = service.execute(request);

        assertEquals(42, response.getId());
        assertEquals("Juan Perez", response.getName());
        assertEquals("juan@example.com", response.getEmail());
        assertEquals("+1234567890", response.getPhone());
        assertEquals(savedClient.getRegistrationDate(), response.getRegistrationDate());
    }

    @Test
    void execute_whenEmailAlreadyExists_throwsDuplicateClientEmailException() {
        ClientRepository clientRepository = mock(ClientRepository.class);
        CreateClientService service = new CreateClientService(clientRepository);

        ClientRequestDTO request = new ClientRequestDTO();
        request.setName("Juan Perez");
        request.setEmail("juan@example.com");
        request.setPhone("+1234567890");

        when(clientRepository.existsByEmail("juan@example.com")).thenReturn(true);

        assertThrows(DuplicateClientEmailException.class, () -> service.execute(request));
    }
}
