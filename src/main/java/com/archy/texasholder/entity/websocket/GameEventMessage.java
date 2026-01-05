package com.archy.texasholder.entity.websocket;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Game event specific message for WebSocket notifications
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GameEventMessage extends WebSocketMessage {
    private String gameId;
    private int roundNumber;
    private String currentPlayerId;
    
    public GameEventMessage(String roomId, String eventType, Object data, String gameId, int roundNumber, String currentPlayerId) {
        super("GAME_EVENT", roomId, eventType, data);
        this.gameId = gameId;
        this.roundNumber = roundNumber;
        this.currentPlayerId = currentPlayerId;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public int getRoundNumber() {
        return roundNumber;
    }

    public void setRoundNumber(int roundNumber) {
        this.roundNumber = roundNumber;
    }

    public String getCurrentPlayerId() {
        return currentPlayerId;
    }

    public void setCurrentPlayerId(String currentPlayerId) {
        this.currentPlayerId = currentPlayerId;
    }
}