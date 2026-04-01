package com.glamgest.app.application.usecase;

import com.glamgest.app.application.dto.UserResponseDTO;

import java.util.List;

public interface GetAllUsersUseCase {

    List<UserResponseDTO> execute();
}