package com.example.checklist.jwt;

import com.example.checklist.entities.UserRoles;
import com.example.model.auth.RoleDto;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;


class JwtUtilTest {

    private static final String ADMIN = "admin";
    private static final String PASSWORD = "password";
    private final JwtUtil jwtUtil = new JwtUtil("xCqKExhNReXXYd38l6LGnj1gTsStI5ngDqBJLAlp0FrKpY08qA4s7TRIPVup34DF", 3600);

    @Test
    void testGenerateAndValidateToken() {
        var userRoles =  Set.of(new UserRoles(RoleDto.ADMIN));
        var token = jwtUtil.generateToken(ADMIN, userRoles);
        assertNotNull(token);

        var userDetails = User.withUsername(ADMIN)
                .password(PASSWORD)
                .build();

        assertTrue(jwtUtil.validateToken(token, userDetails));
        assertEquals(ADMIN, jwtUtil.getUserNameFromToken(token));
        assertTrue(jwtUtil.getExpirationSeconds(token) > 0);
        assertTrue(jwtUtil.getExpirationSeconds(token) <= 3600);
        await().atMost(1, TimeUnit.SECONDS).until(() -> jwtUtil.getExpirationSeconds(token) < 3600);
        assertTrue(jwtUtil.getExpirationSeconds(token) < 3600);
    }

}

