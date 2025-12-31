package com.archy.dezhou.functional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import com.archy.dezhou.entity.ApiResponse;
import com.archy.dezhou.entity.Player;
import com.archy.dezhou.entity.RoomDB;
import com.archy.dezhou.entity.User;
import com.archy.dezhou.entity.room.PukerGame;
import com.archy.dezhou.global.ConstList;
import com.archy.dezhou.security.JwtTokenProvider;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import com.archy.dezhou.service.RoomService;
import com.archy.dezhou.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Tag("functional")
@AutoConfigureMockMvc
@Tag("game-api")
class PukerGameIntegrationTest {
    
    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RoomService roomService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private Logger logger = LoggerFactory.getLogger(getClass());

    private List<Player> testPlayers;
    private PukerGame pukerGame;
    private Map<Integer, String> playerTokens;

    @BeforeEach
    public void setUp() {
        testPlayers = new ArrayList<>();
        playerTokens = new HashMap<>();

        IntStream.range(0, 4).forEach( i -> {
            Player player = createTestPlayer(i + 1000, "player" + i, 1000);
            testPlayers.add(player);

            String token = "Bearer " + createTestJwtToken(player.getUid());
            playerTokens.put(i + 1000, token);
        });

        this.pukerGame = createTestRoomForMultiplePlayers();
        this.roomService.addRoom(pukerGame);
        
        // Add our test players to the UserService's internal cache
        setupTestUsersInUserService();
        // addPlayersToRoom();

        // startGameWithMultiplePlayers();
    }

    private void setupTestUsersInUserService() {
        // Since we can't mock UserService, we'll use reflection to add our test players
        // to the UserService's internal playersMap
        try {
            java.lang.reflect.Field playersMapField = UserService.class.getDeclaredField("playersMap");
            playersMapField.setAccessible(true);
            Map<Integer, Player> playersMap = (Map<Integer, Player>) playersMapField.get(userService);
            
            testPlayers.forEach(player -> {
                playersMap.put(player.getUid(), player);
            });
            
            logger.info("Successfully added test players to UserService cache");
        } catch (Exception e) {
            logger.error("Failed to set up test users in UserService", e);
            throw new RuntimeException("Failed to set up test users", e);
        }
    }



    private Player createTestPlayer(int playerId, String name, int chips) {

        User user = new User();
        user.setUid(playerId);
        user.setAccount(name);
        user.setAllmoney(chips * 10);

        Player player = new Player(user);
        player.setUid(playerId);
        player.setAccount(name);
        player.setChips(chips);
        player.setAllmoney(chips * 10);
        player.setActive(true);
        player.setReady(true);
        player.setSeatId(playerId - 1000 + 1);

        return player;
    }

    private PukerGame createTestRoomForMultiplePlayers() {
        RoomDB roomDB = new RoomDB();
        roomDB.setId(200);
        roomDB.setName("multiplayer-test");
        roomDB.setRoomtype("public");
        roomDB.setBbet(10);
        roomDB.setSbet(5);
        roomDB.setMinbuy(500);
        roomDB.setMaxbuy(2000);

        PukerGame room = new PukerGame(roomDB);
        room.setRoomid(200);
        room.setName("multiplayer-test");
        room.setMaxPlayers(4);
        room.setMinPlayers(2);

        return room;
    }

    
    private void startGameWithMultiplePlayers(){

        testPlayers.forEach( player -> {
            this.playerJoinRoom(player);
            this.playerSitdown(player);
        });

        pukerGame.initGame();
        
        pukerGame.gameStartHandle();

    }

    private String createTestJwtToken(Integer uid) {
        // Find the player with this UID
        Player player = testPlayers.stream()
            .filter(p -> p.getUid() == uid)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Player not found: " + uid));
        
        // Generate a real JWT token using the actual JWT provider
        return jwtTokenProvider.generateToken(player);
    }

    @AfterEach
    public void cleanupMultiplayerGame() {
        this.testPlayers.clear();
        this.playerTokens.clear();
    }

