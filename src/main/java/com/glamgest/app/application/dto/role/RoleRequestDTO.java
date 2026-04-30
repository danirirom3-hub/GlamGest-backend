package com.glamgest.app.application.dto.role;

import jakarta.validation.constraints.NotBlank;

public class RoleRequestDTO {

    @NotBlank(message = "Role name is required")
    private String name;
    
    @NotBlank(message = "Role description is required")
    private String description;

    public RoleRequestDTO() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}