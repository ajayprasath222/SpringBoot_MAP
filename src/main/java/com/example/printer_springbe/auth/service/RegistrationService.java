package com.example.printer_springbe.auth.service;

import com.example.printer_springbe.auth.constant.AuthConstants;
import com.example.printer_springbe.auth.dto.request.SendOtpRequest;
import com.example.printer_springbe.auth.dto.request.SignUpRequest;
import com.example.printer_springbe.auth.dto.request.VerifyOtpRequest;
import com.example.printer_springbe.auth.dto.response.SendOtpResponse;
import com.example.printer_springbe.auth.dto.response.SignUpResponse;
import com.example.printer_springbe.auth.dto.response.VerifyOtpResponse;
import com.example.printer_springbe.auth.entity.OtpSession;
import com.example.printer_springbe.auth.entity.User;
import com.example.printer_springbe.auth.repository.OtpSessionRepository;
import com.example.printer_springbe.auth.repository.UserRepository;
import com.example.printer_springbe.auth.util.EmailNormalizer;
import com.example.printer_springbe.auth.util.SecureTokenHasher;
import com.example.printer_springbe.common.exception.BusinessException;
import com.example.printer_springbe.common.config.MailModeResolver;
import com.example.printer_springbe.common.response.ResponseCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
public class RegistrationService {

    private final UserRepository userRepository;
    private final OtpSessionRepository otpSessionRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpGenerator otpGenerator;
    private final OtpMailService otpMailService;
    private final JwtTokenService jwtTokenService;

    private final int otpTtlSeconds;
    private final int otpResendCooldownSeconds;
    private final int maxOtpAttempts;
    private final int registrationTokenTtlSeconds;
    private final MailModeResolver mailModeResolver;
    private final boolean exposeOtpInResponse;

    public RegistrationService(
            UserRepository userRepository,
            OtpSessionRepository otpSessionRepository,
            PasswordEncoder passwordEncoder,
            OtpGenerator otpGenerator,
            OtpMailService otpMailService,
            JwtTokenService jwtTokenService,
            MailModeResolver mailModeResolver,
            @Value("${app.auth.otp.ttl-seconds:300}") int otpTtlSeconds,
            @Value("${app.auth.otp.resend-cooldown-seconds:60}") int otpResendCooldownSeconds,
            @Value("${app.auth.otp.max-attempts:5}") int maxOtpAttempts,
            @Value("${app.auth.registration-token.ttl-seconds:900}") int registrationTokenTtlSeconds,
            @Value("${app.auth.expose-otp-in-response:true}") boolean exposeOtpInResponse) {
        this.userRepository = userRepository;
        this.otpSessionRepository = otpSessionRepository;
        this.passwordEncoder = passwordEncoder;
        this.otpGenerator = otpGenerator;
        this.otpMailService = otpMailService;
        this.jwtTokenService = jwtTokenService;
        this.otpTtlSeconds = otpTtlSeconds;
        this.otpResendCooldownSeconds = otpResendCooldownSeconds;
        this.maxOtpAttempts = maxOtpAttempts;
        this.registrationTokenTtlSeconds = registrationTokenTtlSeconds;
        this.mailModeResolver = mailModeResolver;
        this.exposeOtpInResponse = exposeOtpInResponse;
    }

    @Transactional
    public SendOtpResponse sendOtp(SendOtpRequest request) {
        String email = EmailNormalizer.normalize(request.email());

        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new BusinessException(ResponseCode.EMAIL_ALREADY_REGISTERED, HttpStatus.CONFLICT);
        }

        OtpSession session = otpSessionRepository.findByEmailIgnoreCase(email)
                .orElseGet(() -> OtpSession.forEmail(email));

        enforceResendCooldown(session);

        String otp = otpGenerator.generate();
        Instant now = Instant.now();

        session.setOtpHash(passwordEncoder.encode(otp));
        session.setOtpExpiresAt(now.plusSeconds(otpTtlSeconds));
        session.setVerified(false);
        session.setRegistrationTokenHash(null);
        session.setRegistrationTokenExpiresAt(null);
        session.setAttemptCount(0);
        session.setLastOtpSentAt(now);

        otpSessionRepository.save(session);
        otpMailService.sendOtp(email, otp, otpTtlSeconds);

