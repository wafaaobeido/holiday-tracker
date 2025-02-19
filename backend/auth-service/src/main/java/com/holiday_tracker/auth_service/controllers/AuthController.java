package com.holiday_tracker.auth_service.controllers;


import com.holiday_tracker.auth_service.models.User;
import com.holiday_tracker.auth_service.services.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private AuthService authService;


    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        String token = authService.authenticate(user);
        return ResponseEntity.ok(Map.of("access_token", token));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user, @AuthenticationPrincipal Jwt jwt) {
        // For example, use the JWT token value in your service call.
        authService.register(user, jwt != null ? jwt.getTokenValue() : null);
        return ResponseEntity.ok(Map.of("message", "User registered successfully"));
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUserInfo(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(Map.of(
                "username", jwt.getClaim("preferred_username"),
                "email", jwt.getClaim("email"),
                "roles", jwt.getClaim("realm_access")  // further processing as needed
        ));
    }
}