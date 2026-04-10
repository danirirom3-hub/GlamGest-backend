package com.glamgest.app.application.usecase.user;

import com.glamgest.app.application.dto.user.UserResponseDTO;
import com.glamgest.app.application.dto.user.UserUpdateDTO;

public interface UpdateUserUseCase {

    UserResponseDTO execute(UserUpdateDTO userUpdateDTO);
}