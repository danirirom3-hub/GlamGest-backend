package com.glamgest.app.application.usecase;

import com.glamgest.app.application.dto.EmployeeResponseDTO;

import java.util.List;

public interface GetAllEmployeesUseCase {

    List<EmployeeResponseDTO> execute();
}