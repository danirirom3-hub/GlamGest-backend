package com.glamgest.app.application.usecase.employee;

import com.glamgest.app.application.dto.employee.EmployeeResponseDTO;

public interface GetEmployeeByIdUseCase {

    EmployeeResponseDTO execute(Integer id);
}