        boolean embedded = mailModeResolver.isEmbedded();
        boolean includeOtpInResponse = exposeOtpInResponse && embedded;
        return new SendOtpResponse(
                email,
                otpTtlSeconds,
                otpResendCooldownSeconds,
                embedded
                        ? "OTP generated for local dev — use data.SendOtp.otp (not sent to your real inbox)"
                        : "OTP sent to your email inbox",
                embedded ? "EMBEDDED" : "SMTP",
                embedded ? "/api/v1/dev/mails" : null,
                includeOtpInResponse ? otp : null
        );
    }

    @Transactional
    public VerifyOtpResponse verifyOtp(VerifyOtpRequest request) {
        String email = EmailNormalizer.normalize(request.email());
        OtpSession session = otpSessionRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new BusinessException(ResponseCode.OTP_INVALID, HttpStatus.UNAUTHORIZED));

        if (session.getAttemptCount() >= maxOtpAttempts) {
            throw new BusinessException(ResponseCode.OTP_ATTEMPTS_EXCEEDED, HttpStatus.TOO_MANY_REQUESTS);
        }

        Instant now = Instant.now();
        if (session.getOtpExpiresAt().isBefore(now) || !passwordEncoder.matches(request.otp(), session.getOtpHash())) {
            session.incrementAttemptCount();
            otpSessionRepository.save(session);
            throw new BusinessException(ResponseCode.OTP_INVALID, HttpStatus.UNAUTHORIZED);
        }

        String registrationToken = UUID.randomUUID().toString();
        session.setVerified(true);
        session.setRegistrationTokenHash(SecureTokenHasher.sha256(registrationToken));
        session.setRegistrationTokenExpiresAt(now.plusSeconds(registrationTokenTtlSeconds));
        session.setAttemptCount(0);
        otpSessionRepository.save(session);

        return new VerifyOtpResponse(
                email,
                registrationToken,
                AuthConstants.TOKEN_TYPE_REGISTRATION,
                registrationTokenTtlSeconds
        );
    }

    @Transactional
    public SignUpResponse signUp(SignUpRequest request, String registrationToken) {
        if (registrationToken == null || registrationToken.isBlank()) {
            throw new BusinessException(
                    ResponseCode.REGISTRATION_TOKEN_INVALID,
                    HttpStatus.UNAUTHORIZED,
                    "Missing " + AuthConstants.REGISTRATION_TOKEN_HEADER + " header"
            );
        }

        String email = EmailNormalizer.normalize(request.email());

        if (!request.password().equals(request.confirmPassword())) {
            throw new BusinessException(ResponseCode.PASSWORD_MISMATCH, HttpStatus.BAD_REQUEST);
        }

        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new BusinessException(ResponseCode.EMAIL_ALREADY_REGISTERED, HttpStatus.CONFLICT);
        }

        OtpSession session = otpSessionRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new BusinessException(ResponseCode.REGISTRATION_NOT_VERIFIED, HttpStatus.FORBIDDEN));

        validateRegistrationSession(session, registrationToken);

        User user = new User(email, passwordEncoder.encode(request.password()));
        user = userRepository.save(user);
        otpSessionRepository.deleteByEmailIgnoreCase(email);

        String accessToken = jwtTokenService.createAccessToken(email, user.getId());
        return new SignUpResponse(
                user.getId(),
                email,
                accessToken,
                AuthConstants.TOKEN_TYPE_BEARER,
                jwtTokenService.accessTokenTtlSeconds()
        );
    }

    private void enforceResendCooldown(OtpSession session) {
        if (session.getLastOtpSentAt() == null) {
            return;
        }
        Instant allowedAt = session.getLastOtpSentAt().plusSeconds(otpResendCooldownSeconds);
        if (Instant.now().isBefore(allowedAt)) {
            long waitSeconds = Duration.between(Instant.now(), allowedAt).getSeconds();
            throw new BusinessException(
                    ResponseCode.OTP_RESEND_TOO_SOON,
                    HttpStatus.TOO_MANY_REQUESTS,
                    "Please wait " + Math.max(1, waitSeconds) + " seconds before requesting another OTP"
            );
        }
    }

    private void validateRegistrationSession(OtpSession session, String registrationToken) {
        if (!session.isVerified()) {
            throw new BusinessException(ResponseCode.REGISTRATION_NOT_VERIFIED, HttpStatus.FORBIDDEN);
        }

        Instant now = Instant.now();
        if (session.getRegistrationTokenExpiresAt() == null
                || session.getRegistrationTokenExpiresAt().isBefore(now)) {
            throw new BusinessException(ResponseCode.REGISTRATION_TOKEN_INVALID, HttpStatus.UNAUTHORIZED);
        }

        String tokenHash = SecureTokenHasher.sha256(registrationToken.trim());
        if (session.getRegistrationTokenHash() == null
                || !session.getRegistrationTokenHash().equals(tokenHash)) {
            throw new BusinessException(ResponseCode.REGISTRATION_TOKEN_INVALID, HttpStatus.UNAUTHORIZED);
        }
    }
}
