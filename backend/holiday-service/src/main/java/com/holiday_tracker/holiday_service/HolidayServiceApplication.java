package com.holiday_tracker.holiday_service;

//import com.holiday_tracker.holiday_service.config.JwtConfig;

import com.holiday_tracker.holiday_service.config.JwtConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
@EnableCaching
@SpringBootApplication
@ComponentScan(basePackages = {
		"com.holiday_tracker.holiday_service",
		"com.holiday_tracker.shared_libraries.config.kafka",
		"com.holiday_tracker.shared_libraries.config.swagger"
})
@EnableConfigurationProperties(JwtConfig.class)
public class HolidayServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(HolidayServiceApplication.class, args);
	}

}
