package com.glamgest.app.infrastructure.presentation.controller;

import com.glamgest.app.application.dto.EmployeeRequestDTO;
import com.glamgest.app.application.dto.EmployeeResponseDTO;
import com.glamgest.app.application.dto.EmployeeUpdateDTO;
import com.glamgest.app.application.usecase.*;
import com.glamgest.app.infrastructure.presentation.helper.BuilderHelper;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final GetEmployeeByIdUseCase getEmployeeByIdUseCase;
    private final CreateEmployeeUseCase createEmployeeUseCase;
    private final UpdateEmployeeUseCase updateEmployeeUseCase;
    private final DeleteEmployeeUseCase deleteEmployeeUseCase;
    private final GetAllEmployeesUseCase getAllEmployeesUseCase;

    public EmployeeController(GetEmployeeByIdUseCase getEmployeeByIdUseCase,
            CreateEmployeeUseCase createEmployeeUseCase,
            UpdateEmployeeUseCase updateEmployeeUseCase,
            DeleteEmployeeUseCase deleteEmployeeUseCase,
            GetAllEmployeesUseCase getAllEmployeesUseCase) {
        this.getEmployeeByIdUseCase = getEmployeeByIdUseCase;
        this.createEmployeeUseCase = createEmployeeUseCase;
        this.updateEmployeeUseCase = updateEmployeeUseCase;
        this.deleteEmployeeUseCase = deleteEmployeeUseCase;
        this.getAllEmployeesUseCase = getAllEmployeesUseCase;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return BuilderHelper.buildResponse(getEmployeeByIdUseCase.execute(id), "empleado obtenido", HttpStatus.OK, true);
    }

    @PostMapping
    public ResponseEntity<?> createEmployee(@Valid @RequestBody EmployeeRequestDTO employeeRequestDTO,
            BindingResult result) {
        if (result.hasFieldErrors()) {
            return this.validation(result);
        }

        EmployeeResponseDTO createdEmployee = createEmployeeUseCase.execute(employeeRequestDTO);
        return BuilderHelper.buildResponse(createdEmployee, "empleado creado", HttpStatus.CREATED, true);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEmployee(@PathVariable Integer id,
            @Valid @RequestBody EmployeeUpdateDTO employeeUpdateDTO,
            BindingResult result) {
        if (result.hasFieldErrors()) {
            return this.validation(result);
        }

        employeeUpdateDTO.setId(id);
        EmployeeResponseDTO updatedEmployee = updateEmployeeUseCase.execute(employeeUpdateDTO);
        return BuilderHelper.buildResponse(updatedEmployee, "empleado actualizado", HttpStatus.OK, true);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEmployee(@PathVariable Integer id) {
        deleteEmployeeUseCase.execute(id);
        return BuilderHelper.buildResponse(null, "empleado eliminado", HttpStatus.NO_CONTENT, true);
    }

    @GetMapping
    public ResponseEntity<?> getAllEmployees() {
        List<EmployeeResponseDTO> employees = getAllEmployeesUseCase.execute();
        return BuilderHelper.buildResponse(employees, "empleados obtenidos", HttpStatus.OK, true);
    }

    private ResponseEntity<?> validation(BindingResult result) {
        Map<String, Object> errors = new HashMap<>();
        result.getFieldErrors().forEach(error -> errors.put(error.getField(),
                "El campo " + error.getField() + " " + error.getDefaultMessage()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
}