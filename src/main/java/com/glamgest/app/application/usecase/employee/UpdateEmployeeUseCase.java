package com.glamgest.app.application.usecase.employee;

import com.glamgest.app.application.dto.employee.EmployeeResponseDTO;
import com.glamgest.app.application.dto.employee.EmployeeUpdateDTO;

public interface UpdateEmployeeUseCase {

    EmployeeResponseDTO execute(EmployeeUpdateDTO employeeUpdateDTO);
}