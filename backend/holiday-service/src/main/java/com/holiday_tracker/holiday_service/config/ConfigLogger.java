package com.holiday_tracker.holiday_service.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;


@Component
public class ConfigLogger {

    @Autowired
    private Environment env;

    @PostConstruct
    public void logKafkaConfig() {
        String bootstrapServers = env.getProperty("spring.kafka.bootstrap-servers");
        System.out.println("Kafka bootstrap servers: " + bootstrapServers);
    }
}