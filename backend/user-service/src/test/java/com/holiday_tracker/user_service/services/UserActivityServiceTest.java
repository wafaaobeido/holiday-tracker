package com.holiday_tracker.user_service.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.holiday_tracker.shared_libraries.events.UserActivityEvent;
import com.holiday_tracker.user_service.models.UserActivityLog;
import com.holiday_tracker.user_service.repositories.UserActivityRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.test.context.TestPropertySource;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
@TestPropertySource(properties = "spring.profiles.active=test")
@ExtendWith(MockitoExtension.class)
class UserActivityServiceTest {

    @Mock
    private UserActivityRepository repository;


    @InjectMocks
    private UserActivityService service;

    private UserActivityLog log;


    @BeforeEach
    void setUp() {log = new UserActivityLog( "user123", "Logged in", Instant.now());}

    @Test
    void consumeUserActivity_ShouldSaveEventToDatabase() throws JsonProcessingException {

        UserActivityEvent event = new UserActivityEvent("testuser", "Fetched holidays", Instant.now());

        ConsumerRecord<String, UserActivityEvent> record = new ConsumerRecord<>(
                "auth-events",
                0,
                10L,
                "key",
                event
        );

        Acknowledgment acknowledgment = mock(Acknowledgment.class);

        service.listenAuthEvents(record.value(), record.topic(), record.partition(), record.offset(), acknowledgment);

        verify(acknowledgment, times(1)).acknowledge();
        verify(repository, times(1)).save(any(UserActivityLog.class));
    }

    @Test
    void getUserActivities_ShouldReturnUserLogs() {
        when(repository.findByUserName("user123")).thenReturn(List.of(log));

        List<UserActivityLog> logs = service.getUserActivities("user123");

        assertFalse(logs.isEmpty());
        assertEquals("user123", logs.get(0).getUserName());
    }

    @Test
    void getAllUserActivities_ShouldReturnAllLogs() {
        when(repository.findAll()).thenReturn(List.of(log));

        List<UserActivityLog> logs = service.getAllUserActivities();

        assertFalse(logs.isEmpty());
    }
}