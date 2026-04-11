package com.glamgest.app.application.dto.user;

public class UserResponseDTO {

    private Integer id;
    private String name;
    private String email;
    private Integer roleId;
    private String roleName;
    private Boolean active;

    public UserResponseDTO(Integer id, String name, String email,
            Integer roleId, String roleName, Boolean active) {

        this.id = id;
        this.name = name;
        this.email = email;
        this.roleId = roleId;
        this.roleName = roleName;
        this.active = active;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public Boolean getActive() {
        return active;
    }
}