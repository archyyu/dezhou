package com.archy.texasholder.config;

import com.archy.texasholder.repo.RoomDBRepository;
import com.archy.texasholder.entity.RoomDB;
import com.archy.texasholder.entity.puker.PukerHelp;
import com.archy.texasholder.entity.room.PukerGame;
import com.archy.texasholder.service.RoomService;
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
    private final RoomDBRepository roomDBRepository;

    public TestRoomConfig(RoomService roomService, RoomDBRepository roomDBRepository) {
        this.roomService = roomService;
        this.roomDBRepository = roomDBRepository;
    }

    @PostConstruct
    public void initializeTestRooms() {
        System.out.println("TestRoomConfig: Initializing test rooms...");
        // Create some test rooms for functional tests using RoomDB
        RoomDB beginnerRoomDB = RoomDB.builder()
                .id(1)
                .name("beginner")
                .roomtype("public")
                .minbuy(100)
                .maxbuy(1000)
                .bbet(10)
                .sbet(5)
                .build();

        RoomDB intermediateRoomDB = RoomDB.builder()
                .id(2)
                .name("intermediate")
                .roomtype("public")
                .minbuy(1000)
                .maxbuy(5000)
                .bbet(50)
                .sbet(25)
                .build();

        RoomDB advancedRoomDB = RoomDB.builder()
                .id(3)
                .name("advanced")
                .roomtype("private")
                .minbuy(5000)
                .maxbuy(20000)
                .bbet(200)
                .sbet(100)
                .build();
        
        // Save rooms to database (the API queries the DB via JPA)
        roomDBRepository.save(beginnerRoomDB);
        roomDBRepository.save(intermediateRoomDB);
        roomDBRepository.save(advancedRoomDB);
        
        // Create PukerGame instances
        PukerGame beginnerRoom = new PukerGame(beginnerRoomDB, null,  new PukerHelp());
        PukerGame intermediateRoom = new PukerGame(intermediateRoomDB, null, new PukerHelp());
        PukerGame advancedRoom = new PukerGame(advancedRoomDB, null, new PukerHelp());
        
        // Add rooms to the service
        roomService.addRoom(beginnerRoom);
        roomService.addRoom(intermediateRoom);
        roomService.addRoom(advancedRoom);
        
    }
}