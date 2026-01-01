package com.archy.dezhou.entity.websocket;

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
}