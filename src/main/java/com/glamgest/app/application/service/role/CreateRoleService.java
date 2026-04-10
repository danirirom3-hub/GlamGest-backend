package com.glamgest.app.application.service.role;

import com.glamgest.app.application.dto.role.RoleRequestDTO;
import com.glamgest.app.application.dto.role.RoleResponseDTO;
import com.glamgest.app.application.usecase.role.CreateRoleUseCase;
import com.glamgest.app.common.exception.DuplicateRoleNameException;
import com.glamgest.app.domain.model.Role;
import com.glamgest.app.domain.repository.RoleRepository;
import org.springframework.stereotype.Service;

@Service
public class CreateRoleService implements CreateRoleUseCase {

    private final RoleRepository roleRepository;

    public CreateRoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public RoleResponseDTO execute(RoleRequestDTO roleRequestDTO) {
        if (roleRepository.existsByName(roleRequestDTO.getName())) {
            throw new DuplicateRoleNameException("Role name already exists: " + roleRequestDTO.getName());
        }

        Role role = new Role(
                null,
                roleRequestDTO.getName(),
                roleRequestDTO.getDescription()
        );

        Role savedRole = roleRepository.save(role);

        return new RoleResponseDTO(
                savedRole.getId(),
                savedRole.getName(),
                savedRole.getDescription()
        );
    }
}