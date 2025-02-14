package com.holiday_tracker.user_service.security;

import com.holiday_tracker.user_service.controllers.UserActivityController;
import com.holiday_tracker.user_service.services.UserActivityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@TestPropertySource(properties = "spring.profiles.active=test")
@ExtendWith(MockitoExtension.class)
class UserActivitySecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private UserActivityService userActivityService;

    @InjectMocks
    private UserActivityController userActivityController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userActivityController).build();
    }

    @Test
    void getUserActivity_ShouldAllowAuthenticatedUser() throws Exception {
        mockMvc.perform(get("/users/user123"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldAllowAccessWithAuthenticatedUser() throws Exception {
        mockMvc.perform(get("/users/user123"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldHaveStatelessSessionManagement() throws Exception {
        mockMvc.perform(get("/users/user123"))
                .andExpect(header().doesNotExist("Set-Cookie")); // No session cookies should be set
    }
}