package com.holiday_tracker.shared_libraries.dto;

public class HolidayDTO {
    private String countryCode;
    private String date;
    private String localName;
    public HolidayDTO(String countryCode, String date, String localName) {
        this.countryCode = countryCode;
        this.date = date;
        this.localName = localName;
    }

    public String getCountryCode() { return countryCode; }
    public String getDate() { return date; }
    public String getLocalName() { return localName; }
}
