package com.example.printer_springbe.auth.service;

import com.example.printer_springbe.auth.repository.RevokedTokenRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Component
public class RevokedTokenCleanupJob {

    private final RevokedTokenRepository revokedTokenRepository;

    public RevokedTokenCleanupJob(RevokedTokenRepository revokedTokenRepository) {
        this.revokedTokenRepository = revokedTokenRepository;
    }

    @Scheduled(fixedDelayString = "${app.auth.revoked-token-cleanup-interval-ms:3600000}")
    @Transactional
    public void deleteExpiredRevokedTokens() {
        revokedTokenRepository.deleteExpiredBefore(Instant.now());
    }
}
