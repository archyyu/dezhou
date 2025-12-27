package com.archy.dezhou.game;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.archy.dezhou.GameCmdException;
import com.archy.dezhou.entity.Player;
import com.archy.dezhou.entity.RoomDB;
import com.archy.dezhou.entity.User;
import com.archy.dezhou.entity.room.PukerGame;
import com.archy.dezhou.global.ConstList;

public class PukerGameTest {
    
    private List<Player> testPlayers;
    private PukerGame testRoom;

    @BeforeEach
    public void setUp() {
        // Initialize test data
        testPlayers = new ArrayList<>();
        
        // Create 4 test users for Texas Hold'em
        setUpTestUsers();
        
        // Create a test room
        setUpTestRoom();
        
    }

    private void setUpTestUsers() {
        // Create 4 test players with different characteristics
        String[] names = {"Alice", "Bob", "Charlie", "Diana"};
        int[] chips = {1000, 1000, 1000, 1000};
        
        for (int i = 0; i < 4; i++) {
            User user = new User();
            user.setUid(i + 1);
            user.setAccount(names[i]);
            user.setAllmoney(chips[i]);
            // user.setRoommoney(chips[i]);
            
            Player player = new Player(user);
            player.setChips(chips[i]);
            player.setSeatId(i + 1);
            player.setActive(true);
            player.setReady(true);
            
            testPlayers.add(player);
            
        }
    }

    private void setUpTestRoom() {
        // Create a Texas Hold'em room
        RoomDB roomDB = new RoomDB();
        roomDB.setId(1);
        roomDB.setBbet(10);
        roomDB.setName("beginner");
        roomDB.setMinbuy(1000);
        roomDB.setMaxbuy(2000);
        roomDB.setRoomtype("public");
        roomDB.setShowname("beginner");

        testRoom = new PukerGame(roomDB);
        testRoom.setName("TexasHoldemTestRoom");
        testRoom.setRoomtype("rg"); // Regular game
        testRoom.setBbet(10); // Big blind
        testRoom.setSbet(5); // Small blind
        testRoom.setMaxPlayers(9);
        testRoom.setMinPlayers(2);
        testRoom.setGameType(ConstList.GAME_TYPE_TEXAS_HOLDEM);
    }

    @Test
    public void testPlayerSitdown() throws GameCmdException {
        int playerIndex = 0;
        int seatId = 2;
        int cd = 1000;
        Player player = testPlayers.get(playerIndex);
        testRoom.userJoin(player);
        testRoom.playerSitDown(seatId, player, cd);

        assertEquals(seatId, player.getSeatId());
        assertEquals(cd, player.getRoommoney());
        assertEquals(player.getRoommoney() + player.getAllmoney(), 1000);
        
    }


    @Test
    public void testPukerGame() {

        int cd = 100;
        // Add players to the room
        for (Player player : testPlayers) {
            testRoom.userJoin(player);
            try {
                testRoom.playerSitDown(player.getSeatId(), player, cd);
            } catch (GameCmdException e) {
                System.err.print("sitDown err:" + e.getMessage());
            }


            player.setRoomId(testRoom.getRoomid());
        }

        testRoom.initGame();
        testRoom.gameStartHandle();

        assertEquals(1, testRoom.getRoundNum());
        assertEquals(1, testRoom.getRound());
        followBet();
        followBet();
        followBet();
        followBet();

        assertEquals(2, testRoom.getRound());
        followBet();
        followBet();
        followBet();
        followBet();

        assertEquals(3, testRoom.getRound());
        followBet();
        followBet();
        followBet();
        followBet();

        assertEquals(4, testRoom.getRound());
        followBet();
        followBet();
        followBet();
        followBet();

        testRoom.gameStartHandle();
        assertEquals(2, testRoom.getRoundNum());
    }

    private void followBet() {
        Player currentPlayer = this.testRoom.getCurrentPlayer();
        testRoom.playerFollow(currentPlayer);
    }

}
