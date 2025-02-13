package com.holiday_tracker.auth_service.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String email;
    private String password;
}