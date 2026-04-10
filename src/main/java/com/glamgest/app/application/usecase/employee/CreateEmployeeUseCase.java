package com.glamgest.app.application.usecase.employee;

import com.glamgest.app.application.dto.employee.EmployeeRequestDTO;
import com.glamgest.app.application.dto.employee.EmployeeResponseDTO;

public interface CreateEmployeeUseCase {

    EmployeeResponseDTO execute(EmployeeRequestDTO employeeRequestDTO);
}