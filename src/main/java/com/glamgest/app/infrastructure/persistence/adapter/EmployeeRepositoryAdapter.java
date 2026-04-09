package com.glamgest.app.infrastructure.persistence.adapter;

import com.glamgest.app.domain.model.Employee;
import com.glamgest.app.domain.repository.EmployeeRepository;
import com.glamgest.app.infrastructure.persistence.entity.Employees;
import com.glamgest.app.infrastructure.persistence.repository.JpaEmployeeRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class EmployeeRepositoryAdapter implements EmployeeRepository {

    private final JpaEmployeeRepository jpaEmployeeRepository;

    public EmployeeRepositoryAdapter(JpaEmployeeRepository jpaEmployeeRepository) {
        this.jpaEmployeeRepository = jpaEmployeeRepository;
    }

    @Override
    public Optional<Employee> findById(Integer id) {
        return jpaEmployeeRepository.findById(id)
                .map(entity -> new Employee(
                        entity.getEmployeeId(),
                        entity.getName(),
                        entity.getPhone(),
                        entity.getActive()));
    }

    @Override
    public Optional<Employee> findByPhone(String phone) {
        return jpaEmployeeRepository.findByPhone(phone)
                .map(entity -> new Employee(
                        entity.getEmployeeId(),
                        entity.getName(),
                        entity.getPhone(),
                        entity.getActive()));
    }

    @Override
    public boolean existsByPhone(String phone) {
        return phone != null && jpaEmployeeRepository.existsByPhone(phone);
    }

    @Override
    public Employee save(Employee employee) {
        Employees entity = new Employees();
        if (employee.getId() != null) {
            entity.setEmployeeId(employee.getId());
        }
        entity.setName(employee.getName());
        entity.setPhone(employee.getPhone());
        entity.setActive(employee.getActive());

        Employees savedEntity = jpaEmployeeRepository.save(entity);

        return new Employee(
                savedEntity.getEmployeeId(),
                savedEntity.getName(),
                savedEntity.getPhone(),
                savedEntity.getActive());
    }

    @Override
    public void deleteById(Integer id) {
        jpaEmployeeRepository.deleteById(id);
    }

    @Override
    public List<Employee> findAll() {
        return jpaEmployeeRepository.findAll().stream()
                .map(entity -> new Employee(
                        entity.getEmployeeId(),
                        entity.getName(),
                        entity.getPhone(),
                        entity.getActive()))
                .collect(Collectors.toList());
    }
}