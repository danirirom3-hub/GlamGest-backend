package com.glamgest.app.infrastructure.presentation.controller;

import com.glamgest.app.application.dto.service.ServiceRequestDTO;
import com.glamgest.app.application.dto.service.ServiceResponseDTO;
import com.glamgest.app.application.dto.service.ServiceUpdateDTO;
import com.glamgest.app.application.usecase.service.CreateServiceUseCase;
import com.glamgest.app.application.usecase.service.DeleteServiceUseCase;
import com.glamgest.app.application.usecase.service.GetAllServicesUseCase;
import com.glamgest.app.application.usecase.service.GetServiceByIdUseCase;
import com.glamgest.app.application.usecase.service.UpdateServiceUseCase;
import com.glamgest.app.infrastructure.presentation.helper.BuilderHelper;
import com.glamgest.app.infrastructure.configuration.AuditLogContext;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api/services")
public class ServiceController {

    private final CreateServiceUseCase createServiceUseCase;
    private final GetAllServicesUseCase getAllServicesUseCase;
    private final GetServiceByIdUseCase getServiceByIdUseCase;
    private final UpdateServiceUseCase updateServiceUseCase;
    private final DeleteServiceUseCase deleteServiceUseCase;
    private final AuditLogContext auditLogContext;

    public ServiceController(CreateServiceUseCase createServiceUseCase,
                             GetAllServicesUseCase getAllServicesUseCase,
                             GetServiceByIdUseCase getServiceByIdUseCase,
                             UpdateServiceUseCase updateServiceUseCase,
                             DeleteServiceUseCase deleteServiceUseCase,
                             AuditLogContext auditLogContext) {
        this.createServiceUseCase = createServiceUseCase;
        this.getAllServicesUseCase = getAllServicesUseCase;
        this.getServiceByIdUseCase = getServiceByIdUseCase;
        this.updateServiceUseCase = updateServiceUseCase;
        this.deleteServiceUseCase = deleteServiceUseCase;
        this.auditLogContext = auditLogContext;
    }

    @PostMapping
    public ResponseEntity<?> createService(@Valid @RequestBody ServiceRequestDTO serviceRequestDTO) {
        ServiceResponseDTO response = createServiceUseCase.execute(serviceRequestDTO);
        auditLogContext.setResource(response.getId().toString(), response.getName());
        return BuilderHelper.buildResponse(response, "Service created successfully", HttpStatus.CREATED, true);
    }

    @GetMapping
    public ResponseEntity<?> getAllServices() {
        List<ServiceResponseDTO> services = getAllServicesUseCase.execute();
        return BuilderHelper.buildResponse(services, "Services retrieved successfully", HttpStatus.OK, true);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/admin")
    public ResponseEntity<?> getAllServicesForAdmin() {
        List<ServiceResponseDTO> services = getAllServicesUseCase.executeIncludingInactive();
        return BuilderHelper.buildResponse(services, "All services retrieved successfully", HttpStatus.OK, true);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getServiceById(@PathVariable Integer id) {
        ServiceResponseDTO service = getServiceByIdUseCase.execute(id);
        return BuilderHelper.buildResponse(service, "Service retrieved successfully", HttpStatus.OK, true);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateService(@PathVariable Integer id, @Valid @RequestBody ServiceUpdateDTO serviceUpdateDTO) {
        ServiceResponseDTO response = updateServiceUseCase.execute(id, serviceUpdateDTO);
        auditLogContext.setResource(response.getId().toString(), response.getName());
        return BuilderHelper.buildResponse(response, "Service updated successfully", HttpStatus.OK, true);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteService(@PathVariable Integer id) {
        String serviceName = deleteServiceUseCase.execute(id);
        auditLogContext.setResource(id.toString(), serviceName);
        return BuilderHelper.buildResponse(null, "Service deactivated successfully", HttpStatus.OK, true);
    }
}
