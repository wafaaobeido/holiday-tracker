package com.holiday_tracker.auth_service.security;

import com.holiday_tracker.auth_service.controllers.AuthController;
import com.holiday_tracker.auth_service.models.User;
import com.holiday_tracker.auth_service.services.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestPropertySource(properties = {
        "spring.autoconfigure.exclude= KafkaAutoConfiguration.class",
        "spring.profiles.active=test"
})
@WebMvcTest(AuthController.class)
class AuthSecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Test
    void register_ShouldReturn200_WhenValidUserAndJwtProvided() throws Exception {
        doNothing().when(authService).register(any(User.class), anyString());

        mockMvc.perform(post("/auth/register")
                        .with(jwt().jwt(builder -> builder
                                .tokenValue("mock-token")
                                .claim("preferred_username", "johndoe")
                                .claim("roles", List.of("USER"))
                        ))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "firstname": "John",
                                "lastname": "Doe",
                                "username": "johndoe",
                                "email": "johndoe@example.com",
                                "password": "password123"
                            }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully"));
    }

    @Test
    void register_ShouldReturn401_WhenJwtNotProvided() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "firstname": "John",
                                "lastname": "Doe",
                                "username": "johndoe",
                                "email": "johndoe@example.com",
                                "password": "password123"
                            }
                        """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void register_ShouldReturn400_WhenRequestBodyIsEmpty() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .with(jwt().jwt(builder -> builder
                                .tokenValue("mock-token")
                                .claim("preferred_username", "johndoe")
                                .claim("roles", List.of("USER"))
                        ))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isBadRequest());
    }
}