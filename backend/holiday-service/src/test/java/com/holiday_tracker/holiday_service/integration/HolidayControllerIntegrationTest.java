package com.holiday_tracker.holiday_service.integration;

import com.holiday_tracker.holiday_service.controllers.HolidayController;
import com.holiday_tracker.holiday_service.models.Holiday;
import com.holiday_tracker.holiday_service.models.HolidayResponse;
import com.holiday_tracker.holiday_service.services.HolidayService;
import com.holiday_tracker.shared_libraries.config.kafka.KafkaProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestPropertySource(properties = "spring.profiles.active=test")
@WebMvcTest(HolidayController.class)
@ExtendWith(MockitoExtension.class)
class HolidayControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private KafkaProducer kafkaProducer;

    @MockitoBean
    private RedisTemplate redisTemplate;

    @MockitoBean
    private CacheManager cacheManager;
    @MockitoBean
    private HolidayService holidayService;

    @InjectMocks
    private HolidayController holidayController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(holidayController).build();
    }

    @Test
    void testGetAll_SingleCountryCode() throws Exception {
        String countryCode = "US";
        HolidayResponse mockResponse = new HolidayResponse(Map.of(countryCode, List.of(new Holiday("New Year", LocalDate.of(2025,01,01)))), new HashMap<>());

        when(holidayService.getAll(List.of(countryCode))).thenReturn(mockResponse);

        mockMvc.perform(get("/holidays/all/US"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.US[0].name").value("New Year"))
                .andExpect(jsonPath("$.data.US[0].date").value("2025-01-01"));

        verify(holidayService, times(1)).getAll(List.of(countryCode));
    }

    @Test
    void testGetAll_MultipleCountryCodes() throws Exception {
        String countryCodes = "US,FR";
        List<String> countryList = Arrays.asList("US", "FR");
        HolidayResponse mockResponse = new HolidayResponse(
                Map.of(
                        "US", List.of(new Holiday("New Year", LocalDate.of(2025,01,01))),
                        "FR", List.of(new Holiday("Bastille Day", LocalDate.of(2025,07,14)))
                ),
                new HashMap<>()
        );

        when(holidayService.getAll(countryList)).thenReturn(mockResponse);

        mockMvc.perform(get("/holidays/all/US,FR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.US[0].name").value("New Year"))
                .andExpect(jsonPath("$.data.FR[0].name").value("Bastille Day"));

        verify(holidayService, times(1)).getAll(countryList);
    }

    @Test
    void testGetAll_WithErrorResponse() throws Exception {
        String countryCode = "DE";
        HolidayResponse mockResponse = new HolidayResponse(
                new HashMap<>(),
                Map.of("DE", "Failed to process country DE: API error")
        );

        when(holidayService.getAll(List.of(countryCode))).thenReturn(mockResponse);

        mockMvc.perform(get("/holidays/all/DE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errors.DE").value("Failed to process country DE: API error"));

        verify(holidayService, times(1)).getAll(List.of(countryCode));
    }

    @Test
    void testGetAll_EmptyCountryCode() throws Exception {
        HolidayResponse mockResponse = new HolidayResponse(new HashMap<>(), new HashMap<>());

        when(holidayService.getAll(Collections.emptyList())).thenReturn(mockResponse);

        mockMvc.perform(get("/holidays/all/"))
                .andExpect(status().isNotFound());

        verify(holidayService, never()).getAll(anyList());

    }
}