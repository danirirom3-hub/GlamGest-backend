package com.glamgest.app.application.usecase;

import com.glamgest.app.application.dto.EmployeeRequestDTO;
import com.glamgest.app.application.dto.EmployeeResponseDTO;

public interface CreateEmployeeUseCase {

    EmployeeResponseDTO execute(EmployeeRequestDTO employeeRequestDTO);
}