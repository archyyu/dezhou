package com.archy.texasholder.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;

import com.archy.texasholder.GameCmdException;
import com.archy.texasholder.entity.Player;
import com.archy.texasholder.entity.RoomDB;
import com.archy.texasholder.entity.User;
import com.archy.texasholder.entity.room.GameRoom;

public class GameRoomTest {


    private RoomDB roomDB = null;

    private GameRoom gameRoom = null;

    private Player player = null;

    @BeforeEach
    public void setUp() {

        User user = new User();
        user.setUid(1);
        user.setAccount("user1");
        user.setAllmoney(1000);
        user.setRoommoney(100);
        
        player = new Player(user);
        player.setChips(100);
        player.setActive(true);
        player.setReady(true);

        roomDB = new RoomDB();
        roomDB.setId(1);
        roomDB.setBbet(10);
        roomDB.setName("beginner");
        roomDB.setMinbuy(1000);
        roomDB.setMaxbuy(2000);
        roomDB.setRoomtype("public");
        roomDB.setShowname("beginner");

        this.gameRoom = new GameRoom(roomDB);
    }

    @Test
    public void testPlayerJoin() {

        assertEquals(0, gameRoom.userJoin(player));
        assertEquals(-2, gameRoom.userJoin(player));
        assertTrue(gameRoom.getSpectatorList().contains(player));
    
    }

    @Test
    public void testPlayerSitdown() throws GameCmdException {
        int seatId = 7;
        gameRoom.userJoin(player);
        gameRoom.playerSitDown(seatId, player, 10);
        
        assertEquals(player, gameRoom.getPlayerBySeat(seatId));
        assertFalse(gameRoom.getSpectatorList().contains(player));
    }


    @Test
    public void testPlayerStandup() throws GameCmdException {

        int seatId = 7;
        assertFalse(gameRoom.playerStandUp(player));
        
        gameRoom.userJoin(player);
        assertEquals(gameRoom.getRoomid(), player.getRoomid());

        gameRoom.playerSitDown(seatId, player, 10);

        assertTrue(gameRoom.playerStandUp(player));
        assertTrue(gameRoom.getSpectatorList().contains(player));
    }

}
