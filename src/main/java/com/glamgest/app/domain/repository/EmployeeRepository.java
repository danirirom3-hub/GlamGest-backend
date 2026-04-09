package com.glamgest.app.domain.repository;

import com.glamgest.app.domain.model.Employee;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository {

    Optional<Employee> findById(Integer id);

    Optional<Employee> findByPhone(String phone);

    boolean existsByPhone(String phone);

    Employee save(Employee employee);

    void deleteById(Integer id);

    List<Employee> findAll();
}