package com.holiday_tracker.auth_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
		"com.holiday_tracker.auth_service",
		"com.holiday_tracker.shared_libraries.config.kafka",
		"com.holiday_tracker.shared_libraries.config.swagger"
})
public class AuthServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(AuthServiceApplication.class, args);
	}

}
