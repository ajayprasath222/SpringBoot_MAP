package com.example.printer_springbe.auth.web;

import com.example.printer_springbe.auth.constant.AuthConstants;
import com.example.printer_springbe.auth.model.AuthenticatedUser;
import com.example.printer_springbe.auth.repository.RevokedTokenRepository;
import com.example.printer_springbe.auth.service.JwtTokenService;
import com.example.printer_springbe.common.exception.BusinessException;
import com.example.printer_springbe.common.response.ResponseCode;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtAuthInterceptor implements HandlerInterceptor {

    private static final String LOGOUT_PATH = "/api/v1/auth/logout";

    private final JwtTokenService jwtTokenService;
    private final RevokedTokenRepository revokedTokenRepository;

    public JwtAuthInterceptor(
            JwtTokenService jwtTokenService,
            RevokedTokenRepository revokedTokenRepository) {
        this.jwtTokenService = jwtTokenService;
        this.revokedTokenRepository = revokedTokenRepository;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        boolean logoutRequest = isLogoutRequest(request);
        String header = request.getHeader(AuthConstants.AUTHORIZATION_HEADER);

        if (!StringUtils.hasText(header)) {
            throw new BusinessException(
                    ResponseCode.UNAUTHENTICATED,
                    HttpStatus.UNAUTHORIZED,
                    "Authorization header is missing. Login with POST /api/v1/auth/login, then set header: Bearer <accessToken>"
            );
        }

        if (!header.regionMatches(true, 0, AuthConstants.BEARER_PREFIX, 0, AuthConstants.BEARER_PREFIX.length())) {
            throw new BusinessException(
                    ResponseCode.UNAUTHENTICATED,
                    HttpStatus.UNAUTHORIZED,
                    "Authorization must start with 'Bearer ' (with a space). Example: Bearer eyJhbGciOiJIUzI1NiJ9..."
            );
        }

        String token = header.substring(AuthConstants.BEARER_PREFIX.length()).trim();
        if (!StringUtils.hasText(token) || token.contains("{{") || token.contains("}}")) {
            throw new BusinessException(
                    ResponseCode.UNAUTHENTICATED,
                    HttpStatus.UNAUTHORIZED,
                    "Access token is empty or Postman variable not set. Call /api/v1/auth/login and paste data.Login.accessToken into Bearer token"
            );
        }

        try {
            AuthenticatedUser user = jwtTokenService.parseAccessToken(token);
            if (!logoutRequest && revokedTokenRepository.existsByJti(user.jti())) {
                throw new BusinessException(
                        ResponseCode.UNAUTHENTICATED,
                        HttpStatus.UNAUTHORIZED,
                        "Access token has been revoked. Please login again"
                );
            }
            request.setAttribute(AuthConstants.AUTH_USER_REQUEST_ATTR, user);
            return true;
        } catch (ExpiredJwtException ex) {
            if (logoutRequest) {
                // Allow logout to complete idempotently for already-expired tokens.
                return true;
            }
            throw new BusinessException(ResponseCode.TOKEN_EXPIRED, HttpStatus.UNAUTHORIZED);
        } catch (BusinessException ex) {
            throw ex;
        } catch (JwtException ex) {
            throw new BusinessException(
                    ResponseCode.UNAUTHENTICATED,
                    HttpStatus.UNAUTHORIZED,
                    "Invalid access token"
            );
        }
    }

    private static boolean isLogoutRequest(HttpServletRequest request) {
        String uri = request.getRequestURI();
        if (uri == null) {
            return false;
        }
        String contextPath = request.getContextPath();
        if (StringUtils.hasText(contextPath) && uri.startsWith(contextPath)) {
            uri = uri.substring(contextPath.length());
        }
        return LOGOUT_PATH.equals(uri);
    }
}
