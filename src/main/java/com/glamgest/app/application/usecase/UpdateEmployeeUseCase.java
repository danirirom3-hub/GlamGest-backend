package com.glamgest.app.application.usecase;

import com.glamgest.app.application.dto.EmployeeResponseDTO;
import com.glamgest.app.application.dto.EmployeeUpdateDTO;

public interface UpdateEmployeeUseCase {

    EmployeeResponseDTO execute(EmployeeUpdateDTO employeeUpdateDTO);
}