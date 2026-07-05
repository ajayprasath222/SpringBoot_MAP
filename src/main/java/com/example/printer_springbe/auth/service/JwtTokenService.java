package com.example.printer_springbe.auth.service;

import com.example.printer_springbe.auth.model.AuthenticatedUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtTokenService {

    private final SecretKey secretKey;
    private final long accessTokenTtlSeconds;

    public JwtTokenService(
            @Value("${app.auth.jwt.secret}") String secret,
            @Value("${app.auth.jwt.access-token-ttl-seconds:86400}") long accessTokenTtlSeconds) {
        if (secret == null || secret.length() < 32) {
            throw new IllegalStateException("app.auth.jwt.secret must be at least 32 characters");
        }
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenTtlSeconds = accessTokenTtlSeconds;
    }

    public String createAccessToken(String email, Long userId) {
        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(accessTokenTtlSeconds);
        return Jwts.builder()
                .subject(email)
                .claim("uid", userId)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(secretKey)
                .compact();
    }

    public int accessTokenTtlSeconds() {
        return (int) accessTokenTtlSeconds;
    }

    public AuthenticatedUser parseAccessToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            Long userId = claims.get("uid", Long.class);
            String email = claims.getSubject();
            if (userId == null || email == null || email.isBlank()) {
                throw new JwtException("Invalid token claims");
            }
            return new AuthenticatedUser(userId, email);
        } catch (ExpiredJwtException ex) {
            throw ex;
        } catch (JwtException ex) {
            throw ex;
        }
    }
}