    @Test
    void testPukerGameFollowBet() {

        startGameWithMultiplePlayers();

        followBet();
        followBet();
        followBet();
        followBet();

        assertEquals(2, pukerGame.getRound());
        this.check();
        this.check();
        this.check();
        this.check();

        assertEquals(3, pukerGame.getRound());
        this.check();
        this.check();
        this.check();
        this.check();

        assertEquals(4, pukerGame.getRound());
        this.check();
        this.check();
        this.check();
        this.check();

        pukerGame.gameStartHandle();
        assertEquals(2, pukerGame.getRoundNum());


    }

    private void playerJoinRoom(Player player) {
        // Set up security context for this player
        setupSecurityContextForPlayer(player);
        
        try {
            String token = getPlayerToken(player.getUid());

            // Use MockMvc instead of TestRestTemplate for better security context integration
            String response = this.mockMvc.perform(
                    org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .post("/api/v1/room/{roomId}/join", this.pukerGame.getRoomid())
                        .header("Authorization", token)
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                    .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            logger.info("========:" + response);
            
            // Parse the response to check if it's successful
            ApiResponse apiResponse = objectMapper.readValue(response, ApiResponse.class);
            assertTrue(apiResponse.isSuccess());
        } catch (Exception e) {
            logger.error("Error in playerJoinRoom", e);
            throw new RuntimeException("Failed to join room", e);
        } finally {
            // Clear security context after the call
            clearSecurityContext();
        }

    }

    private void playerSitdown(Player player) {
        // Create parameters for sitdown command
        Map<String, String> params = new HashMap<>();
        params.put("sid", String.valueOf(player.getSeatId()));
        params.put("cb", "1000"); // buy-in amount
        
        this.executePlayerActionWithParamsViaApi(player, ConstList.CMD_SITDOWN, params);

    }

    private void followBet() {
        Player currentPlayer = this.pukerGame.getCurrentPlayer();
        Map<String, String> params = new HashMap<>();
        this.executePlayerActionWithParamsViaApi(currentPlayer, ConstList.CMD_FOLLOW_BET, params);
    }

    private void check() {
        Player currentPlayer = this.pukerGame.getCurrentPlayer();
        Map<String, String> params = new HashMap<>();
        this.executePlayerActionWithParamsViaApi(currentPlayer, ConstList.CMD_CHECK, params);
    }

    private String getPlayerToken(int playerId) {
        return this.playerTokens.get(playerId);
    }

    /**
     * Set up Spring Security context for a player
     */
    private void setupSecurityContextForPlayer(Player player) {
        UsernamePasswordAuthenticationToken authentication = 
            new UsernamePasswordAuthenticationToken(player, null, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    /**
     * Clear Spring Security context
     */
    private void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    private void executePlayerActionViaApi(Player player, String action) {
        
        // Set up security context for this player
        setupSecurityContextForPlayer(player);
        
        try {
            String token = getPlayerToken(player.getUid());

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", token);

            ResponseEntity<ApiResponse> response = this.testRestTemplate.postForEntity(
                "/api/v1/game/{roomId}/action/{action}", headers, ApiResponse.class, pukerGame.getRoomid(), action);
            
            assertEquals(200, response.getStatusCodeValue());
            assertTrue(response.getBody().isSuccess());
        } finally {
            // Clear security context after the call
            clearSecurityContext();
        }

    }

    private void executePlayerActionWithParamsViaApi(Player player, String action, Map<String, String> params) {
        
        
        // Set up security context for this player
        setupSecurityContextForPlayer(player);
        
        try {
            String token = getPlayerToken(player.getUid());

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", token);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String,String>> entity = new HttpEntity<>(params, headers);

            ResponseEntity<ApiResponse> response = this.testRestTemplate.exchange(
                "/api/v1/game/{roomId}/action/{action}", 
                HttpMethod.POST, 
                entity, 
                ApiResponse.class, 
                pukerGame.getRoomid(), action);
            assertEquals(200, response.getStatusCodeValue());
            assertTrue(response.getBody().isSuccess());
        } finally {
            // Clear security context after the call
            clearSecurityContext();
        }
    }


}
