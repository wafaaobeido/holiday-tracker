package com.holiday_tracker.holiday_service.models;

import lombok.*;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class HolidayResponse {
    private Map<String, List<Holiday>> data;
    private Map<String, String> errors;
}

