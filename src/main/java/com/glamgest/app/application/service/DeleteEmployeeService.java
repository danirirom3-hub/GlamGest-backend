package com.glamgest.app.application.service;

import com.glamgest.app.application.usecase.DeleteEmployeeUseCase;
import com.glamgest.app.common.exception.ResourceNotFoundException;
import com.glamgest.app.domain.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

@Service
public class DeleteEmployeeService implements DeleteEmployeeUseCase {

    private final EmployeeRepository employeeRepository;

    public DeleteEmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public void execute(Integer id) {
        if (!employeeRepository.findById(id).isPresent()) {
            throw new ResourceNotFoundException("Employee not found with id " + id);
        }
        employeeRepository.deleteById(id);
    }
}