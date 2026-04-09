package com.glamgest.app.application.service;

import com.glamgest.app.application.dto.RoleResponseDTO;
import com.glamgest.app.application.usecase.GetRoleByIdUseCase;
import com.glamgest.app.common.exception.ResourceNotFoundException;
import com.glamgest.app.domain.model.Role;
import com.glamgest.app.domain.repository.RoleRepository;
import org.springframework.stereotype.Service;

@Service
public class GetRoleByIdService implements GetRoleByIdUseCase {

    private final RoleRepository roleRepository;

    public GetRoleByIdService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public RoleResponseDTO execute(Integer id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id " + id));

        return new RoleResponseDTO(
                role.getId(),
                role.getName(),
                role.getDescription()
        );
    }
}