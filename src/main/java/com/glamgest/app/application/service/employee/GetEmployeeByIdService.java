package com.glamgest.app.application.service.employee;

import com.glamgest.app.application.dto.employee.EmployeeResponseDTO;
import com.glamgest.app.application.usecase.employee.GetEmployeeByIdUseCase;
import com.glamgest.app.common.exception.ResourceNotFoundException;
import com.glamgest.app.domain.model.Employee;
import com.glamgest.app.domain.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

@Service
public class GetEmployeeByIdService implements GetEmployeeByIdUseCase {

    private final EmployeeRepository employeeRepository;

    public GetEmployeeByIdService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public EmployeeResponseDTO execute(Integer id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id " + id));

        return new EmployeeResponseDTO(
                employee.getId(),
                employee.getName(),
                employee.getPhone(),
                employee.getActive()
        );
    }
}