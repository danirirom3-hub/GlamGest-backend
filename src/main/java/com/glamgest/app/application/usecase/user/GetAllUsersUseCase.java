package com.glamgest.app.application.usecase.user;

import java.util.List;

import com.glamgest.app.application.dto.user.UserResponseDTO;

public interface GetAllUsersUseCase {

    List<UserResponseDTO> execute();
}