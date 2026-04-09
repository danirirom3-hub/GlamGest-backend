package com.glamgest.app.application.usecase;

import com.glamgest.app.application.dto.RoleRequestDTO;
import com.glamgest.app.application.dto.RoleResponseDTO;

public interface CreateRoleUseCase {

    RoleResponseDTO execute(RoleRequestDTO roleRequestDTO);
}