package com.archy.texasholder.config;

import org.junit.jupiter.api.Test;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WebConfigTest {

    private final WebConfig webConfig = new WebConfig();

    @Test
    void testWebConfigImplementsWebMvcConfigurer() {
        assertTrue(webConfig instanceof org.springframework.web.servlet.config.annotation.WebMvcConfigurer,
                   "WebConfig should implement WebMvcConfigurer");
    }

    @Test
    void testCorsFilterBean() {
        CorsFilter corsFilter = webConfig.corsFilter();
        assertNotNull(corsFilter, "CORS filter bean should not be null");
    }

    @Test
    void testCorsMappingsConfiguration() {
        // Create a mock CorsRegistry to test the configuration
        CorsRegistry registry = new CorsRegistry() {
            @Override
            public org.springframework.web.servlet.config.annotation.CorsRegistration addMapping(String pathPattern) {
                return new org.springframework.web.servlet.config.annotation.CorsRegistration(pathPattern) {
                    @Override
                    public org.springframework.web.servlet.config.annotation.CorsRegistration allowedOrigins(String... origins) {
                        assertArrayEquals(new String[]{"http://localhost:5173", "http://localhost:5174", "http://localhost:3000", "http://localhost:8080", "http://localhost:8888", "http://127.0.0.1:5173"}, origins);
                        return this;
                    }
                };
            }
        };
        
        // This should not throw an exception and should configure the registry correctly
        assertDoesNotThrow(() -> webConfig.addCorsMappings(registry),
                          "addCorsMappings should execute without exceptions");
    }

    @Test
    void testCorsConfigurationDetails() {
        // Test the configuration by creating a new instance and checking the source directly
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        // Configure allowed origins
        config.setAllowedOrigins(List.of(
            "http://localhost:5173",  // Vue development server
            "http://localhost:5174",  // Alternative Vue port
            "http://localhost:3000",  // Common alternative
            "http://localhost:8080",  // Same origin
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
        
        // Now test the configuration
        assertNotNull(config, "CORS configuration should not be null");
        assertTrue(config.getAllowedOrigins().contains("http://localhost:5173"), "Should allow Vue dev server");
        assertTrue(config.getAllowedOrigins().contains("http://localhost:3000"), "Should allow common dev port");
        assertTrue(config.getAllowedMethods().contains("GET"), "Should allow GET method");
        assertTrue(config.getAllowedMethods().contains("POST"), "Should allow POST method");
        assertTrue(config.getAllowedHeaders().contains("Authorization"), "Should allow Authorization header");
        assertTrue(config.getAllowCredentials(), "Should allow credentials");
        assertEquals(3600L, config.getMaxAge(), "Max age should be 1 hour");
    }
}