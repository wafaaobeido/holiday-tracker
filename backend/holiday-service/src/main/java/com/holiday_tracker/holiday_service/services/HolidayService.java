package com.holiday_tracker.holiday_service.services;

import com.holiday_tracker.holiday_service.exceptions.HolidayServiceException;
import com.holiday_tracker.holiday_service.models.Holiday;
import com.holiday_tracker.holiday_service.models.HolidayCount;
import com.holiday_tracker.holiday_service.models.HolidayResponse;
import com.holiday_tracker.holiday_service.utils.SecurityUtil;
import com.holiday_tracker.shared_libraries.config.kafka.KafkaProducer;
import com.holiday_tracker.shared_libraries.events.UserActivityEvent;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class HolidayService {

    private static final Logger logger = LoggerFactory.getLogger(HolidayService.class);
    private static final String API_URL_TEMPLATE = "https://date.nager.at/api/v3/PublicHolidays/{year}/{countryCode}";
    private final KafkaProducer kafkaProducer;
    private RestTemplate restTemplate;
    public HolidayService(KafkaProducer kafkaProducer,
                          RestTemplate restTemplate)
    {
        this.kafkaProducer = kafkaProducer;
        this.restTemplate = restTemplate;
    }

    @PostConstruct
    public void init() {
        logger.info("HolidayService initialized");
    }

    /**
     *  Fetch public holidays and cache them in Redis.
     *  Cache Key: "holidays_{year}_{countryCode}"
     */
    @Cacheable(value = "holidays", key = "year + '_' + countryCode", unless = "result == null or result.isEmpty()")
    public List<Holiday> getPublicHolidays(int year, String countryCode) {
        try {
            if (year <= 0 || countryCode == null || countryCode.isBlank()) {
                throw new IllegalArgumentException("Invalid parameters: year=" + year + ", countryCode=" + countryCode);
            }
            logger.info( String.format(" 🔵 Fetching holidays from external API for country: %s and year: %s", countryCode, year));
            ResponseEntity<Holiday[]> response = restTemplate.getForEntity(API_URL_TEMPLATE, Holiday[].class, year, countryCode);
            Holiday[] holidays = response.getBody();
            if(holidays == null)throw new HolidayServiceException("No data received from external API for country: " + countryCode);

            List<Holiday> holidayList = Arrays.asList(holidays);
            return holidayList;
        } catch (Exception e) {
            logger.error( String.format(" 🔴 Error fetching holidays for %s: %s", countryCode, e.getMessage()));
            throw new HolidayServiceException(" 🔴 Error fetching holidays", e);
        }
    }

    /**
     * Evict cache (clear Redis) for all holidays.
     */
    @CacheEvict(value = "holidays", allEntries = true)
    public void refreshHolidays() {
        System.out.println("Cache cleared for holidays");
    }

    public HolidayResponse getAll(List<String> countryCodes) {

        Map<String, List<Holiday>> result = new HashMap<>();
        Map<String, String> errorMap = new HashMap<>();

        LocalDate today = LocalDate.now();
        int currentYear = today.getYear();

        for (String code : countryCodes) {
            try {
                List<Holiday> holidays = getPublicHolidays(currentYear, code);
                List<Holiday> filtered = holidays.stream()
                        .filter(h ->  h.getDate().isBefore(today))
                        .sorted(Comparator.comparing(Holiday::getDate).reversed())
                        .collect(Collectors.toList());
                result.put(code, filtered);
            } catch (Exception e){
                logger.error( String.format("Failed to process country %s: %s", code, e.getMessage()));
                errorMap.put(code,  String.format("Failed to process country %s: %s" , code , e.getMessage()));
            }
        }
        String userName = Optional.ofNullable(SecurityUtil.getUserName()).orElse("anonymousUser");

        result.forEach((code,holiday) -> {
            kafkaProducer.sendMessage("holiday-events", new UserActivityEvent(userName,"Fetched "
                    + result.size() + " holidays in " + currentYear + " for country " + code));
        });

        return new HolidayResponse(result, errorMap);
    }

    public HolidayResponse getLastCelebratedHolidays(int limitPerCountry, List<String> countryCodes) {

        Map<String, List<Holiday>> result = new HashMap<>();
        Map<String, String> errorMap = new HashMap<>();

        LocalDate today = LocalDate.now();
        int currentYear = today.getYear();
        for (String code : countryCodes) {
            try {
                List<Holiday> holidays = getPublicHolidays(currentYear, code);
                List<Holiday> filtered = holidays.stream()
                        .filter(h ->  h.getDate().isBefore(today))
                        .sorted(Comparator.comparing(Holiday::getDate).reversed())
                        .limit(limitPerCountry)
                        .collect(Collectors.toList());
                result.put(code, filtered);
            } catch (Exception e){
                logger.error( String.format("Failed to process country %s: %s", code, e.getMessage()));
                errorMap.put(code,  String.format("Failed to process country %s: %s" , code , e.getMessage()));
            }
        }
        String userName = Optional.ofNullable(SecurityUtil.getUserName()).orElse("anonymousUser");

        result.forEach((code,holiday) -> {
            kafkaProducer.sendMessage("holiday-events", new UserActivityEvent(userName,"Fetched "
                + result.size() + " holidays in " + currentYear + " for country " + code));
        });

        return new HolidayResponse(result, errorMap);
    }

    public List<HolidayCount> countWeekdayHolidays(int year, List<String> countryCodes) {

        List<HolidayCount> holidayList = countryCodes.stream().map(code -> {
                                            List<Holiday> holidays = getPublicHolidays(year, code);
                                            long count = holidays.stream().filter(h -> {
                                                LocalDate date = h.getDate();
                                                return !(date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY);
                                            }).count();
                                            return new HolidayCount(code, count);
                                        }).sorted(Comparator.comparingLong(HolidayCount::getCount).reversed())
                                        .collect(Collectors.toList());
        String userName = Optional.ofNullable(SecurityUtil.getUserName()).orElse("anonymousUser");
        holidayList.forEach(codeCount -> {
            kafkaProducer.sendMessage("holiday-events", new UserActivityEvent(userName, "Fetched " +
                    codeCount.getCount() + " holidays in " + year +
                    " for country " + codeCount.getCountryCode()));
        });
        return holidayList;
    }

    public HolidayResponse someWeekdayHolidays(int year, List<String> countryCodes, long limitPerCountry) {
        Map<String, List<Holiday>> result = new HashMap<>();
        Map<String, String> errorMap = new HashMap<>();
        for (String code : countryCodes) {
                    try {
                        List<Holiday> holidays = getPublicHolidays(year, code);
                        List<Holiday> filtered = holidays.stream()
                                .filter(h -> {
                                    LocalDate date = h.getDate();
                                    return !(date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY);
                                })
                                .sorted(Comparator.comparing(Holiday::getDate).reversed())
                                .limit(limitPerCountry)
                                .collect(Collectors.toList());
                        result.put(code, filtered);
                    } catch (Exception e){
                        logger.error( String.format("Failed to process country %s: %s", code, e.getMessage()));
                        errorMap.put(code,  String.format("Failed to process country %s: %s" , code , e.getMessage()));
                    }
            }
        String userName = Optional.ofNullable(SecurityUtil.getUserName()).orElse("anonymousUser");

        result.forEach((code,holiday) -> {
            kafkaProducer.sendMessage("holiday-events", new UserActivityEvent(userName,"Fetched " +
                    holiday.size() + " holidays in " + year + " for country " + code));

        });

        return new HolidayResponse(result, errorMap);
    }

    public List<Holiday> getCommonHolidays(int year, String country1, String country2) {
        List<Holiday> holidays1 = getPublicHolidays(year, country1);
        List<Holiday> holidays2 = getPublicHolidays(year, country2);

        List<LocalDate> dates2 = holidays2.stream()
                .map(Holiday::getDate)
                .toList();

        List<Holiday> holidayList = holidays1.stream()
                .filter(h -> dates2.contains(h.getDate()))
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(Holiday::getDate, h -> h, (h1, h2) -> {
                            // Merge local names if duplicate date found
                            h1.setLocalName(h1.getLocalName() + " / " + h2.getLocalName());
                            return h1;
                        }),
                        map -> map.values().stream()
                                .sorted(Comparator.comparing(Holiday::getDate))
                                .collect(Collectors.toList())
                ));

        String userName = Optional.ofNullable(SecurityUtil.getUserName()).orElse("anonymousUser");
        kafkaProducer.sendMessage("holiday-events",
                new UserActivityEvent(userName,"Fetched "
                        + holidayList.size() + " holidays in " + year +
                " for country " + country1 + ", and country " + country2));

        return holidayList;
    }


}
