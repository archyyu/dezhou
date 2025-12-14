package com.archy.dezhou.functional;

import com.archy.dezhou.entity.ApiResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RoomListFunctionalTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testRoomListEndpoint() {
        ResponseEntity<ApiResponse> response = restTemplate.getForEntity(
                "/api/v1/room/list", 
                ApiResponse.class);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().isSuccess());
        assertNotNull(response.getBody().getData());
        assertTrue(response.getBody().getData() instanceof java.util.List);
        
        java.util.List<?> rooms = (java.util.List<?>) response.getBody().getData();
        assertFalse(rooms.isEmpty());
    }

    @Test
    void testRoomListEndpointPerformance() {
        long startTime = System.currentTimeMillis();
        
        ResponseEntity<ApiResponse> response = restTemplate.getForEntity(
                "/api/v1/room/list", 
                ApiResponse.class);

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().isSuccess());
        
        // Response should be fast - under 500ms
        assertTrue(duration < 500, "Response time should be under 500ms, but was " + duration + "ms");
    }

    @Test
    void testRoomListEndpointWithParameters() {
        ResponseEntity<ApiResponse> response = restTemplate.getForEntity(
                "/api/v1/room/list?type=public", 
                ApiResponse.class);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().isSuccess());
        assertNotNull(response.getBody().getData());
    }

    @Test
    void testSpecificRoomEndpoint() {
        ResponseEntity<ApiResponse> response = restTemplate.getForEntity(
                "/api/v1/room/beginner", 
                ApiResponse.class);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().isSuccess());
        assertNotNull(response.getBody().getData());
    }

    @Test
    void testInvalidRoomEndpoint() {
        ResponseEntity<ApiResponse> response = restTemplate.getForEntity(
                "/api/v1/room/nonexistent", 
                ApiResponse.class);

        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
        assertFalse(response.getBody().isSuccess());
        assertNotNull(response.getBody().getMessage());
    }
}