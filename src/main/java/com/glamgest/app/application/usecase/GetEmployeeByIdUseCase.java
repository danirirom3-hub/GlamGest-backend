package com.glamgest.app.application.usecase;

import com.glamgest.app.application.dto.EmployeeResponseDTO;

public interface GetEmployeeByIdUseCase {

    EmployeeResponseDTO execute(Integer id);
}