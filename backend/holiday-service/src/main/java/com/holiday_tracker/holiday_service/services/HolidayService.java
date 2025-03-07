package com.holiday_tracker.holiday_service.services;

import com.holiday_tracker.holiday_service.models.Holiday;
import com.holiday_tracker.holiday_service.repositories.HolidayRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HolidayService {

    private final HolidayRepository holidayRepository;

    public HolidayService(HolidayRepository holidayRepository) {
        this.holidayRepository = holidayRepository;
    }

    public List<Holiday> getHolidays(String country, int year) {
        return null;
    }
}
