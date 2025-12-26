package com.archy.dezhou;

import java.util.ArrayList;
import java.util.List;

import com.archy.dezhou.entity.Player;
import com.archy.dezhou.entity.RoomDB;
import com.archy.dezhou.entity.User;
import com.archy.dezhou.entity.room.PukerGame;
import com.archy.dezhou.global.ConstList;

// this class is to test the workflow of the texas holdem game
public class TestGame {

    public static void main(String[] args) {
        
        TestGame testGame = new TestGame();

        testGame.setUp();
        // testGame.detectNextPlayer();
        normalFollowBet(testGame);
        // normalFollowBet(testGame);
        // normalFollowBet(testGame);

    }

    private static void normalFollowBet(TestGame testGame) {

        testGame.startGame();
        testGame.followBet();
        testGame.followBet();
        testGame.followBet();
        testGame.followBet();

        testGame.followBet();
        testGame.followBet();
        testGame.followBet();
        testGame.followBet();

        testGame.followBet();
        testGame.followBet();
        testGame.followBet();
        testGame.followBet();

        testGame.followBet();
        testGame.followBet();
        testGame.followBet();
        testGame.followBet();

    }



    private List<Player> testPlayers;
    private PukerGame testRoom;


    void setUp() {
        // Initialize test data
        testPlayers = new ArrayList<>();
        
        // Create 4 test users for Texas Hold'em
        createTestUsers();
        
        // Create a test room
        createTestRoom();
        
        // Initialize the game
        initializeGame();

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
        
        // Add players to the room
        for (Player player : testPlayers) {
            testRoom.userJoin(player);
            try {
                testRoom.playerSitDown(player.getSeatId(), player, player.getRoommoney());
            } catch (GameCmdException e) {
                System.err.print("sitDown err:" + e.getMessage());
            }


            player.setRoomId(testRoom.getRoomid());
        }
        
    }



    private void initializeGame() {
        // Create a new Texas Hold'em game
        // testRoom.setPokerGame(texasHoldemGame);
        
        // Initialize the game with players
        testRoom.initGame();
    
    }

    private void startGame() {
        testRoom.gameStartHandle();
    }

    private void hearBeat() {



        for (int i=0;i<3;i++){

            testRoom.beatHeart(System.currentTimeMillis() / 1000);
            
            try {
                Thread.sleep(1000);
            } catch (Exception e) {

            }

        }
    
    }

    private void detectNextPlayer() {
        Player currentPlayer = this.testRoom.getCurrentPlayer();
        System.err.println("current player seat id:" + currentPlayer.getSeatId());
    }

    private void followBet() {
        Player currentPlayer = this.testRoom.getCurrentPlayer();

        testRoom.playerFollow(currentPlayer);
    }



    private void followBet(int bet) {
        Player currentPlayer = this.testRoom.getCurrentPlayer();

        testRoom.playerAddBet(currentPlayer, bet);
    }

    // private void followbet() {

    //     int smallbet = 5;

    //     for (int i=0 ; i<10 ; i++) {
    //         Player currentPlayer = this.texasHoldemGame.getCurrentPlayer();
    //         if (currentPlayer == null) {
                
    //         }
    //         int bet = 10;
    //         texasHoldemGame.playerFollowBet(currentPlayer, bet);
    //     }

    // }

}
