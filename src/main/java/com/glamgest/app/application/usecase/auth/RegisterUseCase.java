package com.glamgest.app.application.usecase.auth;

import com.glamgest.app.application.dto.auth.LoginResponseDTO;
import com.glamgest.app.application.dto.auth.RegisterRequestDTO;

public interface RegisterUseCase {
    LoginResponseDTO execute(RegisterRequestDTO request);
}
