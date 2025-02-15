package com.holiday_tracker.shared_libraries.events;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
public class UserActivityEvent {

    @JsonProperty("userName")
    private String userName;

    @JsonProperty("content")
    private String content;

    @JsonProperty("timestamp")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "UTC")
    private Instant timestamp;

    public UserActivityEvent(String userName, String content) {
        this.userName = userName;
        this.content = content;
        this.timestamp = Instant.now();
    }

    public String getUserName() { return userName; }
    public String getContent() { return content; }
    public Instant getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        return "CustomKafkaMessage{" +
                ", content='" + content + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", user id='" + userName + '\'' +
                '}';
    }
}
