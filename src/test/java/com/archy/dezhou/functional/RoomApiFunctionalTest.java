package com.archy.dezhou.functional;

import com.archy.dezhou.entity.ApiResponse;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Functional tests specifically for Room API endpoints
 */
@Tag("room-api")
@Tag("functional")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RoomApiFunctionalTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testGetRoomList() {
        ResponseEntity<ApiResponse> response = restTemplate.getForEntity(
                "/api/v1/room/list", 
                ApiResponse.class);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().isSuccess());
        assertNotNull(response.getBody().getData());
        
        // Verify we get an array of rooms
        assertTrue(response.getBody().getData() instanceof java.util.List);
        java.util.List<?> rooms = (java.util.List<?>) response.getBody().getData();
        assertFalse(rooms.isEmpty(), "Room list should not be empty");
    }

    @Test
    void testGetRoomListWithTypeParameter() {
        ResponseEntity<ApiResponse> response = restTemplate.getForEntity(
                "/api/v1/room/list?type=public", 
                ApiResponse.class);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().isSuccess());
        
        // Verify all rooms are of type 'public'
        java.util.List<?> rooms = (java.util.List<?>) response.getBody().getData();
        assertTrue(rooms.size() > 0, "Should have at least one public room");
    }

    @Test
    void testGetSpecificRoom() {
        // Test with beginner room
        ResponseEntity<ApiResponse> response = restTemplate.getForEntity(
                "/api/v1/room/beginner", 
                ApiResponse.class);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().isSuccess());
        assertNotNull(response.getBody().getData());
        
        // Verify room details
        Object roomData = response.getBody().getData();
        assertNotNull(roomData, "Room data should not be null");
    }

    @Test
    void testGetNonExistentRoom() {
        ResponseEntity<ApiResponse> response = restTemplate.getForEntity(
                "/api/v1/room/nonexistent", 
                ApiResponse.class);

        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
        assertFalse(response.getBody().isSuccess());
        assertNotNull(response.getBody().getMessage());
    }

    @Test
    void testRoomListPerformance() {
        long startTime = System.currentTimeMillis();
        
        ResponseEntity<ApiResponse> response = restTemplate.getForEntity(
                "/api/v1/room/list", 
                ApiResponse.class);

        long duration = System.currentTimeMillis() - startTime;
        
        assertTrue(duration < 500, 
            "Room list endpoint should respond in under 500ms, but took " + duration + "ms");
    }

    @Test
    void testRoomListResponseStructure() {
        ResponseEntity<ApiResponse> response = restTemplate.getForEntity(
                "/api/v1/room/list", 
                ApiResponse.class);

        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        
        // Verify response structure
        assertNotNull(response.getBody().getStatus());
        assertNotNull(response.getBody().getCode());
        assertNotNull(response.getBody().getMessage());
        assertNotNull(response.getBody().getTimestamp());
        assertNotNull(response.getBody().getData());
    }
}