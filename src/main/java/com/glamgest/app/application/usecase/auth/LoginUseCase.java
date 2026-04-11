package com.glamgest.app.application.usecase.auth;

import com.glamgest.app.application.dto.auth.LoginRequestDTO;
import com.glamgest.app.application.dto.auth.LoginResponseDTO;

public interface LoginUseCase {

    LoginResponseDTO execute(LoginRequestDTO loginRequestDTO);
}