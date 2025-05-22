package com.example.checklist.service;

import com.example.checklist.entities.AppUser;
import com.example.checklist.jwt.JwtUtil;
import com.example.checklist.repository.UserRepository;
import com.example.checklist.resources.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class AppUserDetailsService  implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private static final Logger LOGGER = LoggerFactory.getLogger(AppUserDetailsService.class);

    public AppUserDetailsService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        var appUser =  userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        return new User(
                appUser.getUsername(),
                appUser.getPassword(),
                appUser.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.name())).toList()
        );
    }

    public String loginUser(String username, String password) {
        var appUser =  userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        if(!passwordEncoder.encode(password).matches(appUser.getPassword())) {
            throw new BadCredentialsException(String.format("Password %s is invalid for user %s", password, username));
        }
        LOGGER.info("Generating toker for user {}", username);
        return jwtUtil.generateToken(username, appUser.getRoles());
        }

    public Long getExpirationTimeFrom(String token) {
        LOGGER.info("Getting expiration time from token");
        return jwtUtil.getExpirationSeconds(token);
    }

    public AppUser registerUser(String username, String password, Set<Role> roles) {
        var appUser = new AppUser(username, passwordEncoder.encode(password), roles);
        return userRepository.save(appUser);
    }
}
