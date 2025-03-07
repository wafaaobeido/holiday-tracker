package com.holiday_tracker.user_service.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

@Testcontainers
@EmbeddedKafka(partitions = 1, topics = "user-activity-events")
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
@TestPropertySource(properties = "spring.profiles.active=test")
public class KafkaConsumerTest extends KafkaContainerSetup {

    @Test
    public void testConsumeMessage() {
        // Set up consumer properties
        Properties consumerProps = new Properties();
        consumerProps.put("bootstrap.servers", getBootstrapServers());
        consumerProps.put("group.id", "test-group");
        consumerProps.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        consumerProps.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        consumerProps.put("auto.offset.reset", "earliest");

        // Create the consumer
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerProps);
        consumer.subscribe(Collections.singletonList("test-topic"));

        // Poll for messages (allowing some time for the message to be delivered)
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(5));


        consumer.close();
    }
}
