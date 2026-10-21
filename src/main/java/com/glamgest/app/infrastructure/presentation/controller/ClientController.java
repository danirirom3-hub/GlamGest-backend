package com.glamgest.app.infrastructure.presentation.controller;

import com.glamgest.app.application.dto.client.ClientRequestDTO;
import com.glamgest.app.application.dto.client.ClientResponseDTO;
import com.glamgest.app.application.dto.client.ClientUpdateDTO;
import com.glamgest.app.application.usecase.client.CreateClientUseCase;
import com.glamgest.app.application.usecase.client.DeleteClientUseCase;
import com.glamgest.app.application.usecase.client.GetAllClientsUseCase;
import com.glamgest.app.application.usecase.client.GetClientByIdUseCase;
import com.glamgest.app.application.usecase.client.GetMyClientUseCase;
import com.glamgest.app.application.usecase.client.UpdateClientUseCase;
import com.glamgest.app.infrastructure.presentation.helper.BuilderHelper;
import com.glamgest.app.infrastructure.configuration.AuditLogContext;
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
    private final GetMyClientUseCase getMyClientUseCase;
    private final AuditLogContext auditLogContext;

    public ClientController(CreateClientUseCase createClientUseCase,
                            GetAllClientsUseCase getAllClientsUseCase,
                            GetClientByIdUseCase getClientByIdUseCase,
                            UpdateClientUseCase updateClientUseCase,
                            DeleteClientUseCase deleteClientUseCase,
                            GetMyClientUseCase getMyClientUseCase,
                            AuditLogContext auditLogContext) {
        this.createClientUseCase = createClientUseCase;
        this.getAllClientsUseCase = getAllClientsUseCase;
        this.getClientByIdUseCase = getClientByIdUseCase;
        this.updateClientUseCase = updateClientUseCase;
        this.deleteClientUseCase = deleteClientUseCase;
        this.getMyClientUseCase = getMyClientUseCase;
        this.auditLogContext = auditLogContext;
    }

    @PostMapping
    public ResponseEntity<?> createClient(@Valid @RequestBody ClientRequestDTO clientRequestDTO,
                                          BindingResult result) {
        if (result.hasFieldErrors()) {
            return validation(result);
        }

        ClientResponseDTO createdClient = createClientUseCase.execute(clientRequestDTO);
        auditLogContext.setResource(createdClient.getId().toString(), createdClient.getName());
        return BuilderHelper.buildResponse(createdClient, "Cliente creado", HttpStatus.CREATED, true);
    }

    @GetMapping
    public ResponseEntity<?> getAllClients(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "recent") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size) {
        return BuilderHelper.buildResponse(getAllClientsUseCase.execute(search, sort, page, size),
                "Clientes obtenidos", HttpStatus.OK, true);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMyClient() {
        return BuilderHelper.buildResponse(getMyClientUseCase.execute(), "Mi perfil obtenido", HttpStatus.OK, true);
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
        auditLogContext.setResource(updatedClient.getId().toString(), updatedClient.getName());
        return BuilderHelper.buildResponse(updatedClient, "Cliente actualizado", HttpStatus.OK, true);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteClient(@PathVariable Integer id) {
        String clientName = deleteClientUseCase.execute(id);
        auditLogContext.setResource(id.toString(), clientName);
        return BuilderHelper.buildResponse(null, "Cliente desactivado", HttpStatus.NO_CONTENT, true);
    }

    private ResponseEntity<?> validation(BindingResult result) {
        Map<String, Object> errors = new HashMap<>();
        result.getFieldErrors().forEach(error -> errors.put(error.getField(),
                "El campo " + error.getField() + " " + error.getDefaultMessage()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
}
