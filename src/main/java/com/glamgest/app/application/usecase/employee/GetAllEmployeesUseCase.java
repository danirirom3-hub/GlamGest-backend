package com.glamgest.app.application.usecase.employee;

import java.util.List;

import com.glamgest.app.application.dto.employee.EmployeeResponseDTO;

public interface GetAllEmployeesUseCase {

    List<EmployeeResponseDTO> execute();
}