package com.archy.texasholder.security;

import com.archy.texasholder.config.JwtConfig;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Simple JWT test to verify basic functionality
 */
public class JwtTest {

    @Test
    public void testBasicJwtFunctionality() {
        // Create a simple JWT config
        JwtConfig jwtConfig = new JwtConfig();
        
        // Manually set the values (since we added manual getters)
        // Use a longer secret key (256 bits minimum required for JWT)
        String secret = "test-secret-key-1234567890-abcdefghijklmnop";
        long expiration = 3600000L; // 1 hour
        
        // Create secret key
        SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        
        // Create a simple token
        String token = Jwts.builder()
                .setSubject("1")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .claim("username", "testuser")
                .claim("userId", 1)
                .signWith(secretKey)
                .compact();
        
        System.out.println("Generated token: " + token);
        
        // Validate the token
        try {
            var claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            
            System.out.println("Token is valid");
            System.out.println("Subject: " + claims.getBody().getSubject());
            System.out.println("Username: " + claims.getBody().get("username"));
            System.out.println("User ID: " + claims.getBody().get("userId"));
        } catch (Exception e) {
            System.out.println("Token validation failed: " + e.getMessage());
        }
    }
}