package com.holiday_tracker.holiday_service.kafka;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class KafkaContainerSetup {
    // Declare your Kafka container using asCompatibleSubstituteFor if needed:
    private static final DockerImageName KAFKA_IMAGE = DockerImageName.parse("confluentinc/cp-kafka:7.3.1")
            .asCompatibleSubstituteFor("apache/kafka");

    public static KafkaContainer kafkaContainer = new KafkaContainer(KAFKA_IMAGE);

    @BeforeAll
    public static void startContainer() {
        kafkaContainer.start();
    }

    @AfterAll
    public static void stopContainer() {
        kafkaContainer.stop();
    }

    public static String getBootstrapServers() {
        return kafkaContainer.getBootstrapServers();
    }
}