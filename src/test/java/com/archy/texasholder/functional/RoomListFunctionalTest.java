package com.archy.texasholder.functional;

import com.archy.texasholder.entity.ApiResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class RoomListFunctionalTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testRoomListEndpoint() {
        ResponseEntity<ApiResponse> response = restTemplate.getForEntity(
                "/api/v1/room/roomTypeList", 
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
    void testRoomListEndpointWithParameters() {
        ResponseEntity<ApiResponse> response = restTemplate.getForEntity(
                "/api/v1/room/1/list", 
                ApiResponse.class);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().isSuccess());
        assertNotNull(response.getBody().getData());
    }

    
}