package com.holiday_tracker.holiday_service.repositories;

import com.holiday_tracker.holiday_service.models.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HolidayRepository extends JpaRepository<Holiday, Long> {
    // For demonstration, you could add custom queries as needed.
    List<Holiday> findByCountryCode(String countryCode);
}
