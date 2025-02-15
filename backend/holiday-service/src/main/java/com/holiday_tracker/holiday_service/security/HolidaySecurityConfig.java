package com.holiday_tracker.holiday_service.security;

import com.holiday_tracker.holiday_service.utils.SecurityFilter;
import com.holiday_tracker.shared_libraries.security.SecurityConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class HolidaySecurityConfig extends SecurityConfig{

    @Bean
    public SecurityFilterChain holidaySecurityFilterChain(HttpSecurity http, SecurityFilter securityFilter) throws Exception {

        http = http
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors(cors -> cors.configurationSource(corsConfigurationSource()));

        http
                .securityMatcher("/holidays/**")
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/v3/api-docs",
                                "/webjars/**",
                                "/swagger-resources/**",
                                "/configuration/**",
                                "/holidays/cache-test/**",
                                "/holidays/refresh-cache"
                        ).permitAll()

                        .requestMatchers("/").permitAll()

                        .requestMatchers("/holidays/**").authenticated()
                        .anyRequest().authenticated()

                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }




}
