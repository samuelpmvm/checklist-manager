package com.checklist.entities;

import com.model.auth.RoleDto;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "user_roles")
public class UserRoles {
    @Id
    @GeneratedValue
    private UUID id;

    @Enumerated(EnumType.STRING)
    private RoleDto role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "username")
    private AppUser appUser;

    public UserRoles() {
        // Default constructor
    }

    public UserRoles(RoleDto role) {
        this.role = role;
    }

    public RoleDto getRole() {
        return role;
    }

    public void setRole(RoleDto role) {
        this.role = role;
    }

    public AppUser getAppUser() {
        return appUser;
    }

    public void setAppUser(AppUser appUser) {
        this.appUser = appUser;
    }
}
