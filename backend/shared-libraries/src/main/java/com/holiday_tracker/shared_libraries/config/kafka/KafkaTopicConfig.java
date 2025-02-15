package com.holiday_tracker.shared_libraries.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {
    @Bean
    public NewTopic holidayEventsTopic() {
        return new NewTopic("holiday-events", 1, (short) 1);
    }
    @Bean
    public NewTopic authEventsTopic() {
        return new NewTopic("auth-events", 1, (short) 1);
    }
    @Bean
    public NewTopic UserEventsTopic() {
        return new NewTopic("user-events", 1, (short) 1);
    }
}
