package com.glamgest.app.domain.model;

public class User {

    private Integer id;
    private String name;
    private String email;
    private String password;
    private Integer roleId;
    private Boolean active;

    public User(Integer id, String name, String email,
            String password, Integer roleId, Boolean active) {

        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.roleId = roleId;
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
