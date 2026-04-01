package com.glamgest.app.application.dto;

public class UserRequestDTO {

    private String name;
    private String email;
    private String password;
    private Integer roleId;
    private Boolean active;

    public UserRequestDTO() {}

    public UserRequestDTO(String name, String email, String password, Integer roleId, Boolean active) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.roleId = roleId;
        this.active = active;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}