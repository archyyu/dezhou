package com.archy.dezhou.entity.response;

import com.archy.dezhou.entity.room.GameRoom;
import com.archy.dezhou.entity.room.PukerGame;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Room Response Entity
 * Replaces XML responses for room information
 */
@Data
@NoArgsConstructor
public class RoomResponse {
    private int roomId;
    private String name;
    private String creator;
    private String showname;
    private int maxUsers;
    private int currentUsers;
    private int minbuy;
    private int maxbuy;
    private int sbet; // small bet
    private int bbet; // big bet
    private String roomType;
    private String status; // "open", "full", "private", etc.
    private List<RoomPlayer> players;
    private GameStateResponse gameState;

    /**
     * Create RoomResponse from GameRoom
     */
    public RoomResponse(PukerGame room) {
        this.roomId = room.getRoomid();
        this.name = room.getName();
        this.creator = room.getCreator();
        this.showname = room.getShowname();
        this.maxUsers = room.getMaxUsers();
        this.minbuy = room.getMinbuy();
        this.maxbuy = room.getMaxbuy();
        this.sbet = room.getSbet();
        this.bbet = room.getBbet();
        this.roomType = "regular"; // Default type
        this.status = determineRoomStatus(room);
        
        // Convert players to RoomPlayer objects
        this.players = room.getPlayers().stream()
            .map(RoomPlayer::new)
            .toList();
        
        // Add game state if game is active
        if (room.isGame()) {
            this.gameState = new GameStateResponse(room);
        }
    }

    private String determineRoomStatus(GameRoom room) {
        int currentPlayers = room.getPlayerCount();
        if (currentPlayers >= room.getMaxUsers()) {
            return "full";
        } else if (room.isGame()) {
            return "in-game";
        } else {
            return "open";
        }
    }

    /**
     * Player information within a room
     */
    @Data
    @NoArgsConstructor
    public static class RoomPlayer {
        private int playerId;
        private String playerName;
        private int seatId;
        private int chips;
        private String status; // "playing", "sitting-out", "away", etc.
        private boolean isReady;
        
        public RoomPlayer(com.archy.dezhou.entity.Player player) {
            this.playerId = player.getUid();
            this.playerName = player.getAccount();
            this.seatId = player.getSeatId();
            this.chips = player.getChips();
            this.status = player.isActive() ? "playing" : "sitting-out";
            this.isReady = player.isReady();
        }
    }

    /**
     * Room list item (simplified version for room lists)
     */
    @Data
    @NoArgsConstructor
    public static class RoomListItem {
        private int roomId;
        private String name;
        private int currentPlayers;
        private int maxPlayers;
        private int minBuyIn;
        private int maxBuyIn;
        private int smallBet;
        private int bigBet;
        private String creator;
        private String showname;
        private String status;

        public RoomListItem(GameRoom room) {
            this.roomId = room.getRoomid();
            this.name = room.getName();
            this.currentPlayers = room.getPlayerCount();
            this.maxPlayers = room.getMaxUsers();
            this.minBuyIn = room.getMinbuy();
            this.maxBuyIn = room.getMaxbuy();
            this.smallBet = room.getSbet();
            this.bigBet = room.getBbet();
            this.creator = room.getCreator();
            this.showname = room.getShowname();
            this.status = determineRoomStatus(room);
        }

        private String determineRoomStatus(GameRoom room) {
            int currentPlayers = room.getPlayerCount();
            if (currentPlayers >= room.getMaxUsers()) {
                return "full";
            } else if (room.isGame()) {
                return "in-game";
            } else {
                return "open";
            }
        }
    }
}