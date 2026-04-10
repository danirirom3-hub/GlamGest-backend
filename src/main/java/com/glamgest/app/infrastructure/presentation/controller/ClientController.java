package com.glamgest.app.infrastructure.presentation.controller;

import com.glamgest.app.application.dto.ClientRequestDTO;
import com.glamgest.app.application.dto.ClientResponseDTO;
import com.glamgest.app.application.dto.ClientUpdateDTO;
import com.glamgest.app.application.usecase.CreateClientUseCase;
import com.glamgest.app.application.usecase.DeleteClientUseCase;
import com.glamgest.app.application.usecase.GetAllClientsUseCase;
import com.glamgest.app.application.usecase.GetClientByIdUseCase;
import com.glamgest.app.application.usecase.UpdateClientUseCase;
import com.glamgest.app.infrastructure.presentation.helper.BuilderHelper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    private final CreateClientUseCase createClientUseCase;
    private final GetAllClientsUseCase getAllClientsUseCase;
    private final GetClientByIdUseCase getClientByIdUseCase;
    private final UpdateClientUseCase updateClientUseCase;
    private final DeleteClientUseCase deleteClientUseCase;

    public ClientController(CreateClientUseCase createClientUseCase,
                            GetAllClientsUseCase getAllClientsUseCase,
                            GetClientByIdUseCase getClientByIdUseCase,
                            UpdateClientUseCase updateClientUseCase,
                            DeleteClientUseCase deleteClientUseCase) {
        this.createClientUseCase = createClientUseCase;
        this.getAllClientsUseCase = getAllClientsUseCase;
        this.getClientByIdUseCase = getClientByIdUseCase;
        this.updateClientUseCase = updateClientUseCase;
        this.deleteClientUseCase = deleteClientUseCase;
    }

    @PostMapping
    public ResponseEntity<?> createClient(@Valid @RequestBody ClientRequestDTO clientRequestDTO,
                                          BindingResult result) {
        if (result.hasFieldErrors()) {
            return validation(result);
        }

        ClientResponseDTO createdClient = createClientUseCase.execute(clientRequestDTO);
        return BuilderHelper.buildResponse(createdClient, "Cliente creado", HttpStatus.CREATED, true);
    }

    @GetMapping
    public ResponseEntity<?> getAllClients() {
        List<ClientResponseDTO> clients = getAllClientsUseCase.execute();
        return BuilderHelper.buildResponse(clients, "Clientes obtenidos", HttpStatus.OK, true);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getClientById(@PathVariable Integer id) {
        ClientResponseDTO client = getClientByIdUseCase.execute(id);
        return BuilderHelper.buildResponse(client, "Cliente obtenido", HttpStatus.OK, true);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateClient(@PathVariable Integer id,
                                          @Valid @RequestBody ClientUpdateDTO clientUpdateDTO,
                                          BindingResult result) {
        if (result.hasFieldErrors()) {
            return validation(result);
        }

        clientUpdateDTO.setId(id);
        ClientResponseDTO updatedClient = updateClientUseCase.execute(clientUpdateDTO);
        return BuilderHelper.buildResponse(updatedClient, "Cliente actualizado", HttpStatus.OK, true);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteClient(@PathVariable Integer id) {
        deleteClientUseCase.execute(id);
        return BuilderHelper.buildResponse(null, "Cliente eliminado", HttpStatus.NO_CONTENT, true);
    }

    private ResponseEntity<?> validation(BindingResult result) {
        Map<String, Object> errors = new HashMap<>();
        result.getFieldErrors().forEach(error -> errors.put(error.getField(),
                "El campo " + error.getField() + " " + error.getDefaultMessage()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
}