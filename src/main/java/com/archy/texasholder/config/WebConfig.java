package com.archy.texasholder.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

/**
 * Web Configuration for CORS and other web-related settings
 */
@Configuration
public class WebConfig {

    /**
     * CORS Filter configuration
     * This provides an additional layer of CORS configuration
     */
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // Configure allowed origins
        config.setAllowedOrigins(List.of(
            "http://localhost:5173",  // Vue development server
            "http://localhost:5174",  // Alternative Vue port
            "http://localhost:3000",  // Common alternative
            "http://localhost:8080"   // Same origin
        ));

        // Configure allowed methods
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Configure allowed headers
        config.setAllowedHeaders(Arrays.asList(
            "Authorization",
            "Content-Type",
            "Accept",
            "X-Requested-With"
        ));

        // Allow credentials (for JWT authentication)
        config.setAllowCredentials(true);

        // Set max age for preflight requests
        config.setMaxAge(3600L); // 1 hour

        // Apply to all endpoints
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}