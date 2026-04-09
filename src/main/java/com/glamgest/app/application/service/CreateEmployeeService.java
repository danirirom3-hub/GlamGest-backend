package com.glamgest.app.application.service;

import com.glamgest.app.application.dto.EmployeeRequestDTO;
import com.glamgest.app.application.dto.EmployeeResponseDTO;
import com.glamgest.app.application.usecase.CreateEmployeeUseCase;
import com.glamgest.app.common.exception.DuplicateEmployeePhoneException;
import com.glamgest.app.domain.model.Employee;
import com.glamgest.app.domain.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

@Service
public class CreateEmployeeService implements CreateEmployeeUseCase {

    private final EmployeeRepository employeeRepository;

    public CreateEmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public EmployeeResponseDTO execute(EmployeeRequestDTO employeeRequestDTO) {
        if (employeeRequestDTO.getPhone() != null && employeeRepository.existsByPhone(employeeRequestDTO.getPhone())) {
            throw new DuplicateEmployeePhoneException("Employee phone already exists: " + employeeRequestDTO.getPhone());
        }

        Employee employee = new Employee(
                null,
                employeeRequestDTO.getName(),
                employeeRequestDTO.getPhone(),
                employeeRequestDTO.getActive()
        );

        Employee savedEmployee = employeeRepository.save(employee);

        return new EmployeeResponseDTO(
                savedEmployee.getId(),
                savedEmployee.getName(),
                savedEmployee.getPhone(),
                savedEmployee.getActive()
        );
    }
}