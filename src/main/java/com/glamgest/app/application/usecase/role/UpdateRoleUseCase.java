package com.glamgest.app.application.usecase.role;

import com.glamgest.app.application.dto.role.RoleResponseDTO;
import com.glamgest.app.application.dto.role.RoleUpdateDTO;

public interface UpdateRoleUseCase {

    RoleResponseDTO execute(RoleUpdateDTO roleUpdateDTO);
}