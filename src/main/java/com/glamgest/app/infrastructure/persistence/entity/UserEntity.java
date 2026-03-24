package com.glamgest.app.infrastructure.persistence.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column(name = "user_id")
    private Integer id;

    private String name;

    private String email;

    private String password;

    @Column(name = "role_id")
    private Integer roleId;

    @Column(name = "active", columnDefinition = "TINYINT")
    private Boolean active;

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public Boolean getActive() {
        return active;
    }
}