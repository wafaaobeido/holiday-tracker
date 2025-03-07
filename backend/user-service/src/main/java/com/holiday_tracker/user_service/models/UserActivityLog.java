package com.holiday_tracker.user_service.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.Instant;

@Entity
public class UserActivityLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty("userName")
    private String userName;
    @JsonProperty("content")
    private String content;

    @JsonProperty("timestamp")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "UTC")
    private Instant timestamp;

    public UserActivityLog() {
    }

    public UserActivityLog(String userName, String content, Instant timestamp) {
        this.userName = userName;
        this.content = content;
        this.timestamp = timestamp;
    }
    public String getUserName() { return userName; }
    public String getContent() { return content; }
    public Instant getTimestamp() { return timestamp; }


}
