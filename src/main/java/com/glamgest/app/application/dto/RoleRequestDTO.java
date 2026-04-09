package com.glamgest.app.application.dto;

import jakarta.validation.constraints.NotBlank;

public class RoleRequestDTO {

    @NotBlank
    private String name;

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