package com.holiday_tracker.auth_service.models;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private String firstname;
    private String lastname;
    private String username;
    private String email;
    private String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
}