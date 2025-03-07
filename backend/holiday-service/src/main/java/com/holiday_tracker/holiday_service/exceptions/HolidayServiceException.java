package com.holiday_tracker.holiday_service.exceptions;

public class HolidayServiceException extends RuntimeException{
    public HolidayServiceException(String message) {
        super(message);
    }

    public HolidayServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
