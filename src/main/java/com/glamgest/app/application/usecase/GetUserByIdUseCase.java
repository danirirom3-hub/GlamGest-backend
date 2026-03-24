package com.glamgest.app.application.usecase;

import com.glamgest.app.application.dto.UserResponseDTO;

public interface GetUserByIdUseCase {

    UserResponseDTO execute(Integer id);
}