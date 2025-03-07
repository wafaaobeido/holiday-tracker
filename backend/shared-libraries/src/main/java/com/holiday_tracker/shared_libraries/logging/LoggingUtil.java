package com.holiday_tracker.shared_libraries.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingUtil.class);

    public static void logInfo(String message) {
        LOGGER.info("[INFO] " + message);
    }

    public static void logWarn(String message) {
        LOGGER.warn("[WARN] " + message);
    }

    public static void logError(String message, Exception e) {
        LOGGER.error("[ERROR] " + message, e);
    }
}
