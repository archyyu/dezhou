package com.archy.texasholder.controller.api;

import com.archy.texasholder.entity.ApiResponse;
import com.archy.texasholder.entity.Player;
import com.archy.texasholder.security.JwtTokenProvider;
import com.archy.texasholder.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Test API Controller for testing JWT authentication
 */
@RestController
@RequestMapping("/api/v1/test")
public class TestApiController extends BaseApiController {

    @Resource
    private JwtTokenProvider jwtTokenProvider;

    @Resource
    private UserService userService;

    /**
     * Public endpoint to test JWT token generation
     */
    @GetMapping("/public")
    public ResponseEntity<ApiResponse<?>> publicEndpoint() {
        return successResponse("This is a public endpoint");
    }

    /**
     * Protected endpoint to test JWT authentication
     */
    @GetMapping("/protected")
    public ResponseEntity<ApiResponse<?>> protectedEndpoint() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.getPrincipal() instanceof Player) {
            Player player = (Player) authentication.getPrincipal();
            return successResponse("Authenticated as user: " + player.getAccount());
        }
        
        return successResponse("Authenticated but no user details available");
    }

    /**
     * Endpoint to generate test JWT token
     */
    @GetMapping("/generate-token")
    public ResponseEntity<ApiResponse<?>> generateTestToken() {
        // Get a test user (user with ID 1 if exists)
        Player testUser = userService.getUserByUserId(1);
        
        if (testUser != null) {
            String token = jwtTokenProvider.generateToken(testUser);
            return successResponse("Test token: " + token);
        }
        
        return errorResponse("No test user found");
    }
}