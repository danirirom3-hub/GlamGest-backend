package com.glamgest.app.application.usecase;

import com.glamgest.app.application.dto.UserUpdateDTO;
import com.glamgest.app.application.dto.UserResponseDTO;

public interface UpdateUserUseCase {

    UserResponseDTO execute(UserUpdateDTO userUpdateDTO);
}