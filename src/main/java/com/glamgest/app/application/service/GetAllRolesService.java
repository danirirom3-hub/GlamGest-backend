package com.glamgest.app.application.service;

import com.glamgest.app.application.dto.RoleResponseDTO;
import com.glamgest.app.application.usecase.GetAllRolesUseCase;
import com.glamgest.app.domain.model.Role;
import com.glamgest.app.domain.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GetAllRolesService implements GetAllRolesUseCase {

    private final RoleRepository roleRepository;

    public GetAllRolesService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public List<RoleResponseDTO> execute() {
        List<Role> roles = roleRepository.findAll();
        return roles.stream()
                .map(role -> new RoleResponseDTO(
                        role.getId(),
                        role.getName(),
                        role.getDescription()))
                .collect(Collectors.toList());
    }
}