package com.archy.dezhou.config;

import com.archy.dezhou.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

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
                    "/actuator/**"
                ).permitAll() // Public endpoints
                .requestMatchers(
                    "/api/v1/user/**",
                    "/api/v1/game/**",
                    "/api/v1/room/**",
                    "/api/v1/test/protected"
                ).authenticated() // Protected endpoints
                .anyRequest().authenticated() // All other requests require authentication
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}