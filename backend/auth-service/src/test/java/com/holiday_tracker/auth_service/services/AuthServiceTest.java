package com.holiday_tracker.auth_service.services;

import com.holiday_tracker.auth_service.exceptions.AuthenticationException;
import com.holiday_tracker.auth_service.models.User;
import com.holiday_tracker.shared_libraries.config.kafka.KafkaProducer;
import com.holiday_tracker.shared_libraries.events.UserActivityEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestPropertySource(locations = "classpath:application-test.yml")
class AuthServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private KafkaProducer kafkaProducer;

    @Mock
    private JwtDecoder jwtDecoder;

    @Mock
    private KafkaTemplate<String, UserActivityEvent> kafkaTemplate;

    @InjectMocks
    AuthService authService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPassword("password123");
        testUser.setEmail("testuser@example.com");
        testUser.setFirstname("Test");
        testUser.setLastname("User");
    }

    @Test
    void authenticate_ShouldReturnToken_WhenValidCredentials() {
        Map<String, Object> responseMap = Map.of("access_token", "fake-jwt-token");
        ResponseEntity<Map> mockResponse = new ResponseEntity<>(responseMap, HttpStatus.OK);

        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(mockResponse);

        Jwt mockJwt = Jwt.withTokenValue("mock-token")
                .header("alg", "HS256")
                .claim("preferred_username", "testuser")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();

        when(jwtDecoder.decode(anyString())).thenReturn(mockJwt);

        // Call authenticate method
        String token = authService.authenticate(testUser);

        assertNotNull(token);
        assertEquals("fake-jwt-token", token);
        verify(kafkaProducer, times(1))
                .sendMessage(eq("auth-events"), any(UserActivityEvent.class));
    }

    @Test
    void authenticate_ShouldThrowAuthenticationException_WhenUsernameMismatch() {
        Map<String, Object> responseMap = Map.of("access_token", "fake-jwt-token");
        ResponseEntity<Map> mockResponse = new ResponseEntity<>(responseMap, HttpStatus.OK);

        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(mockResponse);

        Jwt mockJwt = Jwt.withTokenValue("mock-token")
                .header("alg", "HS256")
                .claim("preferred_username", "wronguser")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();

        when(jwtDecoder.decode(anyString())).thenReturn(mockJwt);

        assertThrows(AuthenticationException.class, () -> authService.authenticate(testUser));
    }

    @Test
    void authenticate_ShouldThrowAuthenticationException_OnFailure() {
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Map.class)))
                .thenThrow(new RuntimeException("Keycloak service unavailable"));

        AuthenticationException thrown = assertThrows(AuthenticationException.class, () -> authService.authenticate(testUser));
        assertTrue(thrown.getMessage().contains("Authentication failed"));
    }

    @Test
    void register_ShouldMakeHttpCall() {
        ResponseEntity<Object> mockResponse = new ResponseEntity<>(HttpStatus.CREATED);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Void.class)))
                .thenReturn((ResponseEntity) mockResponse);

        assertDoesNotThrow(() -> authService.register(testUser, "dummy-token"));
    }
}