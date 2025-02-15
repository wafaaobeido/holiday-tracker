package com.holiday_tracker.holiday_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = "spring.profiles.active=test")
class HolidayServiceApplicationTests {
	@Test
	void contextLoads() {
	}
}