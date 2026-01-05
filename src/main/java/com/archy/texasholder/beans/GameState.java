package com.archy.texasholder.beans;

import java.util.List;
import java.util.Map;

import com.archy.texasholder.entity.Player;
import com.archy.texasholder.entity.Puke;
import com.archy.texasholder.entity.room.PukerGame;
import com.archy.texasholder.global.ConstList;

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
    private int roundIndex;
    private int roundNum;
    private int maxBet;

    private int countDown;
    private String gamePhase; // "betting", "showdown", etc.
    private List<PlayerState> players;
    private List<PlayerState> spectaclors;
    private List<Puke> publicPukers;
    private Map<Integer,Integer> winMap;
    private GameSettings settings;

    /**
     * Create GameStateResponse from Room and Game
     */
    public GameState(PukerGame game) {
        this.roomId = game.getRoomid();
        this.roomName = game.getName();
        this.playerCount = game.getPlayerCount();
        this.maxPlayers = game.getMaxUsers();
        
        // Add game-specific information
        Player currentPlayer = game.getCurrentPlayer();
        this.currentPlayerId = currentPlayer != null ? currentPlayer.getUid() : 0;
        this.currentPlayerSeat = currentPlayer != null ? currentPlayer.getSeatId() : 0;
        this.currentBetAmount = game.getCurrentRoundBet();
        this.potAmount = game.getTotalBet(); // game.getPotAmount();
        this.gamePhase = game.getRoomState().getName(); // game.getGamePhase();
        this.publicPukers = game.getPublicPukes(); // game.getCommunityCards();

        this.roundIndex = game.getRound();
        this.roundNum = game.getRoundNum();

        this.winMap = game.getWinMap();
        this.countDown = game.getSecsPassByTurn();

        this.maxBet = game.getMaxBet();
        
        // Convert players to PlayerState objects
        this.players = game.getPlayers().stream()
            .map(player -> new PlayerState(player))
            .toList();

        this.spectaclors = game.getSpectatorList().stream().map(player -> new PlayerState(player)).toList();
        
    }
    
}
