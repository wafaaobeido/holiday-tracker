package com.holiday_tracker.auth_service.controllers;

import com.holiday_tracker.auth_service.models.User;
import com.holiday_tracker.auth_service.services.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@TestPropertySource(properties = "spring.profiles.active=test")
@WebMvcTest(AuthController.class)
class AuthControllerIntegrationTest {

    @MockitoBean
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void login_ShouldReturnToken_WhenValidUser() {
        User user = new User("testUser", "password");
        when(authService.authenticate(user)).thenReturn("mockToken");

        ResponseEntity<?> response = authController.login(user);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(((Map) response.getBody()).containsKey("access_token"));
        assertEquals("mockToken", ((Map) response.getBody()).get("access_token"));
    }

    @Test
    void register_ShouldReturnSuccessMessage() {
        User user = new User("testUser", "password", "email@example.com", "First", "Last");
        Jwt jwtMock = mock(Jwt.class);
        when(jwtMock.getTokenValue()).thenReturn("mockJwtToken");

        ResponseEntity<?> response = authController.register(user, jwtMock);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(((Map) response.getBody()).containsKey("message"));
        assertEquals("User registered successfully", ((Map) response.getBody()).get("message"));

        verify(authService, times(1)).register(eq(user), eq("mockJwtToken"));
    }
}