package com.holiday_tracker.holiday_service.utils;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtil {
    private static JwtDecoder jwtDecoder;
    private static String userName;

    public SecurityUtil(JwtDecoder jwtDecoder) {
        SecurityUtil.jwtDecoder = jwtDecoder;
    }

    /**
     * Processes the Authorization header for authentication.
     */
    public static void run(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // Remove "Bearer " prefix
            processJwtToken(token);
        } else {
            System.out.println("🔴 No valid authentication header provided.");
        }
    }

    /**
     * Validates and extracts user details from JWT token.
     */
    private static void processJwtToken(String token) {
        try {
            Jwt jwt = jwtDecoder.decode(token);
            JwtAuthenticationToken authentication = new JwtAuthenticationToken(jwt);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            System.out.println("✅ User authenticated: " + jwt.getSubject());
            setUserName(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        } catch (Exception e) {
            System.out.println("❌ Invalid JWT Token: " + e.getMessage());
        }
    }

    public static void setUserName(Object principal) {
        if (principal instanceof JwtAuthenticationToken jwtAuth) {
            Jwt jwt = (Jwt) principal;
            userName = jwt.getClaim("preferred_username");
        }
    }

    public static String getUserName() {
        return userName;
    }
}
