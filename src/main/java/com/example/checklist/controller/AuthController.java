package com.example.checklist.controller;

import com.example.api.auth.AuthApi;
import com.example.checklist.service.AppUserDetailsService;
import com.example.model.auth.LoginRequest;
import com.example.model.auth.LoginResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController implements AuthApi {

    private final AppUserDetailsService appUserDetailsService;
    static final String APPLICATION_LOGIN_RESPONSE_V1_JSON = "application/login-response-v1+json";
    static final String APPLICATION_LOGIN_REQUEST_V1_JSON = "application/login-request-v1+json";

    public AuthController(AppUserDetailsService appUserDetailsService) {
        this.appUserDetailsService = appUserDetailsService;
    }

    @Override
    public ResponseEntity<LoginResponse> login(LoginRequest loginRequest) {
        var token = appUserDetailsService.loginUser(loginRequest.getUsername(), loginRequest.getPassword());
        return ResponseEntity.ok(new LoginResponse(token, Math.toIntExact(appUserDetailsService.getExpirationTimeFrom(token))));
    }
}
