package com.example.printer_springbe.controller;

import com.example.printer_springbe.common.response.ApiResponse;
import com.example.printer_springbe.common.response.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Example of the standard response envelope. Replace or extend with real resources
 * (e.g. return {@code ApiResponses.okEntity("Login", loginResult)}).
 */
@RestController
@RequestMapping("/api/v1")
public class HealthController {

    @GetMapping("/health")
    public ResponseEntity<ApiResponse> health() {
        return ApiResponses.okEntity("Health", Map.of("up", true));
    }
}
