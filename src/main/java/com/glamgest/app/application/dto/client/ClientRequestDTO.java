package com.glamgest.app.application.dto.client;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class ClientRequestDTO {

    @NotBlank(message = "Client name is required")
    private String name;

    @NotBlank(message = "Client email is required")
    @Email(message = "Client email must be valid")
    private String email;

    @NotBlank(message = "Client phone is required")
    @Pattern(regexp = "^\\+?[0-9\\-\\s]{7,20}$", message = "Client phone must contain only numbers, spaces, dashes and may start with +")
    private String phone;

    public ClientRequestDTO() {
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}