package com.holiday_tracker.holiday_service.controllers;

import com.holiday_tracker.holiday_service.models.Holiday;
import com.holiday_tracker.holiday_service.services.HolidayService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/holiday")
public class HolidayController {

    private final HolidayService holidayService;

    public HolidayController(HolidayService holidayService) {
        this.holidayService = holidayService;
    }

    @GetMapping("/{country}/{year}")
    public ResponseEntity<List<Holiday>> getHolidays(@PathVariable String country, @PathVariable int year) {
        return ResponseEntity.ok(holidayService.getHolidays(country, year));
    }
}
