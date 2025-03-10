package com.holiday_tracker.auth_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;

@SpringBootTest
@ComponentScan(basePackages = {
		"com.holiday_tracker.auth_service",
		"com.holiday_tracker.shared_libraries.config.kafka"
})
class AuthServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
