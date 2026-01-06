package com.archy.texasholder.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

/**
 * Web Configuration for CORS and other web-related settings
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

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
            "http://localhost:8080",  // Same origin
            "http://localhost:8880",  // Backend service port
            "http://localhost:8888"   // Additional development port
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

    /**
     * WebMvcConfigurer implementation for CORS configuration
     * This provides global CORS configuration for Spring MVC
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOrigins(
                "http://localhost:5173",
                "http://localhost:5174", 
                "http://localhost:3000",
                "http://localhost:8080",
                "http://localhost:8880",
                "http://localhost:8888",
                "http://127.0.0.1:5173"
            )
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(true)
            .maxAge(3600);
    }
}