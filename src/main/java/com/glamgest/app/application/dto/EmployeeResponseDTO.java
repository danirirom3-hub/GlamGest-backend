package com.glamgest.app.application.dto;

public class EmployeeResponseDTO {

    private Integer id;
    private String name;
    private String phone;
    private Boolean active;

    public EmployeeResponseDTO() {
    }

    public EmployeeResponseDTO(Integer id, String name, String phone, Boolean active) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.active = active;
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

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}