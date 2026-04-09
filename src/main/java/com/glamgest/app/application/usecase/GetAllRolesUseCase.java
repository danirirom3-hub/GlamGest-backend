package com.glamgest.app.application.usecase;

import com.glamgest.app.application.dto.RoleResponseDTO;

import java.util.List;

public interface GetAllRolesUseCase {

    List<RoleResponseDTO> execute();
}