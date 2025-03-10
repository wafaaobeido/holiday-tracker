package com.holiday_tracker.auth_service.config;

import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakConfig {

//    private String issuerUri;
//
//    public String getJwkSetUri() {
//        return issuerUri + "/protocol/openid-connect/certs";
//    }


    @Bean
    public KeycloakSpringBootConfigResolver keycloakConfigResolver() {

        return new KeycloakSpringBootConfigResolver();
    }
}