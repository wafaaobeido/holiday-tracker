package com.holiday_tracker.holiday_service.kafka;

import com.holiday_tracker.shared_libraries.events.UserActivityEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

import static com.holiday_tracker.holiday_service.kafka.KafkaContainerSetup.getBootstrapServers;
import static com.holiday_tracker.holiday_service.kafka.KafkaContainerSetup.startContainer;

@Testcontainers
@EmbeddedKafka(partitions = 1, topics = "user-activity-events")
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
@TestPropertySource(properties = "spring.profiles.active=test")
class KafkaConsumerTest {

    @Test
    public void testConsumeMessage() {
        startContainer();
        // Set up consumer properties
        Properties consumerProps = new Properties();
        consumerProps.put(JsonDeserializer.VALUE_DEFAULT_TYPE, UserActivityEvent.class);
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, getBootstrapServers());
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "test-group");
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class.getName());
        consumerProps.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class.getName());
        consumerProps.put(JsonDeserializer.TRUSTED_PACKAGES, "com.holiday_tracker.shared_libraries.events");

        // Create the consumer
        KafkaConsumer<String, UserActivityEvent> consumer = new KafkaConsumer<>(consumerProps);
        consumer.subscribe(Collections.singletonList("test-topic"));

        // Poll for messages (allowing some time for the message to be delivered)
        ConsumerRecords<String, UserActivityEvent> records = consumer.poll(Duration.ofSeconds(5));

        // You can then add assertions here depending on what you expect.
        // For example, if you know a message should have been produced by your producer test:
        // assertFalse(records.isEmpty());

        consumer.close();
    }
}