package com.archy.texasholder.config;

import com.archy.texasholder.repo.GameRoomDBRepository;
import com.archy.texasholder.repo.RoomDBRepository;
import com.archy.texasholder.entity.GameRoomDB;
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
    private final GameRoomDBRepository gameRoomDBRepository;
    private final RoomDBRepository roomDBRepository;

    public TestRoomConfig(RoomService roomService, GameRoomDBRepository gameRoomDBRepository, RoomDBRepository roomDBRepository) {
        this.roomService = roomService;
        this.gameRoomDBRepository = gameRoomDBRepository;
        this.roomDBRepository = roomDBRepository;
    }

    @PostConstruct
    public void initializeTestRooms() {
        System.out.println("TestRoomConfig: Initializing test rooms...");
        // Create RoomDB entries for room type lookup
        RoomDB beginnerType = RoomDB.builder()
                .roomid(1)
                .showname("Beginner Room")
                .name("beginner")
                .roomtype("public")
                .minbuy(100)
                .maxbuy(1000)
                .bbet(10)
                .sbet(5)
                .build();

        RoomDB intermediateType = RoomDB.builder()
                .roomid(2)
                .showname("Intermediate Room")
                .name("intermediate")
                .roomtype("public")
                .minbuy(1000)
                .maxbuy(5000)
                .bbet(50)
                .sbet(25)
                .build();

        RoomDB advancedType = RoomDB.builder()
                .roomid(3)
                .showname("Advanced Room")
                .name("advanced")
                .roomtype("private")
                .minbuy(5000)
                .maxbuy(20000)
                .bbet(200)
                .sbet(100)
                .build();

        roomDBRepository.save(beginnerType);
        roomDBRepository.save(intermediateType);
        roomDBRepository.save(advancedType);

        // Create GameRoomDB instances linked to the room types above
        GameRoomDB beginnerRoomDB = GameRoomDB.builder()
                .gameroomid(1)
                .roomid(1)
                .name("beginner-1")
                .roomtype("public")
                .minbuy(100)
                .maxbuy(1000)
                .bbet(10)
                .sbet(5)
                .build();

        GameRoomDB intermediateRoomDB = GameRoomDB.builder()
                .gameroomid(2)
                .roomid(2)
                .name("intermediate-1")
                .roomtype("public")
                .minbuy(1000)
                .maxbuy(5000)
                .bbet(50)
                .sbet(25)
                .build();

        GameRoomDB advancedRoomDB = GameRoomDB.builder()
                .gameroomid(3)
                .roomid(3)
                .name("advanced-1")
                .roomtype("private")
                .minbuy(5000)
                .maxbuy(20000)
                .bbet(200)
                .sbet(100)
                .build();
        
        // Save game rooms to database
        gameRoomDBRepository.save(beginnerRoomDB);
        gameRoomDBRepository.save(intermediateRoomDB);
        gameRoomDBRepository.save(advancedRoomDB);
        
        // Create PukerGame instances
        PukerGame beginnerRoom = new PukerGame(beginnerRoomDB, null, new PukerHelp());
        PukerGame intermediateRoom = new PukerGame(intermediateRoomDB, null, new PukerHelp());
        PukerGame advancedRoom = new PukerGame(advancedRoomDB, null, new PukerHelp());
        
        // Add rooms to the service
        roomService.addRoom(beginnerRoom);
        roomService.addRoom(intermediateRoom);
        roomService.addRoom(advancedRoom);
        
    }
}