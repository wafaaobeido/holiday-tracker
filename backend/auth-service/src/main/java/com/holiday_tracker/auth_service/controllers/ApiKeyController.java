package com.holiday_tracker.auth_service.controllers;

import com.holiday_tracker.auth_service.services.ApiKeyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
@RestController
@RequestMapping("/key")
public class ApiKeyController {

    private final ApiKeyService apiKeyService;

    public ApiKeyController(ApiKeyService apiKeyService) {
        this.apiKeyService = apiKeyService;
    }

    @GetMapping
    public ResponseEntity<Map<String, String>> getApiKey() {
        return ResponseEntity.ok(Map.of("apikey", apiKeyService.getOrCreateApiKey()));
    }
}