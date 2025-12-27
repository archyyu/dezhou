package com.archy.dezhou.security;

import com.archy.dezhou.config.JwtConfig;
import com.archy.dezhou.entity.Player;
import com.archy.dezhou.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

    private JwtConfig jwtConfig;
    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        // Create real JwtConfig instance instead of mocking
        jwtConfig = new JwtConfig();
        // Set a proper secret key for testing (256 bits minimum)
        jwtConfig.setSecret("test-secret-key-1234567890-abcdefghijklmnop");
        jwtConfig.setExpiration(3600000L); // 1 hour
        jwtTokenProvider = new JwtTokenProvider(jwtConfig);
    }

    @Test
    void testGenerateAndValidateToken() {
        // Create a test user
        User user = new User();
        user.setUid(1);
        user.setAccount("testuser");
        
        Player player = new Player(user);
        
        // Generate token
        String token = jwtTokenProvider.generateToken(player);
        
        assertNotNull(token);
        assertTrue(token.length() > 0);
        
        // Validate token
        boolean isValid = jwtTokenProvider.validateToken(token);
        assertTrue(isValid);
        
        // Extract user info from token
        Long userId = jwtTokenProvider.getUserIdFromToken(token);
        assertEquals(1L, userId);
        
        String username = jwtTokenProvider.getUsernameFromToken(token);
        assertEquals("testuser", username);
    }

    @Test
    void testInvalidToken() {
        String invalidToken = "invalid.token.here";
        boolean isValid = jwtTokenProvider.validateToken(invalidToken);
        assertFalse(isValid);
    }
}