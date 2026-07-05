package com.example.printer_springbe.auth.service;

import com.example.printer_springbe.auth.constant.AuthConstants;
import com.example.printer_springbe.auth.dto.request.LoginRequest;
import com.example.printer_springbe.auth.dto.response.LoginResponse;
import com.example.printer_springbe.auth.entity.User;
import com.example.printer_springbe.auth.repository.UserRepository;
import com.example.printer_springbe.auth.util.EmailNormalizer;
import com.example.printer_springbe.common.exception.BusinessException;
import com.example.printer_springbe.common.response.ResponseCode;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoginService {

    private static final String INVALID_LOGIN_MESSAGE = "Invalid email or password";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;

    public LoginService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenService jwtTokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        String email = EmailNormalizer.normalize(request.email());

        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> invalidCredentials());

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw invalidCredentials();
        }

        String accessToken = jwtTokenService.createAccessToken(email, user.getId());
        return new LoginResponse(
                user.getId(),
                email,
                accessToken,
                AuthConstants.TOKEN_TYPE_BEARER,
                jwtTokenService.accessTokenTtlSeconds()
        );
    }

    private static BusinessException invalidCredentials() {
        return new BusinessException(
                ResponseCode.INVALID_CREDENTIALS,
                HttpStatus.UNAUTHORIZED,
                INVALID_LOGIN_MESSAGE
        );
    }
}
