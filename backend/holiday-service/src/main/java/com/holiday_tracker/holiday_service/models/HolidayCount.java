package com.holiday_tracker.holiday_service.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HolidayCount {
    private String countryCode;
    private long count;


}
