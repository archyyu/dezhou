package com.archy.dezhou.beans;

import java.util.ArrayList;
import java.util.List;

import com.archy.dezhou.entity.Puke;
import com.archy.dezhou.entity.room.PukerGame;

import lombok.Data;

@Data
public class GameState {

    private int roomId;
    private String creator;
    private String showname;
    private int maxUsers;

    private int minbuy;
    private int maxbuy;
    private int sbet; // small bet
    private int bbet; // big bet
    
    private String roomType;
    private String status;
    
    private String roomName;
    private String gameStatus; // "active", "waiting", "finished"
    private int currentPlayerId;
    private int currentPlayerSeat;
    private int currentBetAmount;
    private int playerCount;
    private int maxPlayers;
    private int potAmount;
    private String gamePhase; // "betting", "showdown", etc.
    private List<PlayerState> players;
    private List<PlayerState> spectaclors;
    private List<Puke> publicPukers;
    private GameSettings settings;

    /**
     * Create GameStateResponse from Room and Game
     */
    public GameState(PukerGame game) {
        this.roomId = game.getRoomid();
        this.roomName = game.getName();
        this.playerCount = game.getPlayerCount();
        this.maxPlayers = game.getMaxUsers();
        this.gameStatus = game.isGame() ? "active" : "waiting";
        
        if (game != null) {
            // Add game-specific information
            this.currentPlayerId = game.getCurrentPlayer().getUid();
            this.currentPlayerSeat = game.getCurrentPlayer().getSeatId();
            this.currentBetAmount = 0;//game.getCurrentBet();
            this.potAmount = 0; // game.getPotAmount();
            this.gamePhase = "betting"; // game.getGamePhase();
            this.publicPukers = new ArrayList<>(); // game.getCommunityCards();
            
            // Convert players to PlayerState objects
            this.players = game.getPlayers().stream()
                .map(player -> new PlayerState(player))
                .toList();

            this.spectaclors = game.getSpectatorList().stream().map(player -> new PlayerState(player)).toList();
        }
    }
    
}
