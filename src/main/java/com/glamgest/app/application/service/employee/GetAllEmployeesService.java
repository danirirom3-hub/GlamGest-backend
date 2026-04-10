package com.glamgest.app.application.service.employee;

import com.glamgest.app.application.dto.employee.EmployeeResponseDTO;
import com.glamgest.app.application.usecase.employee.GetAllEmployeesUseCase;
import com.glamgest.app.domain.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GetAllEmployeesService implements GetAllEmployeesUseCase {

    private final EmployeeRepository employeeRepository;

    public GetAllEmployeesService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public List<EmployeeResponseDTO> execute() {
        return employeeRepository.findAll().stream()
                .map(employee -> new EmployeeResponseDTO(
                        employee.getId(),
                        employee.getName(),
                        employee.getPhone(),
                        employee.getActive()))
                .collect(Collectors.toList());
    }
}