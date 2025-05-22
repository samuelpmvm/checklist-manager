package com.example.checklist.service;

import com.example.checklist.entities.AppUser;
import com.example.checklist.jwt.JwtUtil;
import com.example.checklist.repository.UserRepository;
import com.example.checklist.resources.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AppUserDetailsServiceTest {

    private static final String ADMIN = "admin";

    @InjectMocks
    private AppUserDetailsService appUserDetailsService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void testLoadUserByUsernameSuccess() {
        var appUser = new AppUser(ADMIN, ADMIN, Set.of(Role.ADMIN));
        Mockito.when(userRepository.findByUsername(ADMIN)).thenReturn(Optional.of(appUser));
        var userDetails = appUserDetailsService.loadUserByUsername(ADMIN);

        assertEquals(ADMIN, userDetails.getUsername());
        assertEquals(ADMIN, userDetails.getPassword());
        assertEquals(1, userDetails.getAuthorities().size());
        assertTrue(userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(Role.ADMIN.name())));
    }

    @Test
    void testLoadUserByUsernameFails() {
        Mockito.when(userRepository.findByUsername(ADMIN)).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> appUserDetailsService.loadUserByUsername(ADMIN));
    }

    @Test
    void testLoginUserSuccess() {
        var appUser = new AppUser(ADMIN, ADMIN, Set.of(Role.ADMIN));
        Mockito.when(userRepository.findByUsername(ADMIN)).thenReturn(Optional.of(appUser));
        Mockito.when(passwordEncoder.encode(ADMIN)).thenReturn(ADMIN);
        Mockito.when(jwtUtil.generateToken(ADMIN, appUser.getRoles())).thenReturn(ADMIN);

        var token = appUserDetailsService.loginUser(ADMIN, ADMIN);

        assertEquals(ADMIN, token);
    }

    @Test
    void testLoginUserFailsUserNotFound() {
        Mockito.when(userRepository.findByUsername(ADMIN)).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> appUserDetailsService.loginUser(ADMIN, ADMIN));
    }

    @Test
    void testLoginUserFailsBadCredentials() {
        var appUser = new AppUser(ADMIN, ADMIN, Set.of(Role.ADMIN));
        Mockito.when(userRepository.findByUsername(ADMIN)).thenReturn(Optional.of(appUser));
        Mockito.when(passwordEncoder.encode(ADMIN)).thenReturn("Bad password");
        assertThrows(BadCredentialsException.class, () -> appUserDetailsService.loginUser(ADMIN, ADMIN));
    }

    @Test
    void testGetExpirationSeconds() {
        Mockito.when(jwtUtil.getExpirationSeconds(ADMIN)).thenReturn(10L);
        var expirationTime = appUserDetailsService.getExpirationTimeFrom(ADMIN);
        assertEquals(10L, expirationTime);
    }

    @Test
    void testRegisterUser() {
        var appUser = new AppUser(ADMIN, ADMIN, Set.of(Role.ADMIN));
        Mockito.when(userRepository.save(ArgumentMatchers.any(AppUser.class))).thenReturn(appUser);
        Mockito.when(passwordEncoder.encode(ADMIN)).thenReturn(ADMIN);

        var registeredUser = appUserDetailsService.registerUser(ADMIN, ADMIN, Set.of(Role.ADMIN));

        assertEquals(appUser.getUsername(), registeredUser.getUsername());
        assertEquals(appUser.getPassword(), registeredUser.getPassword());
        assertEquals(appUser.getRoles(), registeredUser.getRoles());
    }

}