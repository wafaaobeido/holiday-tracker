package com.holiday_tracker.holiday_service.controllers;

import com.holiday_tracker.holiday_service.models.Holiday;
import com.holiday_tracker.holiday_service.models.HolidayCount;
import com.holiday_tracker.holiday_service.models.HolidayResponse;
import com.holiday_tracker.holiday_service.services.HolidayService;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/holidays")
public class HolidayController {

    private final HolidayService holidayService;

    public HolidayController(HolidayService holidayService) {
        this.holidayService = holidayService;
    }


    @GetMapping("/all/{countryCode}")
    public HolidayResponse getAll( @PathVariable("countryCode") String countryCodes) {
        List<String> codes = Arrays.stream(countryCodes.split(","))
                .map(String::trim)
                .collect(Collectors.toList());
        return holidayService.getAll(codes);
    }


    @GetMapping("/last/{countryCode}/{number}")
    public HolidayResponse getLastCelebratedHolidays( @PathVariable("number") int number,
                                                      @PathVariable("countryCode") String countryCodes) {
        List<String> codes = Arrays.stream(countryCodes.split(","))
                .map(String::trim)
                .collect(Collectors.toList());
        return holidayService.getLastCelebratedHolidays(number, codes);
    }


    @GetMapping("/weekdaysHolidayCount")
    public List<HolidayCount> countWeekdayHolidays( @RequestParam("year") int year,
                                                    @RequestParam("countryCode") String countryCodes) {
        List<String> codes = Arrays.stream(countryCodes.split(","))
                .map(String::trim)
                .collect(Collectors.toList());
        return holidayService.countWeekdayHolidays(year, codes);
    }

    @GetMapping("/weekdaysHoliday")
    public HolidayResponse someWeekdayHolidays(@RequestParam("year") int year,
                                               @RequestParam("countryCode") String countryCodes,
                                               @RequestParam("numberOfHolidays")long numberOfHolidays) {
        List<String> codes = Arrays.stream(countryCodes.split(","))
                .map(String::trim)
                .collect(Collectors.toList());
        return holidayService.someWeekdayHolidays(year, codes, numberOfHolidays);
    }

    @GetMapping("/common")
    public List<Holiday> getCommonHolidays(@RequestParam("year") int year,
                                           @RequestParam("country1") String country1,
                                           @RequestParam("country2") String country2) {
        return holidayService.getCommonHolidays(year, country1, country2);
    }

    /**
     * Fetch holidays and store them in Redis.
     */
    @GetMapping("/cache-test/{year}/{countryCode}")
    public List<Holiday> testRedisCaching(@PathVariable("year") int year,
                                          @PathVariable("countryCode") String countryCode) {
        return holidayService.getPublicHolidays(year, countryCode);
    }

    /**
     * Clear Redis cache.
     */
    @PostMapping("/refresh-cache")
    public String clearCache() {
        holidayService.refreshHolidays();
        return " Holiday cache cleared!";
    }

}
