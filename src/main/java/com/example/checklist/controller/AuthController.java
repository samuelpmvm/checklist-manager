package com.example.checklist.controller;

import com.example.api.auth.AuthApi;
import com.example.checklist.mapper.RolesMapper;
import com.example.checklist.service.AppUserDetailsService;
import com.example.model.auth.LoginRequest;
import com.example.model.auth.LoginResponse;
import com.example.model.auth.UserInfoRequest;
import com.example.model.auth.UserInfoResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController implements AuthApi {

    private final AppUserDetailsService appUserDetailsService;
    static final String APPLICATION_LOGIN_RESPONSE_V1_JSON = "application/login-response-v1+json";
    static final String APPLICATION_LOGIN_REQUEST_V1_JSON = "application/login-request-v1+json";
    static final String APPLICATION_USERINFO_RESPONSE_V1_JSON = "application/userinfo-response-v1+json";

    public AuthController(AppUserDetailsService appUserDetailsService) {
        this.appUserDetailsService = appUserDetailsService;
    }

    @Override
    public ResponseEntity<LoginResponse> login(LoginRequest loginRequest) {
        var token = appUserDetailsService.loginUser(loginRequest.getUsername(), loginRequest.getPassword());
        return ResponseEntity.ok(new LoginResponse(token, appUserDetailsService.getExpirationTimeFrom(token)));
    }

    @Override
    public ResponseEntity<UserInfoResponse> getUserInfo() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        var username = authentication.getName();
        return new ResponseEntity<>(new UserInfoResponse(username), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Override
    public ResponseEntity<UserInfoResponse> registerUser(UserInfoRequest userInfoRequest) {
        var appUser= appUserDetailsService.registerUser(
                userInfoRequest.getUsername(),
                userInfoRequest.getPassword(),
                RolesMapper.toEntify(userInfoRequest.getRoles()));
        var userInfoResponse = new UserInfoResponse(appUser.getUsername());
        userInfoResponse.setRoles(RolesMapper.toDto(appUser.getRoles()));
        return new ResponseEntity<>(userInfoResponse, HttpStatus.OK);

    }
}
