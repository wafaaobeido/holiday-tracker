package com.holiday_tracker.shared_libraries.config.kafka;

import com.holiday_tracker.shared_libraries.events.UserActivityEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducer {
    private static final Logger logger = LoggerFactory.getLogger(KafkaProducer.class);
    private final KafkaTemplate<String, UserActivityEvent> kafkaTemplate;

    public KafkaProducer(KafkaTemplate<String, UserActivityEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String topic, UserActivityEvent userActivityEvent) {
        logger.info("🔵 Sending event to topic '{}': {}", topic, userActivityEvent);
        kafkaTemplate.send(topic, userActivityEvent);
        logger.info(" 🤢 Message sent to topic: '{}' | Partition: {} | Offset: {}", topic, userActivityEvent);

    }
}
