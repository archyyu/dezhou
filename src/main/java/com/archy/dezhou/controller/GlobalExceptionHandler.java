package com.archy.dezhou.controller;

import com.archy.dezhou.entity.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Global Exception Handler for the Dezhou Poker Server
 * 
 * This class handles exceptions across all controllers, providing consistent error responses
 * and proper HTTP status codes. It follows REST API best practices for error handling.
 */
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handle general exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleException(Exception ex) {
        logger.error("Unexpected error occurred:", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("InternalServerError: " + getSafeErrorMessage(ex)));
    }

    /**
     * Handle illegal argument exceptions (bad request parameters)
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<?>> handleIllegalArgumentException(IllegalArgumentException ex) {
        logger.warn("Illegal argument exception:", ex);
        return ResponseEntity.badRequest()
                .body(ApiResponse.error("InvalidRequest: " + ex.getMessage()));
    }

    /**
     * Handle resource not found exceptions
     */
    @ExceptionHandler(com.archy.dezhou.exception.ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleResourceNotFoundException(com.archy.dezhou.exception.ResourceNotFoundException ex) {
        logger.warn("Resource not found:", ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("ResourceNotFound: " + ex.getMessage()));
    }

    /**
     * Handle authentication exceptions
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<?>> handleAuthenticationException(AuthenticationException ex) {
        logger.warn("Authentication failed:", ex);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("AuthenticationFailed: " + ex.getMessage()));
    }

    /**
     * Handle authentication credentials not found
     */
    @ExceptionHandler(AuthenticationCredentialsNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleAuthenticationCredentialsNotFoundException(AuthenticationCredentialsNotFoundException ex) {
        logger.warn("Authentication credentials not found:", ex);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("AuthenticationRequired: " + ex.getMessage()));
    }

    /**
     * Handle security access denied exceptions
     */
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<ApiResponse<?>> handleAccessDeniedException(org.springframework.security.access.AccessDeniedException ex) {
        logger.warn("Access denied:", ex);
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error("AccessDenied: " + ex.getMessage()));
    }

    /**
     * Handle unsupported operation exceptions
     */
    @ExceptionHandler(UnsupportedOperationException.class)
    public ResponseEntity<ApiResponse<?>> handleUnsupportedOperationException(UnsupportedOperationException ex) {
        logger.warn("Unsupported operation:", ex);
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ApiResponse.error("UnsupportedOperation: " + ex.getMessage()));
    }

    /**
     * Handle null pointer exceptions (should be rare with proper null checks)
     */
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ApiResponse<?>> handleNullPointerException(NullPointerException ex) {
        logger.error("Null pointer exception:", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("InternalServerError: Unexpected null value"));
    }

    /**
     * Handle illegal state exceptions
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<?>> handleIllegalStateException(IllegalStateException ex) {
        logger.error("Illegal state exception:", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("InvalidState: " + ex.getMessage()));
    }

    /**
     * Extract safe error message from exception
     */
    private String getSafeErrorMessage(Exception ex) {
        String message = ex.getMessage();
        return (message != null && !message.isEmpty()) ? message : "An unexpected error occurred";
    }

    /**
     * Custom exception for resource not found scenarios
     */
    public static class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String message) {
            super(message);
        }
    }
}