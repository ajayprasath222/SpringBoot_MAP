package com.example.printer_springbe.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "revoked_tokens")
public class RevokedToken {

    @Id
    @Column(length = 36, nullable = false)
    private String jti;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Instant expiresAt;

    @Column(nullable = false, updatable = false)
    private Instant revokedAt = Instant.now();

    protected RevokedToken() {
    }

    public RevokedToken(String jti, Long userId, Instant expiresAt) {
        this.jti = jti;
        this.userId = userId;
        this.expiresAt = expiresAt;
        this.revokedAt = Instant.now();
    }

    public String getJti() {
        return jti;
    }

    public Long getUserId() {
        return userId;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public Instant getRevokedAt() {
        return revokedAt;
    }
}
