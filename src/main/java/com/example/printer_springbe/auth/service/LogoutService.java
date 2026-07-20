package com.example.printer_springbe.auth.service;

import com.example.printer_springbe.auth.constant.AuthConstants;
import com.example.printer_springbe.auth.dto.response.LogoutResponse;
import com.example.printer_springbe.auth.entity.RevokedToken;
import com.example.printer_springbe.auth.model.AuthenticatedUser;
import com.example.printer_springbe.auth.repository.RevokedTokenRepository;
import com.example.printer_springbe.common.exception.BusinessException;
import com.example.printer_springbe.common.response.ResponseCode;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class LogoutService {

    private static final LogoutResponse LOGGED_OUT = new LogoutResponse("Logged out successfully");

    private final JwtTokenService jwtTokenService;
    private final RevokedTokenRepository revokedTokenRepository;

    public LogoutService(JwtTokenService jwtTokenService, RevokedTokenRepository revokedTokenRepository) {
        this.jwtTokenService = jwtTokenService;
        this.revokedTokenRepository = revokedTokenRepository;
    }

    @Transactional
    public LogoutResponse logout(String authorizationHeader) {
        String token = extractBearerToken(authorizationHeader);
        try {
            AuthenticatedUser user = jwtTokenService.parseAccessToken(token);
            revokeIfNeeded(user);
            return LOGGED_OUT;
        } catch (ExpiredJwtException ex) {
            // Token already unusable — treat as logged out.
            return LOGGED_OUT;
        } catch (JwtException ex) {
            throw new BusinessException(
                    ResponseCode.UNAUTHENTICATED,
                    HttpStatus.UNAUTHORIZED,
                    "Invalid access token"
            );
        }
    }

    private void revokeIfNeeded(AuthenticatedUser user) {
        if (revokedTokenRepository.existsByJti(user.jti())) {
            return;
        }
        revokedTokenRepository.save(new RevokedToken(user.jti(), user.userId(), user.expiresAt()));
    }

    private static String extractBearerToken(String authorizationHeader) {
        if (!StringUtils.hasText(authorizationHeader)) {
            throw new BusinessException(
                    ResponseCode.UNAUTHENTICATED,
                    HttpStatus.UNAUTHORIZED,
                    "Authorization header is missing. Set header: Bearer <accessToken>"
            );
        }
        if (!authorizationHeader.regionMatches(
                true, 0, AuthConstants.BEARER_PREFIX, 0, AuthConstants.BEARER_PREFIX.length())) {
            throw new BusinessException(
                    ResponseCode.UNAUTHENTICATED,
                    HttpStatus.UNAUTHORIZED,
                    "Authorization must start with 'Bearer ' (with a space). Example: Bearer eyJhbGciOiJIUzI1NiJ9..."
            );
        }
        String token = authorizationHeader.substring(AuthConstants.BEARER_PREFIX.length()).trim();
        if (!StringUtils.hasText(token) || token.contains("{{") || token.contains("}}")) {
            throw new BusinessException(
                    ResponseCode.UNAUTHENTICATED,
                    HttpStatus.UNAUTHORIZED,
                    "Access token is empty or Postman variable not set. Call /api/v1/auth/login and paste data.Login.accessToken into Bearer token"
            );
        }
        return token;
    }
}
