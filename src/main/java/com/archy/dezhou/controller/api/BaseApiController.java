package com.archy.dezhou.controller.api;

import com.archy.dezhou.entity.ApiResponse;
import com.archy.dezhou.entity.Player;
import com.archy.dezhou.entity.User;
import com.archy.dezhou.global.UserModule;
import org.springframework.http.ResponseEntity;

/**
 * Base API Controller for handling common functionality
 * Replaces the legacy Backlet pattern with modern Spring Boot REST controllers
 */
public abstract class BaseApiController {

    /**
     * Create a successful API response
     */
    protected <T> ResponseEntity<ApiResponse<T>> successResponse(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setData(data);
        response.setTimestamp(System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }

    /**
     * Create an error API response
     */
    protected ResponseEntity<ApiResponse<?>> errorResponse(String errorMessage) {
        return ResponseEntity.badRequest().body(ApiResponse.error(errorMessage));
    }

    /**
     * Create an error API response with custom status code
     */
    protected ResponseEntity<ApiResponse<?>> errorResponse(String errorMessage, int statusCode) {
        return ResponseEntity.status(statusCode).body(ApiResponse.error(errorMessage));
    }

    /**
     * Create a custom API response
     */
    protected ResponseEntity<ApiResponse<?>> customResponse(boolean success, String status, String code, String message, Object data) {
        return ResponseEntity.ok(new ApiResponse<>(success, status, code, message, data));
    }

    /**
     * Get current user by ID
     */
    protected Player getCurrentUser(String uid) {
        try {
            int userId = Integer.parseInt(uid);
            return UserModule.getInstance().getUserByUserId(userId);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Validate required parameters
     */
    protected boolean validateRequiredParams(String... params) {
        for (String param : params) {
            if (param == null || param.trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Get user by ID (for backward compatibility)
     */
    protected User getUserById(int uid) {
        return UserModule.getInstance().getUserById(uid);
    }
}