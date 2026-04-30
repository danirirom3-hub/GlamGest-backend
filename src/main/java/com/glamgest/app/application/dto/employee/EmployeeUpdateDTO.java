package com.glamgest.app.application.dto.employee;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.validation.constraints.NotBlank;

public class EmployeeUpdateDTO {

    @JsonIgnore
    private Integer id;

    @NotBlank(message = "Employee name is required")
    private String name;

    @NotBlank(message = "Employee phone is required")
    private String phone;

    public EmployeeUpdateDTO() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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