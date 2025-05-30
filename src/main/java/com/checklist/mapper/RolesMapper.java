package com.checklist.mapper;

import com.checklist.entities.AppUser;
import com.checklist.entities.UserRoles;
import com.model.auth.RoleDto;

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

    public static Set<UserRoles> toEntify(AppUser appUser, Set<RoleDto> roles) {
        return roles.stream()
                .map(role -> {
                    var userRole = new UserRoles(role);
                    userRole.setAppUser(appUser);
                    return userRole;
                })
                .collect(Collectors.toSet());
    }
}
