package com.archy.dezhou.security;

import com.archy.dezhou.config.JwtConfig;
import com.archy.dezhou.entity.Player;
import com.archy.dezhou.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtTokenProviderTest {

    @Mock
    private JwtConfig jwtConfig;

    @InjectMocks
    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        when(jwtConfig.getSecret()).thenReturn("test-secret-key-1234567890");
        when(jwtConfig.getExpiration()).thenReturn(3600000L); // 1 hour
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