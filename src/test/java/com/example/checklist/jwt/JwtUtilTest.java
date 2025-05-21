package com.example.checklist.jwt;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;

import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;


class JwtUtilTest {

    private final JwtUtil jwtUtil = new JwtUtil("xCqKExhNReXXYd38l6LGnj1gTsStI5ngDqBJLAlp0FrKpY08qA4s7TRIPVup34DF", 3600);

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
        assertTrue(jwtUtil.getExpirationSeconds(token) > 0);
        assertTrue(jwtUtil.getExpirationSeconds(token) <= 3600);
        await().atMost(1, TimeUnit.SECONDS).until(() -> jwtUtil.getExpirationSeconds(token) < 3600);
        assertTrue(jwtUtil.getExpirationSeconds(token) < 3600);
    }

}

