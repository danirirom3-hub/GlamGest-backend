package com.glamgest.app.application.dto.client;

import java.util.Date;

public class ClientResponseDTO {

    private Integer id;
    private String name;
    private String email;
    private String phone;
    private Date registrationDate;

    public ClientResponseDTO() {
    }

    public ClientResponseDTO(Integer id, String name, String email, String phone, Date registrationDate) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.registrationDate = registrationDate;
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

    public String getPhone() {
        return phone;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }
}