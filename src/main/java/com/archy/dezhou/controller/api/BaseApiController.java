package com.archy.dezhou.controller.api;

import com.archy.dezhou.entity.ApiResponse;
import org.springframework.http.ResponseEntity;

/**
 * Base API Controller for handling common functionality
 * Replaces the legacy Backlet pattern with modern Spring Boot REST controllers
 */
public abstract class BaseApiController {

    /**
     * Create a successful API response
     */
    @SuppressWarnings("unchecked")
    protected ResponseEntity<ApiResponse<?>> successResponse(Object data) {
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @SuppressWarnings("unchecked")
    protected ResponseEntity<ApiResponse<?>> successResponse(String msg) {
        return ResponseEntity.ok(ApiResponse.success(msg));
    }

    /**
     * Create an error API response
     */
    @SuppressWarnings("unchecked")
    protected ResponseEntity<ApiResponse<?>> errorResponse(String errorMessage) {
        ApiResponse<?> errorResponse = ApiResponse.error(errorMessage);
        return ResponseEntity.badRequest().body(errorResponse);
    }

    /**
     * Create an error API response with custom status code
     */
    @SuppressWarnings("unchecked")
    protected <T> ResponseEntity<ApiResponse<T>> errorResponse(String errorMessage, int statusCode) {
        ApiResponse<?> errorResponse = ApiResponse.error(errorMessage);
        return ResponseEntity.status(statusCode).body((ApiResponse<T>) errorResponse);
    }

    /**
     * Create a custom API response
     */
    @SuppressWarnings("unchecked")
    protected <T> ResponseEntity<ApiResponse<T>> customResponse(boolean success, String status, String code, String message, T data) {
        return ResponseEntity.ok(new ApiResponse<>(success, status, code, message, data));
    }

    /**
     * Create a custom API response with HTTP status code
     */
    @SuppressWarnings("unchecked")
    protected <T> ResponseEntity<ApiResponse<T>> customResponse(boolean success, String status, String code, String message, T data, int httpStatus) {
        return ResponseEntity.status(httpStatus).body(new ApiResponse<>(success, status, code, message, data));
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

    
}