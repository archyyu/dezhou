package com.archy.dezhou.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 * JWT Configuration properties
 */
@Configuration
@Data
public class JwtConfig {
    
    @Value("${jwt.secret:dezhou-secret-key-1234567890-abcdefghijklmnop}") // 256+ bits for security
    private String secret;
    
    @Value("${jwt.expiration:86400000}") // 24 hours in milliseconds
    private long expiration;
    
    @Value("${jwt.header:Authorization}")
    private String header;
    
    @Value("${jwt.prefix:Bearer }")
    private String prefix;
    
    // Manually add getters in case Lombok isn't working
    public String getSecret() {
        return secret;
    }
    
    public long getExpiration() {
        return expiration;
    }
    
    public String getHeader() {
        return header;
    }
    
    public String getPrefix() {
        return prefix;
    }
}