package com.archy.dezhou.game;

import com.archy.dezhou.entity.Player;
import com.archy.dezhou.entity.User;
import com.archy.dezhou.entity.room.GameRoom;
import com.archy.dezhou.entity.room.PukerGame;
import com.archy.dezhou.global.ConstList;
import com.archy.dezhou.service.RoomService;
import com.archy.dezhou.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;


/**
 * Texas Hold'em Game Test - Sets up test users, creates a room, and starts a game
 */
@ExtendWith(MockitoExtension.class)
class TexasHoldemGameTest {

    @Mock
    private UserService userService;

    @Mock
    private RoomService roomService;

    private List<Player> testPlayers;
    private GameRoom testRoom;
    private PukerGame texasHoldemGame;

    @BeforeEach
    void setUp() {
        // Initialize test data
        testPlayers = new ArrayList<>();
        
        // Create 4 test users for Texas Hold'em
        createTestUsers();
        
        // Create a test room
        createTestRoom();
        
        // Initialize the game
        initializeGame();

        // start the game
        startGame();

    }

    private void createTestUsers() {
        // Create 4 test players with different characteristics
        String[] names = {"Alice", "Bob", "Charlie", "Diana"};
        int[] chips = {1000, 1500, 1200, 1800};
        
        for (int i = 0; i < 4; i++) {
            User user = new User();
            user.setUid(i + 1);
            user.setAccount(names[i]);
            user.setAllmoney(chips[i]);
            user.setRoommoney(chips[i]);
            
            Player player = new Player(user);
            player.setChips(chips[i]);
            player.setSeatId(i + 1);
            player.setActive(true);
            player.setReady(true);
            
            testPlayers.add(player);
            
            // Mock the user service to return our test players
            // when(userService.getUserByUserId(i + 1)).thenReturn(player);
        }
    }

    private void createTestRoom() {
        // Create a Texas Hold'em room
        testRoom = new GameRoom();
        testRoom.setName("TexasHoldemTestRoom");
        testRoom.setRoomtype("rg"); // Regular game
        testRoom.setBbet(10); // Big blind
        testRoom.setSbet(5); // Small blind
        testRoom.setMaxPlayers(9);
        testRoom.setMinPlayers(2);
        testRoom.setGameType(ConstList.GAME_TYPE_TEXAS_HOLDEM);
        
        // Add players to the room
        for (Player player : testPlayers) {
            testRoom.userJoin(player);
            testRoom.playerSitDown(player.getSeatId(), player, player.getRoommoney());
            player.setRoomId(testRoom.getRoomid());
        }
        
    }

    private void initializeGame() {
        // Create a new Texas Hold'em game
        texasHoldemGame = new PukerGame(testRoom);
        testRoom.setPokerGame(texasHoldemGame);
        
        // Initialize the game with players
        texasHoldemGame.initGame();
    
    }

    private void startGame() {
        texasHoldemGame.gameStartHandle();
    }

    @Test
    private void hearBeat() {

        for (int i=0;i<5;i++){

            texasHoldemGame.beatHeart(System.currentTimeMillis() / 1000);
            
            try {
                Thread.sleep(1000);
            } catch (Exception e) {

            }

        }
    
    }

    @Test
    private void followbet() {
        for (int i=0;i<5;i++) {

            

        }
    }

}