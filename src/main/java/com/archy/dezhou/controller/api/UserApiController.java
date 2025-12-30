package com.archy.dezhou.controller.api;


import com.archy.dezhou.entity.ApiResponse;
import com.archy.dezhou.entity.Player;
import com.archy.dezhou.entity.User;
import com.archy.dezhou.entity.response.UserResponse;
import com.archy.dezhou.security.JwtTokenProvider;
import com.archy.dezhou.service.PlayerService;
import com.archy.dezhou.service.RoomService;
import com.archy.dezhou.service.UserService;

import jakarta.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * User API Controller - Replaces PlayerManageBacklet
 * Handles user authentication, registration, and profile management
 */
@RestController
@RequestMapping("/api/v1/user")
public class UserApiController extends BaseApiController {

    @Resource
    private PlayerService playerService;

    @Resource
    private RoomService roomService;

    @Resource
    private UserService userService;

    @Resource
    private JwtTokenProvider jwtTokenProvider;

    private Logger logger = LoggerFactory.getLogger(getClass());

    // User login endpoint - replaces USERLOGIN command
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> userLogin(
            @RequestParam String name,
            @RequestParam String password) {
        
        logger.info("Login attempt for user: {}", name); // Debug logging
        
        if (!validateRequiredParams(name, password)) {
            logger.warn("Login failed: name or password is null");
            return errorResponse("NameOrPasswordIdNull");
        }

        try {
            logger.debug("Validating user credentials for: {}", name);
            
            
            // Get the user from the response
            User user = userService.getUserByAccount(name);
            
            if (user == null) {

                // auto register
                // '13800000001', 1, 'male', 'Address 1', '2025-01-01 10:00:00', '1990-01-01', '2025-01-01 10:00:00'),
                user = new User();
                user.setAccount(name);
                user.setGendar("male");
                user.setMobile("13800000050");
                user.setAllmoney(10000);

                userService.registerUser(user);

                user = userService.getUserByAccount(name);
            }

            Player player = new Player(user);
            
            // Generate JWT token
            String token = jwtTokenProvider.generateToken(player);
            
            // Create response with token and user info
            Map<String, Object> loginResponse = new HashMap<>();
            loginResponse.put("token", token);
            loginResponse.put("user", player);
            
            return successResponse(loginResponse);
        } catch (Exception e) {
            return errorResponse("LoginFailed: " + e.getMessage());
        }
    }
   

    // Password update endpoint - replaces PASSWORDUPDATE command
    @PutMapping("/password")
    public ResponseEntity<ApiResponse<?>> updatePassword(
            @RequestParam String password,
            @RequestParam String email) {
        
        if (!validateRequiredParams(password, email)) {
            return errorResponse("parmsInInvalid");
        }

        try {
            String[] userID = {"uid", "name"};
            if (!PlayerService.isvalidEmail(email, userID)) {
                return errorResponse("EmailIsinValid");
            }
            
            // TODO: Implement actual password update logic
            return successResponse("Dealing");
        } catch (Exception e) {
            return errorResponse("PasswordUpdateFailed: " + e.getMessage());
        }
    }

    // User info endpoint - replaces UINFO command
    @GetMapping("/info")
    public ResponseEntity<ApiResponse<?>> getUserInfo(
            @RequestParam String uid) {

        if (!validateRequiredParams(uid)) {
            return errorResponse("ParmsIsInvalid");
        }

        try {
            // Get user from JWT authentication or fallback to legacy
            Player uinfo = getAuthenticatedUser(uid);

            if (uinfo == null) {
                return errorResponse("YouAreNotLogined!");
            }

            // Use the new UserResponse entity instead of JsonObjectWrapper
            boolean includeSensitiveData = true; // Adjust based on your logic
            UserResponse response = new UserResponse(includeSensitiveData ? uinfo : null, includeSensitiveData);
            
            return successResponse(response);
        } catch (Exception e) {
            return errorResponse("UserInfoRetrievalFailed: " + e.getMessage());
        }
    }

    // User logout endpoint - replaces LOGOUT command
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<?>> userLogout(@RequestParam String uid) {
        Player user = getAuthenticatedUser(uid);
        if (user != null) {
            return successResponse("loginoutOk");
        } else {
            return successResponse("HasLoginOutBefore");
        }
    }

    // User achievements endpoint - replaces RUSHACH command
    @GetMapping("/achievements")
    public ResponseEntity<ApiResponse<?>> getUserAchievements(
            @RequestParam String uid,
            @RequestParam(required = false) String rn) {
        
        // TODO: Implement achievements logic
        return successResponse("Achievements endpoint");
    }
    
    // Helper method to get authenticated user
    private Player getAuthenticatedUser(String uid) {
        try {
            // First try to get user from security context (JWT authentication)
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof Player) {
                Player authenticatedUser = (Player) authentication.getPrincipal();
                
                // Verify that the uid parameter matches the authenticated user
                if (uid != null && !uid.isEmpty()) {
                    int uidInt = Integer.parseInt(uid);
                    if (authenticatedUser.getUid() == uidInt) {
                        return authenticatedUser;
                    }
                }
                return authenticatedUser;
            }
            
            // Fallback to legacy authentication (for compatibility)
            if (uid != null && !uid.isEmpty()) {
                int uidInt = Integer.parseInt(uid);
                return this.userService.getUserByUserId(uidInt);
            }
            
            return null;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}