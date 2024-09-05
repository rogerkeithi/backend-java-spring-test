package com.rogerkeithi.backend_java_spring_test.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JWTUtil {
    @Value("${token.secret_key}")
    private String secretKey;

    @Value("${token.expires_in}")
    private Long expiresIn;

    public String generateToken(String id, String username, String nivel) {
        return Jwts.builder()
                .subject(username)
                .claim("id", id)
                .claim("username", username)
                .claim("nivel", nivel)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiresIn))
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .compact();
    }
}
