package com.example.printer_springbe.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "otp_sessions")
public class OtpSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 320)
    private String email;

    @Column(nullable = false)
    private String otpHash;

    @Column(nullable = false)
    private Instant otpExpiresAt;

    private boolean verified;

    private String registrationTokenHash;

    private Instant registrationTokenExpiresAt;

    @Column(nullable = false)
    private int attemptCount;

    private Instant lastOtpSentAt;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    protected OtpSession() {
    }

    public static OtpSession forEmail(String email) {
        OtpSession session = new OtpSession();
        session.email = email;
        session.attemptCount = 0;
        session.verified = false;
        return session;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getOtpHash() {
        return otpHash;
    }

    public void setOtpHash(String otpHash) {
        this.otpHash = otpHash;
    }

    public Instant getOtpExpiresAt() {
        return otpExpiresAt;
    }

    public void setOtpExpiresAt(Instant otpExpiresAt) {
        this.otpExpiresAt = otpExpiresAt;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public String getRegistrationTokenHash() {
        return registrationTokenHash;
    }

    public void setRegistrationTokenHash(String registrationTokenHash) {
        this.registrationTokenHash = registrationTokenHash;
    }

    public Instant getRegistrationTokenExpiresAt() {
        return registrationTokenExpiresAt;
    }

    public void setRegistrationTokenExpiresAt(Instant registrationTokenExpiresAt) {
        this.registrationTokenExpiresAt = registrationTokenExpiresAt;
    }

    public int getAttemptCount() {
        return attemptCount;
    }

    public void setAttemptCount(int attemptCount) {
        this.attemptCount = attemptCount;
    }

    public void incrementAttemptCount() {
        this.attemptCount++;
    }

    public Instant getLastOtpSentAt() {
        return lastOtpSentAt;
    }

    public void setLastOtpSentAt(Instant lastOtpSentAt) {
        this.lastOtpSentAt = lastOtpSentAt;
    }
}
