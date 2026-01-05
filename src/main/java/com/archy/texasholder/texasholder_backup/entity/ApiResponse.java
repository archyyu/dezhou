package com.archy.texasholder.entity;

/**
 * Standard API Response format for the Dezhou Poker Server
 * Replaces the legacy XML-based response format with JSON
 */
public class ApiResponse<T> {
    private boolean success;
    private String errorMessage;
    private T data;
    private long timestamp;
    private String status;
    private String code;
    private String message;

    /**
     * Default constructor
     */
    public ApiResponse() {
        this.timestamp = System.currentTimeMillis();
        this.success = true;
    }

    /**
     * Constructor for successful responses
     */
    public ApiResponse(T data) {
        this.success = true;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * Constructor for error responses
     */
    public ApiResponse(String errorMessage) {
        this.success = false;
        this.errorMessage = errorMessage;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * Constructor for detailed responses
     */
    public ApiResponse(boolean success, String status, String code, String message, T data) {
        this.success = success;
        this.status = status;
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    // Static factory methods for common response types

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "success", "200", "Success", data);
    }

    public static ApiResponse<?> error(String errorMessage) {
        return new ApiResponse<>(false, "error", "400", errorMessage, null);
    }

    public static ApiResponse<?> error(String status, String code, String message) {
        return new ApiResponse<>(false, status, code, message, null);
    }

    // Getters and Setters

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "ApiResponse{" +
                "success=" + success +
                ", errorMessage='" + errorMessage + '\'' +
                ", data=" + data +
                ", timestamp=" + timestamp +
                ", status='" + status + '\'' +
                ", code='" + code + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}