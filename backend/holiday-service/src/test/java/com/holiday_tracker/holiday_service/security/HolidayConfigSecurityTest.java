package com.holiday_tracker.holiday_service.security;

import com.holiday_tracker.holiday_service.controllers.HolidayController;
import com.holiday_tracker.holiday_service.services.HolidayService;
import com.holiday_tracker.shared_libraries.config.kafka.KafkaProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestPropertySource(properties = {"spring.profiles.active=test, spring.autoconfigure.exclude= KafkaAutoConfiguration.class" })
@Import(HolidaySecurityConfig.class)
@ExtendWith(MockitoExtension.class)
class HolidayConfigSecurityTest {

    @Autowired
    private MockMvc mockMvc;
    @Mock
    private KafkaProducer kafkaProducer;
    @Mock
    private HolidayService holidayService;
    @MockitoBean
    private SecurityFilterChain securityFilterChain;
    @InjectMocks
    private HolidayController holidayController;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(holidayController).build();
    }
    @Test
    void shouldAllowToAccessEndpoints() throws Exception {
        mockMvc.perform(get("/holidays/last/US/3"))
                .andExpect(status().isOk());
    }

}