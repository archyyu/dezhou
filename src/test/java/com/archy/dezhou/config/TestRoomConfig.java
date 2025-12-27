package com.archy.dezhou.config;

import com.archy.dezhou.entity.RoomDB;
import com.archy.dezhou.entity.room.PukerGame;
import com.archy.dezhou.service.RoomService;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Test configuration to initialize sample rooms for functional tests
 */
@Configuration
@Profile("test")
public class TestRoomConfig {

    private final RoomService roomService;

    public TestRoomConfig(RoomService roomService) {
        this.roomService = roomService;
    }

    @PostConstruct
    public void initializeTestRooms() {
        System.out.println("TestRoomConfig: Initializing test rooms...");
        // Create some test rooms for functional tests using RoomDB
        RoomDB beginnerRoomDB = new RoomDB();
        beginnerRoomDB.setId(1);
        beginnerRoomDB.setName("beginner");
        beginnerRoomDB.setRoomtype("public"); // Public room
        beginnerRoomDB.setMinbuy(100);
        beginnerRoomDB.setMaxbuy(1000);
        beginnerRoomDB.setBbet(10);  // Big blind
        beginnerRoomDB.setSbet(5);   // Small blind
        
        RoomDB intermediateRoomDB = new RoomDB();
        intermediateRoomDB.setId(2);
        intermediateRoomDB.setName("intermediate");
        intermediateRoomDB.setRoomtype("public"); // Public room
        intermediateRoomDB.setMinbuy(1000);
        intermediateRoomDB.setMaxbuy(5000);
        intermediateRoomDB.setBbet(50);  // Big blind
        intermediateRoomDB.setSbet(25);  // Small blind
        
        RoomDB advancedRoomDB = new RoomDB();
        advancedRoomDB.setId(3);
        advancedRoomDB.setName("advanced");
        advancedRoomDB.setRoomtype("private"); // Private room
        advancedRoomDB.setMinbuy(5000);
        advancedRoomDB.setMaxbuy(20000);
        advancedRoomDB.setBbet(200);  // Big blind
        advancedRoomDB.setSbet(100);  // Small blind
        
        // Create PukerGame instances
        PukerGame beginnerRoom = new PukerGame(beginnerRoomDB);
        PukerGame intermediateRoom = new PukerGame(intermediateRoomDB);
        PukerGame advancedRoom = new PukerGame(advancedRoomDB);
        
        // Add rooms to the service
        roomService.addRoom(beginnerRoom);
        roomService.addRoom(intermediateRoom);
        roomService.addRoom(advancedRoom);
        
        System.out.println("TestRoomConfig: Added " + roomService.getRoomList().size() + " test rooms");
    }
}