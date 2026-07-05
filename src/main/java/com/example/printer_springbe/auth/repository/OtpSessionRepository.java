package com.example.printer_springbe.auth.repository;

import com.example.printer_springbe.auth.entity.OtpSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpSessionRepository extends JpaRepository<OtpSession, Long> {

    Optional<OtpSession> findByEmailIgnoreCase(String email);

    void deleteByEmailIgnoreCase(String email);
}
