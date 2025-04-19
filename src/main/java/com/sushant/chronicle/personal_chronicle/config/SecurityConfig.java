package com.sushant.chronicle.personal_chronicle.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz.requestMatchers(HttpMethod.GET, "/api/entries/**", "/api/entries").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs").permitAll()
                .anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults())
            .csrf(AbstractHttpConfigurer::disable);


        return http.build();
    }
    
}
