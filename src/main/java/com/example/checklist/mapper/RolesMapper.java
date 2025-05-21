package com.example.checklist.mapper;

import com.example.checklist.resources.Role;
import com.example.model.auth.RoleDto;

import java.util.Set;
import java.util.stream.Collectors;

public final class RolesMapper {

    private RolesMapper() {
    }

    public static Set<RoleDto> toDto(Set<Role> roles) {
        return roles.stream()
                .map(role -> RoleDto.fromValue(role.name()))
                .collect(Collectors.toSet());
    }

    public static Set<Role> toEntify(Set<RoleDto> roles) {
        return roles.stream()
                .map(role -> Role.valueOf(role.name()))
                .collect(Collectors.toSet());
    }
}
