package com.glamgest.app.domain.model;

import java.time.LocalDateTime;

public class User {

    private Integer id;
    private String name;
    private String email;
    private String password;
    private Integer roleId;
    private String roleName;
    private Boolean active;
    private Boolean privacyPolicyAccepted;
    private String privacyPolicyVersion;
    private LocalDateTime privacyPolicyAcceptedAt;

    public User(Integer id, String name, String email,
            String password, Integer roleId, String roleName, Boolean active) {
        this(id, name, email, password, roleId, roleName, active, false, null);
    }

    public User(Integer id, String name, String email, String password,
            Integer roleId, String roleName, Boolean active,
            Boolean privacyPolicyAccepted, String privacyPolicyVersion) {
        this(id, name, email, password, roleId, roleName, active,
                privacyPolicyAccepted, privacyPolicyVersion, null);
    }

    public User(Integer id, String name, String email, String password,
            Integer roleId, String roleName, Boolean active,
            Boolean privacyPolicyAccepted, String privacyPolicyVersion,
            LocalDateTime privacyPolicyAcceptedAt) {

        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.roleId = roleId;
        this.roleName = roleName;
        this.active = active;
        this.privacyPolicyAccepted = privacyPolicyAccepted;
        this.privacyPolicyVersion = privacyPolicyVersion;
        this.privacyPolicyAcceptedAt = privacyPolicyAcceptedAt;
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

    public String getRoleName() {
        return roleName;
    }

    public Boolean getActive() {
        return active;
    }

    public Boolean getPrivacyPolicyAccepted() {
        return privacyPolicyAccepted;
    }

    public String getPrivacyPolicyVersion() {
        return privacyPolicyVersion;
    }

    public LocalDateTime getPrivacyPolicyAcceptedAt() {
        return privacyPolicyAcceptedAt;
    }
}
