package com.holiday_tracker.user_service.repositories;

import com.holiday_tracker.user_service.models.UserActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserActivityRepository extends JpaRepository<UserActivityLog, Long> {
    List<UserActivityLog> findByUserName(String userName);

}