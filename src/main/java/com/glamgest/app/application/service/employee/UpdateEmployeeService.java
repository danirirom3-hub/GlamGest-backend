package com.glamgest.app.application.service.employee;

import com.glamgest.app.application.dto.employee.EmployeeResponseDTO;
import com.glamgest.app.application.dto.employee.EmployeeUpdateDTO;
import com.glamgest.app.application.usecase.employee.UpdateEmployeeUseCase;
import com.glamgest.app.common.exception.DuplicateEmployeePhoneException;
import com.glamgest.app.common.exception.ResourceNotFoundException;
import com.glamgest.app.domain.model.Employee;
import com.glamgest.app.domain.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

@Service
public class UpdateEmployeeService implements UpdateEmployeeUseCase {

    private final EmployeeRepository employeeRepository;

    public UpdateEmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public EmployeeResponseDTO execute(EmployeeUpdateDTO employeeUpdateDTO) {
        Employee existingEmployee = employeeRepository.findById(employeeUpdateDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id " + employeeUpdateDTO.getId()));

        String phone = employeeUpdateDTO.getPhone();
        if (phone != null) {
            employeeRepository.findByPhone(phone)
                    .filter(employee -> !employee.getId().equals(existingEmployee.getId()))
                    .ifPresent(employee -> {
                        throw new DuplicateEmployeePhoneException("Employee phone already exists: " + phone);
                    });
        }

        Employee employee = new Employee(
                employeeUpdateDTO.getId(),
                employeeUpdateDTO.getName(),
                employeeUpdateDTO.getPhone(),
                existingEmployee.getActive()
        );

        Employee updatedEmployee = employeeRepository.save(employee);

        return new EmployeeResponseDTO(
                updatedEmployee.getId(),
                updatedEmployee.getName(),
                updatedEmployee.getPhone(),
                updatedEmployee.getActive()
        );
    }
}