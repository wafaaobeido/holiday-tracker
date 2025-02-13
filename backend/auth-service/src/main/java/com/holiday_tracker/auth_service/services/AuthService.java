package com.holiday_tracker.auth_service.services;

import com.holiday_tracker.auth_service.exceptions.AuthenticationException;
import com.holiday_tracker.auth_service.exceptions.UserInfoMismatchException;
import com.holiday_tracker.auth_service.exceptions.UserNotFoundException;
import com.holiday_tracker.auth_service.models.User;
import com.holiday_tracker.auth_service.repositories.UserRepository;
import com.holiday_tracker.auth_service.util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import java.io.Serializable;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.Optional;

/**
 * AuthService is responsible for authenticating users.
 * It first checks if the user exists in the system using UserRepository.
 * If the user is not found, it throws a UserNotFoundException.
 * Otherwise, it calls the Keycloak token endpoint to authenticate the user and generate a JWT token.
 *
 * Note: In production, additional validation (like password hashing and secure storage) should be implemented.
 */
@Service
public class AuthService {

    private final JwtUtil jwtUtil;
    private final RestTemplate restTemplate;
    private final String keycloakUrl = "http://localhost:8080/admin/realms/myrealm/users";
    private final String loginUrl = "http://localhost:8080/realms/myrealm/protocol/openid-connect/token";

    private final UserRepository userRepository;
    public AuthService(JwtUtil jwtUtil, RestTemplate restTemplate, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.restTemplate = restTemplate;
        this.userRepository = userRepository;
    }

    public String authenticate(User user) {
        // Check if the user exists in our local database.
        // Even though Keycloak manages authentication, this check ensures
        // that the user is registered in our system. If no user is found,
        // we throw an exception. In production, you might rely on Keycloak's
        // user management entirely, but this is here for demonstration.
        // First, check if the user exists in our system (or in Keycloak)
        Optional<User> existingUser = userRepository.findByUsername(user.getUsername());

        if(existingUser.isEmpty()) throw new UserNotFoundException("User not found: " + user.getUsername());

        // Prepare the request parameters
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", "holiday-tracker-client");
        params.add("client_secret", System.getenv("KEYCLOAK_CLIENT_SECRET"));
        params.add("grant_type", "password");
        params.add("username", user.getUsername());
        params.add("email", user.getEmail());
        params.add("password", user.getPassword());


        // Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // Create the HTTP entity with parameters and headers
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        try {
            // Make the POST request to Keycloak's token endpoint
            ResponseEntity<Map> response = restTemplate.postForEntity(loginUrl, request, Map.class);

            // Extract the access token from the response
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                String token = (String) response.getBody().get("access_token");
                // Decode the token and validate that the subject matches the username provided
                var claims = jwtUtil.decodeToken(token);
                if (!user.getUsername().equals(claims.getSubject())) {
                    throw new UserInfoMismatchException("User info mismatch: token subject does not match the provided username.");
                }
                return token;
            }
            throw new RuntimeException("Failed to authenticate user. Response: " + response.getStatusCode());
        } catch (Exception e){
            throw new AuthenticationException("Error during authentication: " + e.getMessage(), e);

        }
    }

    /**
     * Registers a new user in Keycloak.
     * Note: In production, it's better to use Keycloak's built-in user registration rather than exposing a custom endpoint.
     * For this demonstration, the registration endpoint sends a POST request to Keycloak's Admin API.
     */
    public void register(User user) {
        try {

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + System.getProperty("auth_token"));

            // Create the registration payload as required by Keycloak admin REST API
            var body = Map.of(
                    "username", user.getUsername(),
                    "enabled", true,
                    "email", user.getEmail(),
                    "emailVerified", true,
                    "credentials", new Object[]{
                            Map.of("type", "password", "value", user.getPassword(), "temporary", false)
                    }
            );

            // HTTP Request
            HttpEntity<Map<String, Serializable>> request = new HttpEntity<>(body, headers);

            // Post to Keycloak; in production, the authentication must be handled for this request (e.g., using an admin token)
            ResponseEntity<?> response = restTemplate.postForEntity(keycloakUrl, request, ResponseEntity.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                // Handle non-successful response (log, throw custom exception, etc.)
                throw new RuntimeException("User registration failed: " + response.getStatusCode());
            }
        } catch (Exception e) {
            // Log the error and rethrow or handle accordingly
            e.printStackTrace();
            throw new RuntimeException("Error during user registration", e);
        }
    }
}
