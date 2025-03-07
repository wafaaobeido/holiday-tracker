package com.holiday_tracker.user_service.services;

import com.holiday_tracker.shared_libraries.events.UserActivityEvent;
import com.holiday_tracker.user_service.models.UserActivityLog;
import com.holiday_tracker.user_service.repositories.UserActivityRepository;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserActivityService {
    private final UserActivityRepository repository;
    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);
    public UserActivityService(UserActivityRepository repository) {
        this.repository = repository;
    }

    @KafkaListener(
            topics = "holiday-events",
            groupId = "user-service-group"
    )
    public void listenHolidayEvents(@Payload UserActivityEvent event,
                                    @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                    @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                                    @Header(KafkaHeaders.OFFSET) long offset,
                                    Acknowledgment acknowledgment) {
        try {
            logger.info(" 🤢 Received message: '{}' from user: '{}' at : {}",
                    event.getContent(), event.getUserName(), event.getTimestamp());
            logger.info(" The message: '{}' is from topic: '{}' | Partition: {} | Offset: {}",
                    event.getContent(), topic, partition, offset);
            repository.save(new UserActivityLog(event.getUserName(), event.getContent(), event.getTimestamp()));
            acknowledgment.acknowledge();
        } catch (Exception e) {
            System.err.println(" 🔴 Error processing message: {} " + e.getMessage());
        }
    }

    @KafkaListener(
            topics = "auth-events",
            groupId = "user-service-group"
    )
    public void listenAuthEvents(@Payload UserActivityEvent event,
                                  @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                  @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                                  @Header(KafkaHeaders.OFFSET) long offset,
                                 Acknowledgment acknowledgment) {
        try {
            logger.info(" 🤢 Received message: '{}' from user: '{}' at : {}",
                    event.getContent(), event.getUserName(), event.getTimestamp());
            logger.info(" The message: '{}' is from topic: '{}' | Partition: {} | Offset: {}",
                    event.getContent(), topic, partition, offset);
            repository.save(new UserActivityLog(event.getUserName(), event.getContent(), event.getTimestamp()));
            acknowledgment.acknowledge();
        } catch (Exception e) {
            System.err.println(" 🔴 Error processing message: {} " + e.getMessage());
        }
    }



    public List<UserActivityLog> getUserActivities(String userName) {
        return repository.findByUserName(userName);
    }

    public List<UserActivityLog> getAllUserActivities() {
        return repository.findAll();
    }

}
