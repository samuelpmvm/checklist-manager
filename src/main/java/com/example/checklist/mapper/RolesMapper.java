package com.example.checklist.mapper;

import com.example.checklist.entities.UserRoles;
import com.example.model.auth.RoleDto;

import java.util.Set;
import java.util.stream.Collectors;

public final class RolesMapper {

    private RolesMapper() {
    }

    public static Set<RoleDto> toDto(Set<UserRoles> roles) {
        return roles.stream()
                .map(UserRoles::getRole)
                .collect(Collectors.toSet());
    }

    public static Set<UserRoles> toEntify(Set<RoleDto> roles) {
        return roles.stream()
                .map(UserRoles::new)
                .collect(Collectors.toSet());
    }
}
