package com.holiday_tracker.auth_service.services;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static java.rmi.server.LogStream.log;

@Service
@Slf4j
public class ApiKeyService {

    private final RestTemplate restTemplate;
    @Value(value = "${spring.kong.admin-url}")
    String KONG_ADMIN_URL;
    private final String CONSUMER_NAME = "userAdmin";

    public ApiKeyService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getOrCreateApiKey() {
        if (!consumerExists("userAdmin")) {
            createConsumer();
        }
        return getOrCreateKongApiKey();
    }

    private boolean consumerExists(String username) {
        String url = KONG_ADMIN_URL + "/consumers/" + username;

        // Create headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        log("Sending GET request to: " + url);

        // Encode form data
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(headers);

        try {
            // Send request
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);

            log("Response: " + response.getBody());

            return response.getStatusCode() == HttpStatus.OK;
        } catch (HttpClientErrorException e){
            log("Error: " + e.getMessage());
            return false;
        }

    }

    private void createConsumer() {
        String url = KONG_ADMIN_URL + "/consumers";
        JSONObject request = new JSONObject();
        request.put("username", CONSUMER_NAME);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<String> entity = new HttpEntity<>(request.toString(), headers);
        restTemplate.postForEntity(url, entity, String.class);
    }

    private String getOrCreateKongApiKey() {
        String url = KONG_ADMIN_URL + "/consumers/" + CONSUMER_NAME + "/key-auth";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        log("Sending POST request to: " + url);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        log("Response: " + response.getBody());

        if (response.getStatusCode() == HttpStatus.CREATED) {
            JSONObject jsonResponse = new JSONObject(response.getBody());
            return jsonResponse.getString("key");
        } else {
            throw new RuntimeException(" Failed to generate API key");
        }

    }
}
