package com.glamgest.app.infrastructure.presentation.controller;

import com.glamgest.app.application.dto.role.RoleRequestDTO;
import com.glamgest.app.application.dto.role.RoleResponseDTO;
import com.glamgest.app.application.dto.role.RoleUpdateDTO;
import com.glamgest.app.application.usecase.role.CreateRoleUseCase;
import com.glamgest.app.application.usecase.role.DeleteRoleUseCase;
import com.glamgest.app.application.usecase.role.GetAllRolesUseCase;
import com.glamgest.app.application.usecase.role.GetRoleByIdUseCase;
import com.glamgest.app.application.usecase.role.UpdateRoleUseCase;
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
@RequestMapping("/api/roles")
public class RoleController {

    private final GetRoleByIdUseCase getRoleByIdUseCase;
    private final CreateRoleUseCase createRoleUseCase;
    private final UpdateRoleUseCase updateRoleUseCase;
    private final DeleteRoleUseCase deleteRoleUseCase;
    private final GetAllRolesUseCase getAllRolesUseCase;

    public RoleController(GetRoleByIdUseCase getRoleByIdUseCase,
            CreateRoleUseCase createRoleUseCase,
            UpdateRoleUseCase updateRoleUseCase,
            DeleteRoleUseCase deleteRoleUseCase,
            GetAllRolesUseCase getAllRolesUseCase) {
        this.getRoleByIdUseCase = getRoleByIdUseCase;
        this.createRoleUseCase = createRoleUseCase;
        this.updateRoleUseCase = updateRoleUseCase;
        this.deleteRoleUseCase = deleteRoleUseCase;
        this.getAllRolesUseCase = getAllRolesUseCase;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return BuilderHelper.buildResponse(getRoleByIdUseCase.execute(id), "rol obtenido", HttpStatus.OK, true);
    }

    @PostMapping
    public ResponseEntity<?> createRole(@Valid @RequestBody RoleRequestDTO roleRequestDTO,
            BindingResult result) {
        if (result.hasFieldErrors()) {
            return this.validation(result);
        }

        RoleResponseDTO createdRole = createRoleUseCase.execute(roleRequestDTO);
        return BuilderHelper.buildResponse(createdRole, "rol creado", HttpStatus.CREATED, true);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateRole(@PathVariable Integer id,
            @Valid @RequestBody RoleUpdateDTO roleUpdateDTO,
            BindingResult result) {
        if (result.hasFieldErrors()) {
            return this.validation(result);
        }

        roleUpdateDTO.setId(id);
        RoleResponseDTO updatedRole = updateRoleUseCase.execute(roleUpdateDTO);
        return BuilderHelper.buildResponse(updatedRole, "rol actualizado", HttpStatus.OK, true);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRole(@PathVariable Integer id) {
        deleteRoleUseCase.execute(id);
        return BuilderHelper.buildResponse(null, "rol eliminado", HttpStatus.NO_CONTENT, true);
    }

    @GetMapping
    public ResponseEntity<?> getAllRoles() {
        List<RoleResponseDTO> roles = getAllRolesUseCase.execute();
        return BuilderHelper.buildResponse(roles, "roles obtenidos", HttpStatus.OK, true);
    }

    private ResponseEntity<?> validation(BindingResult result) {
        Map<String, Object> errors = new HashMap<>();
        result.getFieldErrors().forEach(error -> errors.put(error.getField(),
                "El campo " + error.getField() + " " + error.getDefaultMessage()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
}