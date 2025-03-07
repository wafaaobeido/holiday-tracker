package com.holiday_tracker.holiday_service.kafka;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.holiday_tracker.shared_libraries.config.kafka.KafkaProducer;
import com.holiday_tracker.shared_libraries.events.UserActivityEvent;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.kafka.test.context.EmbeddedKafka;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@EmbeddedKafka(partitions = 1, topics = "user-activity-events")
@ExtendWith(MockitoExtension.class)
public class KafkaProducerTest extends KafkaContainerSetup {
    @InjectMocks
    private KafkaProducer kafkaProducer;

    @Mock
    private KafkaTemplate<String, UserActivityEvent> kafkaTemplate;
    @Mock
    private ObjectMapper objectMapper;
    private Logger logger;
    private ListAppender<ILoggingEvent> listAppender;

    @BeforeAll
    static void startKafka(){
        startContainer();
    }
    @AfterAll
    static void stopKafka(){
        stopContainer();
    }

    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
        kafkaProducer = new KafkaProducer(kafkaTemplate);

        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        logger = (Logger) LoggerFactory.getLogger(KafkaProducer.class);

        listAppender = new ListAppender<>();

    }

    @Test
    public void testSendMessage() throws Exception {

        UserActivityEvent mockEvent = new UserActivityEvent("123", "User logged in", Instant.now());

        CompletableFuture future = CompletableFuture.completedFuture(mock(SendResult.class));

        when(kafkaTemplate.send(anyString(), any(UserActivityEvent.class))).thenReturn(future);

        listAppender.start();
        logger.addAppender(listAppender);

        kafkaProducer.sendMessage("test-topic", mockEvent);

        verify(kafkaTemplate, times(1)).send("test-topic", mockEvent);

        boolean found = listAppender.list.stream()
                .anyMatch(event -> event.getMessage().contains("🤢"));

        assertTrue(found, "Expected log message not found");
        // Clean up by detaching the appender
        logger.detachAppender(listAppender);
    }
}
