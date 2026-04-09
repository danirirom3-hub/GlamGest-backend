package com.glamgest.app.application.usecase;

import com.glamgest.app.application.dto.RoleResponseDTO;

public interface GetRoleByIdUseCase {

    RoleResponseDTO execute(Integer id);
}