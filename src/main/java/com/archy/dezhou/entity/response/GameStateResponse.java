package com.archy.dezhou.entity.response;

import com.archy.dezhou.entity.Player;
import com.archy.dezhou.entity.room.PukerGame;
import com.archy.dezhou.entity.room.Room;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Game State Response Entity
 * Replaces XML responses for game state information
 */
@Data
@NoArgsConstructor
public class GameStateResponse {
    private int roomId;
    private String roomName;
    private int currentPlayers;
    private int maxPlayers;
    private String gameStatus; // "active", "waiting", "finished"
    private String currentTurnPlayerId;
    private int currentBetAmount;
    private int potAmount;
    private String gamePhase; // "betting", "showdown", etc.
    private List<PlayerState> players;
    private List<Card> communityCards;
    private GameSettings settings;

    /**
     * Create GameStateResponse from Room and Game
     */
    public GameStateResponse(Room room, PukerGame game) {
        this.roomId = room.getRoomId();
        this.roomName = room.getName();
        this.currentPlayers = room.getPlayerCount();
        this.maxPlayers = room.getMaxUsers();
        this.gameStatus = room.isGame() ? "active" : "waiting";
        
        if (game != null) {
            // Add game-specific information
            this.currentTurnPlayerId = game.getCurrentTurnPlayerId();
            this.currentBetAmount = game.getCurrentBet();
            this.potAmount = game.getPotAmount();
            this.gamePhase = game.getGamePhase();
            this.communityCards = game.getCommunityCards();
            
            // Convert players to PlayerState objects
            this.players = room.getPlayers().stream()
                .map(player -> new PlayerState(player, game))
                .toList();
            
            // Add game settings
            this.settings = new GameSettings(room, game);
        }
    }

    /**
     * Player State within a game
     */
    @Data
    @NoArgsConstructor
    public static class PlayerState {
        private int playerId;
        private String playerName;
        private int seatId;
        private int chips;
        private int currentBet;
        private boolean hasLooked;
        private boolean isActive;
        private boolean isAllIn;
        private boolean isDealer;
        private boolean isCurrentTurn;
        private List<Card> cards;
        private String status; // "playing", "folded", "all-in", etc.
        
        public PlayerState(Player player, PukerGame game) {
            this.playerId = player.getUid();
            this.playerName = player.getAccount();
            this.seatId = player.getSeatId();
            this.chips = player.getChips();
            this.currentBet = player.getCurrentBet();
            this.hasLooked = player.isLooked();
            this.isActive = player.isActive();
            this.isAllIn = player.isAllIn();
            this.isDealer = player.isDealer();
            this.isCurrentTurn = game != null && game.getCurrentTurnPlayerId().equals(String.valueOf(player.getUid()));
            this.cards = player.getCards();
            this.status = determinePlayerStatus(player, game);
        }
        
        private String determinePlayerStatus(Player player, PukerGame game) {
            if (game == null) return "waiting";
            if (!player.isActive()) return "folded";
            if (player.isAllIn()) return "all-in";
            if (game.getCurrentTurnPlayerId().equals(String.valueOf(player.getUid()))) return "current-turn";
            return "playing";
        }
    }

    /**
     * Card representation
     */
    @Data
    @NoArgsConstructor
    public static class Card {
        private String suit; // "hearts", "diamonds", "clubs", "spades"
        private String rank; // "2", "3", ..., "10", "J", "Q", "K", "A"
        private String code; // "H2", "D10", "CA", etc.
        private int value; // Numerical value for comparison
        
        public Card(String suit, String rank, String code, int value) {
            this.suit = suit;
            this.rank = rank;
            this.code = code;
            this.value = value;
        }
    }

    /**
     * Game settings and configuration
     */
    @Data
    @NoArgsConstructor
    public static class GameSettings {
        private int smallBet;
        private int bigBet;
        private int minBuyIn;
        private int maxBuyIn;
        private int timePerTurn;
        private String gameType; // "Texas Hold'em", etc.
        private String bettingStructure; // "No Limit", "Pot Limit", etc.
        
        public GameSettings(Room room, PukerGame game) {
            this.smallBet = room.getSbet();
            this.bigBet = room.getBbet();
            this.minBuyIn = room.getMinbuy();
            this.maxBuyIn = room.getMaxbuy();
            this.timePerTurn = 30; // Default 30 seconds
            this.gameType = "Texas Hold'em";
            this.bettingStructure = "No Limit";
        }
    }
}