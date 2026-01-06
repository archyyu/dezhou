package com.archy.texasholder.config;

import com.archy.texasholder.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;


import java.util.Arrays;
import java.util.List;

/**
 * Security Configuration for JWT-based authentication
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable) // Disable CSRF for API
            .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Enable CORS
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Stateless session for JWT
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/api/v1/user/login",
                    "/api/v1/user/register",
                    "/api/v1/user/auto-register",
                    "/api/v1/test/public",
                    "/api/v1/test/generate-token",
                    "/health",
                    "/actuator/**",
                    "/ws/**",           // WebSocket endpoint
                    "/ws"              // WebSocket endpoint (exact match)
                ).permitAll() // Public endpoints
                .requestMatchers(
                    "/api/v1/user/**",
                    "/api/v1/game/**",
                    "/api/v1/room/**"
                ).permitAll() // Temporarily permit all for testing
                .anyRequest().authenticated() // All other requests require authentication
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }

    /**
     * CORS Configuration for Security
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(
            "http://localhost:5173",  // Vue development server
            "http://localhost:5174",  // Alternative Vue port
            "http://localhost:3000",  // Common alternative
            "http://localhost:8080",  // Same origin
            "http://localhost:8880",  // Backend service port
            "http://localhost:8888",  // Additional development port
            "http://localhost:5173",  // Vue development server (duplicate for safety)
            "http://127.0.0.1:5173"   // Alternative localhost format
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * Ensure CORS filter is applied before JWT filter
     */
    @Bean
    public org.springframework.boot.web.servlet.FilterRegistrationBean<org.springframework.web.filter.CorsFilter> corsFilterRegistration() {
        org.springframework.boot.web.servlet.FilterRegistrationBean<org.springframework.web.filter.CorsFilter> registration = 
            new org.springframework.boot.web.servlet.FilterRegistrationBean<>();
        registration.setFilter(corsFilter());
        registration.setOrder(org.springframework.core.Ordered.HIGHEST_PRECEDENCE); // Apply CORS first
        registration.addUrlPatterns("/*");
        return registration;
    }

    /**
     * CORS Filter bean for explicit registration
     */
    @Bean
    public org.springframework.web.filter.CorsFilter corsFilter() {
        org.springframework.web.cors.UrlBasedCorsConfigurationSource source = 
            new org.springframework.web.cors.UrlBasedCorsConfigurationSource();
        org.springframework.web.cors.CorsConfiguration config = new org.springframework.web.cors.CorsConfiguration();
        
        config.setAllowedOrigins(java.util.List.of(
            "http://localhost:5173",
            "http://localhost:5174",
            "http://localhost:3000", 
            "http://localhost:8080",
            "http://localhost:8880",
            "http://localhost:8888",
            "http://127.0.0.1:5173"
        ));
        config.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(java.util.List.of("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);
        
        source.registerCorsConfiguration("/**", config);
        return new org.springframework.web.filter.CorsFilter(source);
    }
}