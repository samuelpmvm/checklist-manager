package com.example.checklist.jwt;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;

import static org.junit.jupiter.api.Assertions.*;


class JwtUtilTest {

    private final JwtUtil jwtUtil = new JwtUtil("your-very-secure-and-long-secret-key-32-chars", 3600);

    @Test
    void testGenerateAndValidateToken() {
        var token = jwtUtil.generateToken("admin");
        assertNotNull(token);

        var userDetails = User.withUsername("admin")
                .password("password")
                .authorities("ROLE_ADMIN")
                .build();

        assertTrue(jwtUtil.validateToken(token, userDetails));
        assertEquals("admin", jwtUtil.getUserNameFromToken(token));
    }

}

