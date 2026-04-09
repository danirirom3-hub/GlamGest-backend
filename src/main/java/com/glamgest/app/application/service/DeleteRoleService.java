package com.glamgest.app.application.service;

import com.glamgest.app.application.usecase.DeleteRoleUseCase;
import com.glamgest.app.common.exception.ResourceNotFoundException;
import com.glamgest.app.domain.repository.RoleRepository;
import org.springframework.stereotype.Service;

@Service
public class DeleteRoleService implements DeleteRoleUseCase {

    private final RoleRepository roleRepository;

    public DeleteRoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void execute(Integer id) {
        if (!roleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Role not found with id " + id);
        }
        roleRepository.deleteById(id);
    }
}