package com.holiday_tracker.auth_service.controllers;


import com.holiday_tracker.auth_service.models.User;
import com.holiday_tracker.auth_service.services.AuthService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        String token = authService.authenticate(user);
        return ResponseEntity.ok(Map.of("access_token", token));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, @RequestBody User user) {
        System.setProperty("auth_token", authorizationHeader.replace("Bearer ", ""));
        authService.register(user);
        return ResponseEntity.ok(Map.of("message", "User registered successfully"));
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUserInfo(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(Map.of(
                "username", jwt.getClaim("preferred_username"),
                "roles", jwt.getClaim("realm_access")
        ));
    }
}