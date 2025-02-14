package com.holiday_tracker.user_service.controllers;

import com.holiday_tracker.user_service.models.UserActivityLog;
import com.holiday_tracker.user_service.services.UserActivityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@TestPropertySource(properties = "spring.profiles.active=test")
class UserActivityIntegrationTest {

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
    void getUserActivity_ShouldReturnUserLogs() throws Exception {
        List<UserActivityLog> logs = List.of(new UserActivityLog("user123", "Logged in", Instant.now()));

        when(userActivityService.getUserActivities("user123")).thenReturn(logs);

        mockMvc.perform(get("/users/user123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userName").value("user123"));
    }

    @Test
    void getAllUserActivities_ShouldReturnAllLogs() throws Exception {
        when(userActivityService.getAllUserActivities()).thenReturn(List.of());

        mockMvc.perform(get("/users/all"))
                .andExpect(status().isOk());
    }
}