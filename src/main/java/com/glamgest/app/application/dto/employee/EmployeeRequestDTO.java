package com.glamgest.app.application.dto.employee;

import jakarta.validation.constraints.NotBlank;

public class EmployeeRequestDTO {

    @NotBlank(message = "Employee name is required")
    private String name;

    @NotBlank(message = "Employee phone is required")
    private String phone;

    public EmployeeRequestDTO() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}