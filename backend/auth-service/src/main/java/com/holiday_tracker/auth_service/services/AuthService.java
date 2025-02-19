package com.holiday_tracker.auth_service.services;

import com.holiday_tracker.auth_service.exceptions.AuthenticationException;
import com.holiday_tracker.auth_service.models.User;
import com.holiday_tracker.shared_libraries.config.kafka.KafkaProducer;
import com.holiday_tracker.shared_libraries.events.UserActivityEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {
    private final RestTemplate restTemplate;
    private final JwtDecoder jwtDecoder;
    private final KafkaProducer kafkaProducer;

    @Value(value = "${spring.keycloak.url}")
    String keycloakUrl = "http://dummy-register-url.com";

    @Value(value = "${spring.keycloak.login-url}")
    String loginUrl = "http://dummy-login-url.com";

    public String authenticate(User user) {
        try {
            // Use a default client secret if not provided in environment
            String clientSecret = System.getenv("KEYCLOAK_CLIENT_SECRET") != null
                ? System.getenv("KEYCLOAK_CLIENT_SECRET")
                : "dummy-secret";

            MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
            requestBody.add("client_id", "holiday-tracker-client");
            requestBody.add("client_secret", clientSecret);
            requestBody.add("grant_type", "password");
            requestBody.add("firstName", user.getFirstname());
            requestBody.add("lastName", user.getLastname());
            requestBody.add("username", user.getUsername());
            requestBody.add("email", user.getEmail());
            requestBody.add("password", user.getPassword());


            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.add("client_secret", clientSecret);


            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(requestBody, headers);


            ResponseEntity<Map> response = restTemplate.postForEntity(loginUrl, request, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                String token = (String) response.getBody().get("access_token");
                if (token == null) {
                    throw new AuthenticationException("Keycloak response does not contain an access token.");
                }

                Jwt jwt = jwtDecoder.decode(token);

                // Check that the username in the token matches the provided username
                if (!user.getUsername().equals(jwt.getClaim("preferred_username"))) {
                    throw new AuthenticationException("User info mismatch: token subject does not match the provided username.");
                }

                kafkaProducer.sendMessage("auth-events", new UserActivityEvent(user.getUsername(), "Logged In successfully"));
                return token;
            }
            throw new AuthenticationException("Keycloak did not respond or returned an empty body. Response: " + response.getStatusCode());
        } catch (Exception e) {
            log.error("Authentication error: {}", e.getMessage());
            throw new AuthenticationException("Authentication failed: " + e.getMessage(), e);
        }
    }

    public void register(User user, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + authToken);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(Map.of(
                    "username", user.getUsername(),
                    "email", user.getEmail(),
                    "firstName", user.getFirstname(),
                    "lastName", user.getLastname(),
                    "emailVerified", true,
                    "credentials", new Object[]{
                            Map.of("type", "password", "value", user.getPassword(), "temporary", false)
                    }
            ), headers);

            ResponseEntity<Void> response = restTemplate.postForEntity(keycloakUrl, request, Void.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new AuthenticationException("User registration failed: " + response.getStatusCode(), null);
            }

            kafkaProducer.sendMessage("auth-events", new UserActivityEvent(user.getUsername(), "Successfully Registered"));
        } catch (Exception e) {
            log.error("Registration error: {}", e.getMessage());
            throw new AuthenticationException("User registration failed: " + e.getMessage(), e);
        }
    }

}


