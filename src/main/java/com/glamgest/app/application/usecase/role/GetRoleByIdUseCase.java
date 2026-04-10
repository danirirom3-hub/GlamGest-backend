package com.glamgest.app.application.usecase.role;

import com.glamgest.app.application.dto.role.RoleResponseDTO;

public interface GetRoleByIdUseCase {

    RoleResponseDTO execute(Integer id);
}