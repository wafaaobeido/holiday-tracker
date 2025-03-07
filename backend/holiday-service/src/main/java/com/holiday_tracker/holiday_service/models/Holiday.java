package com.holiday_tracker.holiday_service.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Entity
@Table(name = "holidays")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Holiday {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;
    private String name;
    private String localName;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
    private String countryCode;

    public Holiday(String name, LocalDate date, String countryCode) {
        this.name = name;
        this.date = date;
        this.countryCode = countryCode;
    }

    public Holiday(String name, LocalDate date) {
        this.name = name;
        this.date = date;
    }
}