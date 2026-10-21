package com.glamgest.app.application.service.role;

import com.glamgest.app.application.usecase.role.DeleteRoleUseCase;
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
    public String execute(Integer id) {
        var role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id " + id));
        roleRepository.deleteById(id);
        return role.getName();
    }
}
