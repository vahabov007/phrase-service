package com.vahabvahabov.phrase_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http.cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers("/api/v1/phrases/**").permitAll()
                                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll()
                                .anyRequest().authenticated()// Allow Swagger
                );

        return http.build();

    }

    // CORS
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOriginPatterns("*")
                        .allowedHeaders("*")
                        .allowedMethods("POST", "GET", "PATCH", "DELETE", "PUT")
                        .allowCredentials(true);
            }
        };
    }
}