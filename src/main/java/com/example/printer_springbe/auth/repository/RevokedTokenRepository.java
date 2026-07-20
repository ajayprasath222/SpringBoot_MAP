package com.example.printer_springbe.auth.repository;

import com.example.printer_springbe.auth.entity.RevokedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;

public interface RevokedTokenRepository extends JpaRepository<RevokedToken, String> {

    boolean existsByJti(String jti);

    @Modifying(clearAutomatically = true)
    @Query("delete from RevokedToken r where r.expiresAt < :now")
    int deleteExpiredBefore(@Param("now") Instant now);
}
