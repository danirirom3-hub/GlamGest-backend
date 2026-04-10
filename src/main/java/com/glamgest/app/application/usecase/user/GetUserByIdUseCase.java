package com.glamgest.app.application.usecase.user;

import com.glamgest.app.application.dto.user.UserResponseDTO;

public interface GetUserByIdUseCase {

    UserResponseDTO execute(Integer id);
}