package com.example.checklist.controller;

import com.example.checklist.entities.AppUser;
import com.example.checklist.jwt.JwtUtil;
import com.example.checklist.resources.Role;
import com.example.checklist.service.AppUserDetailsService;
import com.example.model.auth.RoleDto;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    private static final String USER = "user";
    private static final String PASSWORD = "password";
    private static final String TOKEN = "token";
    private static final String ADMIN = "ADMIN";

    @MockitoBean
    private AppUserDetailsService appUserDetailsService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void loginSuccess() throws Exception {
        Mockito.when(appUserDetailsService.loginUser(USER, PASSWORD)).thenReturn(TOKEN);
        Mockito.when(appUserDetailsService.getExpirationTimeFrom(TOKEN)).thenReturn(Long.valueOf(10));
        final var request = MockMvcRequestBuilders
                .post("/auth/login")
                .contentType(AuthController.APPLICATION_LOGIN_REQUEST_V1_JSON)
                .accept(AuthController.APPLICATION_LOGIN_RESPONSE_V1_JSON)
                .content("""
                        {
                          "username": "%s",
                          "password": "%s"
                        }
                        """.formatted("user", "password"));
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value(TOKEN))
                .andExpect(jsonPath("$.expiresIn").value("10"));
    }

    @Test
    void getUserInfo() throws Exception {

        UserDetails userDetails = User
                .withUsername(USER)
                .password(USER)
                .build();

        Mockito.when(appUserDetailsService.loadUserByUsername(USER)).thenReturn(userDetails);
        Mockito.when(jwtUtil.getUserNameFromToken(TOKEN)).thenReturn(USER);
        Mockito.when(jwtUtil.validateToken(TOKEN, userDetails)).thenReturn(true);
        final var request = MockMvcRequestBuilders
                .get("/auth/me")
                .header("Authorization", "Bearer " + TOKEN)
                .accept(AuthController.APPLICATION_USERINFO_RESPONSE_V1_JSON);
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(USER));
    }

    @Test
    void registerUserSuccess() throws Exception {
        var appUser = new AppUser(USER, PASSWORD, Collections.singleton(Role.USER));
        Mockito.when(appUserDetailsService.registerUser(USER, PASSWORD, Collections.singleton(Role.USER))).thenReturn(appUser);
        Mockito.when(jwtUtil.getUserNameFromToken(TOKEN)).thenReturn(ADMIN);

        var userDetails = User.withUsername(ADMIN).password(ADMIN).roles(Role.USER.name()).build();
        Mockito.when(jwtUtil.validateToken(TOKEN, userDetails)).thenReturn(true);
        Mockito.when(jwtUtil.getRoleNamesFromToken(TOKEN)).thenReturn(List.of(Role.ADMIN.name()));

        Mockito.when(appUserDetailsService.loadUserByUsername(ADMIN)).thenReturn(userDetails);
        final var request = MockMvcRequestBuilders
                .post("/auth/register")
                .header("Authorization", "Bearer " + TOKEN)
                .contentType(AuthController.APPLICATION_USERINFO_REQUEST_V1_JSON)
                .accept(AuthController.APPLICATION_USERINFO_RESPONSE_V1_JSON)
                .content("""
                        {
                          "username": "%s",
                          "password": "%s",
                          "roles": ["%s"]
                        }
                        """.formatted(USER, PASSWORD, RoleDto.USER.name()));
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(USER))
                .andExpect(jsonPath("$.roles[0]").value(RoleDto.USER.name()));
    }

    @Test
    void registerUserWithoutRoleAdminFails() throws Exception {
        var appUser = new AppUser(USER, PASSWORD, Collections.singleton(Role.USER));
        Mockito.when(appUserDetailsService.registerUser(USER, PASSWORD, Collections.singleton(Role.USER))).thenReturn(appUser);
        Mockito.when(jwtUtil.getUserNameFromToken(TOKEN)).thenReturn(ADMIN);

        var userDetails = User.withUsername(ADMIN).password(ADMIN).roles(Role.USER.name()).build();
        Mockito.when(jwtUtil.validateToken(TOKEN, userDetails)).thenReturn(true);
        Mockito.when(jwtUtil.getRoleNamesFromToken(TOKEN)).thenReturn(List.of(Role.USER.name()));

        Mockito.when(appUserDetailsService.loadUserByUsername(ADMIN)).thenReturn(userDetails);
        final var request = MockMvcRequestBuilders
                .post("/auth/register")
                .header("Authorization", "Bearer " + TOKEN)
                .contentType(AuthController.APPLICATION_USERINFO_REQUEST_V1_JSON)
                .accept(AuthController.APPLICATION_USERINFO_RESPONSE_V1_JSON)
                .content("""
                        {
                          "username": "%s",
                          "password": "%s",
                          "roles": ["%s"]
                        }
                        """.formatted(USER, PASSWORD, RoleDto.USER.name()));
        mockMvc.perform(request)
                .andExpect(status().isUnauthorized());
    }
}