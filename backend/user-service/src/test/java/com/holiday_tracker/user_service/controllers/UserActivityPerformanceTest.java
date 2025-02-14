package com.holiday_tracker.user_service.controllers;

import com.holiday_tracker.user_service.models.UserActivityLog;
import com.holiday_tracker.user_service.repositories.UserActivityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.stream.IntStream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@ExtendWith(MockitoExtension.class)
@WebMvcTest(UserActivityController.class)
@TestPropertySource(properties = "spring.profiles.active=test")
public class UserActivityPerformanceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserActivityRepository repository;

    @MockitoBean
    UserActivityController controller;
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        repository.deleteAll();
        IntStream.range(0, 1000).forEach(i -> repository.save(
                new UserActivityLog( "user123", "Action " + i, Instant.now()))
        );
    }

    @Test
    void shouldHandleHighLoad() throws Exception {
        mockMvc.perform(get("/users/user123"))
                .andExpect(status().isOk());
    }
}