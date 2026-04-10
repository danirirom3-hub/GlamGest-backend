package com.glamgest.app.application.service.role;

import com.glamgest.app.application.dto.role.RoleResponseDTO;
import com.glamgest.app.application.dto.role.RoleUpdateDTO;
import com.glamgest.app.application.usecase.role.UpdateRoleUseCase;
import com.glamgest.app.common.exception.DuplicateRoleNameException;
import com.glamgest.app.common.exception.ResourceNotFoundException;
import com.glamgest.app.domain.model.Role;
import com.glamgest.app.domain.repository.RoleRepository;
import org.springframework.stereotype.Service;

@Service
public class UpdateRoleService implements UpdateRoleUseCase {

    private final RoleRepository roleRepository;

    public UpdateRoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public RoleResponseDTO execute(RoleUpdateDTO roleUpdateDTO) {
        Role existingRole = roleRepository.findById(roleUpdateDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id " + roleUpdateDTO.getId()));

        roleRepository.findByName(roleUpdateDTO.getName())
                .filter(role -> !role.getId().equals(existingRole.getId()))
                .ifPresent(role -> {
                    throw new DuplicateRoleNameException("Role name already exists: " + roleUpdateDTO.getName());
                });

        Role role = new Role(
                roleUpdateDTO.getId(),
                roleUpdateDTO.getName(),
                roleUpdateDTO.getDescription()
        );

        Role updatedRole = roleRepository.save(role);

        return new RoleResponseDTO(
                updatedRole.getId(),
                updatedRole.getName(),
                updatedRole.getDescription()
        );
    }
}