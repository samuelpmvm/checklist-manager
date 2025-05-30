package com.checklist.jwt;

import com.checklist.entities.UserRoles;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Component
public class JwtUtil {
    private final String secretKey;
    private final int expirationTime;

    public JwtUtil(
            @Value("${security.api.secret.key}") String secretKey,
            @Value("${security.api.secret.expiration.seconds}") int expirationTime) {
        this.secretKey = secretKey;
        this.expirationTime = expirationTime;
    }

    public String generateToken(String userName, Set<UserRoles> roles) {
        return Jwts.builder()
                .setSubject(userName)
                .claim("roles", roles.stream().map(userRoles -> userRoles.getRole().getValue()).toList())
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.ofEpochSecond(Instant.now().getEpochSecond() + expirationTime)))
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
                .compact();
    }

    public String getUserNameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public List<String> getRoleNamesFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseClaimsJws(token)
                .getBody().get("roles", List.class);
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        var username = getUserNameFromToken(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    public long getExpirationSeconds(String token) {
        var expirationDate = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
        return expirationDate.toInstant().getEpochSecond() - Date.from(Instant.now()).toInstant().getEpochSecond();
    }

    private boolean isTokenExpired(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration()
                .before(Date.from(Instant.now()));
    }

}
