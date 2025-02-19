package com.holiday_tracker.auth_service.exceptions;

public class UserInfoMismatchException extends RuntimeException {
    public UserInfoMismatchException(String message) {
        super(message);
    }

    public UserInfoMismatchException(String message, Throwable cause) {
        super(message, cause);
    }
}
