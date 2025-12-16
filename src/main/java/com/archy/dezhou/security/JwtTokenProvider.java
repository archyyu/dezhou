package com.archy.dezhou.security;

import com.archy.dezhou.config.JwtConfig;
import com.archy.dezhou.entity.Player;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT Token Provider for generating and validating JWT tokens
 */
@Component
public class JwtTokenProvider {
    
    private final JwtConfig jwtConfig;
    private final SecretKey secretKey;
    
    public JwtTokenProvider(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
        this.secretKey = Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8));
    }
    
    /**
     * Generate JWT token for authenticated user
     */
    public String generateToken(Authentication authentication) {
        Player player = (Player) authentication.getPrincipal();
        
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtConfig.getExpiration());
        
        return Jwts.builder()
                .setSubject(Long.toString(player.getUid()))
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .claim("username", player.getAccount())
                .claim("userId", player.getUid())
                .signWith(secretKey)
                .compact();
    }
    
    /**
     * Generate JWT token for user
     */
    public String generateToken(Player player) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtConfig.getExpiration());
        
        return Jwts.builder()
                .setSubject(Long.toString(player.getUid()))
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .claim("username", player.getAccount())
                .claim("userId", player.getUid())
                .signWith(secretKey)
                .compact();
    }
    
    /**
     * Get user ID from JWT token
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        
        return Long.parseLong(claims.getSubject());
    }
    
    /**
     * Get username from JWT token
     */
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        
        return claims.get("username", String.class);
    }
    
    /**
     * Validate JWT token
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            // Invalid JWT signature
            return false;
        } catch (ExpiredJwtException e) {
            // Expired token
            return false;
        } catch (UnsupportedJwtException e) {
            // Unsupported token
            return false;
        } catch (IllegalArgumentException e) {
            // Empty token
            return false;
        }
    }
}