package com.glamgest.app.application.usecase.role;

import com.glamgest.app.application.dto.role.RoleRequestDTO;
import com.glamgest.app.application.dto.role.RoleResponseDTO;

public interface CreateRoleUseCase {

    RoleResponseDTO execute(RoleRequestDTO roleRequestDTO);
}