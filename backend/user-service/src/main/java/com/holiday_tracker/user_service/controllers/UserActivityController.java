package com.holiday_tracker.user_service.controllers;

import com.holiday_tracker.user_service.models.UserActivityLog;
import com.holiday_tracker.user_service.services.UserActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserActivityController {
    @Autowired
    private  UserActivityService userActivityService;

    public UserActivityController(UserActivityService userActivityService) {
        this.userActivityService=userActivityService;
    }

    @GetMapping("/{userName}")
    public List<UserActivityLog> getUserActivity(@PathVariable("userName") String userName) {
        return userActivityService.getUserActivities(userName);
    }

    @GetMapping("/all")
    public List<UserActivityLog> getAllUserActivities() {
        return userActivityService.getAllUserActivities();
    }
}

