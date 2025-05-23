package com.example.checklist.service;

import com.example.checklist.entities.AppUser;
import com.example.checklist.jwt.JwtUtil;
import com.example.checklist.mapper.RolesMapper;
import com.example.checklist.repository.UserRepository;
import com.example.model.auth.RoleDto;
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
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String username) {
        var appUser =  userRepository.findById(username).orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        return new User(
                appUser.getUsername(),
                appUser.getPassword(),
                appUser.getRoles()
                        .stream()
                        .map(userRoles -> new SimpleGrantedAuthority(userRoles.getRole().getValue())).toList());
    }

    @Transactional(readOnly = true)
    public String loginUser(String username, String password) {
        var appUser =  userRepository.findById(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        if(!passwordEncoder.matches(password, appUser.getPassword())) {
            throw new BadCredentialsException(String.format("Password %s is invalid for user %s", password, username));
        }
        LOGGER.info("Generating toker for user: {}", username);
        return jwtUtil.generateToken(username, appUser.getRoles());
        }

    public Long getExpirationTimeFrom(String token) {
        LOGGER.info("Getting expiration time from token");
        return jwtUtil.getExpirationSeconds(token);
    }

    @Transactional
    public AppUser registerUser(String username, String password, Set<RoleDto> roles) {
        var appUser = new AppUser(username, passwordEncoder.encode(password));
        appUser.setRoles(RolesMapper.toEntify(appUser, roles));
        LOGGER.info("Registering user: {}", username);
        return userRepository.save(appUser);
    }
}
