package com.example.printer_springbe.auth.controller;

import com.example.printer_springbe.auth.constant.AuthConstants;
import com.example.printer_springbe.auth.dto.request.LoginRequest;
import com.example.printer_springbe.auth.dto.request.SendOtpRequest;
import com.example.printer_springbe.auth.dto.request.SignUpRequest;
import com.example.printer_springbe.auth.dto.request.VerifyOtpRequest;
import com.example.printer_springbe.auth.service.LoginService;
import com.example.printer_springbe.auth.service.RegistrationService;
import com.example.printer_springbe.common.response.ApiResponse;
import com.example.printer_springbe.common.response.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final RegistrationService registrationService;
    private final LoginService loginService;

    public AuthController(RegistrationService registrationService, LoginService loginService) {
        this.registrationService = registrationService;
        this.loginService = loginService;
    }

    @PostMapping("/send-otp")
    public ResponseEntity<ApiResponse> sendOtp(@Valid @RequestBody SendOtpRequest request) {
        return ApiResponses.okEntity("SendOtp", registrationService.sendOtp(request));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse> verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {
        return ApiResponses.okEntity("VerifyOtp", registrationService.verifyOtp(request));
    }

    @PostMapping("/sign-up")
    public ResponseEntity<ApiResponse> signUp(
            @Valid @RequestBody SignUpRequest request,
            @RequestHeader(value = AuthConstants.REGISTRATION_TOKEN_HEADER) String registrationToken) {
        return ApiResponses.okEntity("SignUp", registrationService.signUp(request, registrationToken));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponses.okEntity("Login", loginService.login(request));
    }
}
