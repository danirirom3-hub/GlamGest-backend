package com.glamgest.app.application.usecase.role;

import java.util.List;

import com.glamgest.app.application.dto.role.RoleResponseDTO;

public interface GetAllRolesUseCase {

    List<RoleResponseDTO> execute();
}