package com.glamgest.app.application.usecase;

import com.glamgest.app.application.dto.RoleUpdateDTO;
import com.glamgest.app.application.dto.RoleResponseDTO;

public interface UpdateRoleUseCase {

    RoleResponseDTO execute(RoleUpdateDTO roleUpdateDTO);
}