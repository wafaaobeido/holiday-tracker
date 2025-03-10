package com.holiday_tracker.auth_service.kafka;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.holiday_tracker.shared_libraries.events.UserActivityEvent;
import com.holiday_tracker.shared_libraries.config.kafka.KafkaProducer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Instant;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class KafkaProducerTest {
    @Container
    public static KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.3.1")
            .asCompatibleSubstituteFor("apache/kafka"));

    @InjectMocks
    private KafkaProducer kafkaProducer;
    @MockitoBean
    private KafkaConsumer<String, UserActivityEvent> consumer;
    @Mock
    private KafkaTemplate<String, UserActivityEvent> kafkaTemplate;
    @Mock
    private ObjectMapper objectMapper;
    private Logger logger;
    private ListAppender<ILoggingEvent> listAppender;

    @BeforeAll
    static void startKafka() {
        kafkaContainer.start();
    }
    @AfterAll
    static void stopKafka() {
        kafkaContainer.stop();
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        kafkaProducer = new KafkaProducer(kafkaTemplate);

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "test-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList("user-login-topic"));

        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        logger = (Logger) LoggerFactory.getLogger(KafkaProducer.class);

        listAppender = new ListAppender<>();
    }

    @Test
    void sendMessage_ShouldPublishMessageToKafka() throws JsonProcessingException {
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