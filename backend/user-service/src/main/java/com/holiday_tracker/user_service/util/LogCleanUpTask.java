package com.holiday_tracker.user_service.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class LogCleanUpTask {

    @Autowired
    private JdbcTemplate jdbcTemplate;


    @Scheduled(cron = "0 0 0 * * ?") // Run every day at midnight
    public void cleanupOldLogs() {
        String sql = "DELETE FROM user_db.user_activity_log WHERE timestamp < DATE_SUB(NOW(), INTERVAL 30 DAY)";
        int rowsDeleted = jdbcTemplate.update(sql);
        System.out.println("Deleted " + rowsDeleted + " old log entries.");
    }

    @Scheduled(cron = "0 30 0 * * ?") // runs daily at 12:30 AM
    public void maintainLogTableSize() {
        final int MAX_LOG_ROWS = 10000;

        Integer currentCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM user_db.user_activity_log", Integer.class);
        if (currentCount != null && currentCount > MAX_LOG_ROWS) {
            int rowsToDelete = (int) ((currentCount - MAX_LOG_ROWS) + (MAX_LOG_ROWS * 0.1));
            String deleteSql = "DELETE FROM user_db.user_activity_log WHERE id IN (SELECT id FROM user_db.user_activity_log ORDER BY timestamp ASC LIMIT ?)";
            int deleted = jdbcTemplate.update(deleteSql, rowsToDelete);
            System.out.println("Deleted " + deleted + " log entries to maintain log table size.");
        }
    }
}