package com.archy.texasholder.controller.api;

import com.archy.texasholder.entity.ApiResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class GameApiControllerTest {

    @Test
    void testApiResponseCreation() {
        // Test that ApiResponse can be created successfully
        ApiResponse<String> successResponse = ApiResponse.success("test data");
        assertNotNull(successResponse);
        assertTrue(successResponse.isSuccess());
        assertEquals("test data", successResponse.getData());

        ApiResponse<?> errorResponse = ApiResponse.error("test error");
        assertNotNull(errorResponse);
        assertFalse(errorResponse.isSuccess());
        assertEquals("test error", errorResponse.getMessage());
    }

    @Test
    void testGameApiControllerInstantiation() {
        // Test that GameApiController can be instantiated
        GameApiController controller = new GameApiController();
        assertNotNull(controller);
    }

    @Test
    void testBaseApiControllerMethods() {
        // Test the base controller methods
        GameApiController controller = new GameApiController();
        
        // Test success response
        ResponseEntity<ApiResponse<?>> successResponse = controller.successResponse("test data");
        assertNotNull(successResponse);
        assertTrue(successResponse.getBody().isSuccess());
        assertEquals("test data", successResponse.getBody().getData());

        // Test error response
        ResponseEntity<ApiResponse<?>> errorResponse = controller.errorResponse("test error");
        assertNotNull(errorResponse);
        assertFalse(errorResponse.getBody().isSuccess());
        assertEquals("test error", errorResponse.getBody().getMessage());
    }

    @Test
    void testProtectedMethodsExist() {
        // Test that protected methods exist (they will be null since services are not injected)
        GameApiController controller = new GameApiController();
        
        // These methods should exist (even if they return null)
        assertDoesNotThrow(() -> controller.getRoomService());
        assertDoesNotThrow(() -> controller.getUserService());
    }

    @Test
    void testErrorResponseWithCustomStatus() {
        // Test error response with custom status code
        GameApiController controller = new GameApiController();
        
        ResponseEntity<ApiResponse<Object>> errorResponse = controller.errorResponse("test error", 404);
        assertNotNull(errorResponse);
        assertFalse(errorResponse.getBody().isSuccess());
        assertEquals("test error", errorResponse.getBody().getMessage());
        assertEquals(404, errorResponse.getStatusCodeValue());
    }

    @Test
    void testCustomResponse() {
        // Test custom response creation
        GameApiController controller = new GameApiController();
        
        ResponseEntity<ApiResponse<String>> customResponse = controller.customResponse(
                true, "custom", "201", "Custom message", "custom data");
        assertNotNull(customResponse);
        assertTrue(customResponse.getBody().isSuccess());
        assertEquals("custom", customResponse.getBody().getStatus());
        assertEquals("201", customResponse.getBody().getCode());
        assertEquals("Custom message", customResponse.getBody().getMessage());
        assertEquals("custom data", customResponse.getBody().getData());
    }

    @Test
    void testValidateRequiredParams() {
        // Test parameter validation
        GameApiController controller = new GameApiController();
        
        // Test with valid params
        assertTrue(controller.validateRequiredParams("param1", "param2", "param3"));

        // Test with null param
        assertFalse(controller.validateRequiredParams("param1", null, "param3"));

        // Test with empty param
        assertFalse(controller.validateRequiredParams("param1", "", "param3"));

        // Test with whitespace param
        assertFalse(controller.validateRequiredParams("param1", "   ", "param3"));
    }
}