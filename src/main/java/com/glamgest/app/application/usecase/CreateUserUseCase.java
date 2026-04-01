package com.glamgest.app.application.usecase;

import com.glamgest.app.application.dto.UserRequestDTO;
import com.glamgest.app.application.dto.UserResponseDTO;

public interface CreateUserUseCase {

    UserResponseDTO execute(UserRequestDTO userRequestDTO);
}