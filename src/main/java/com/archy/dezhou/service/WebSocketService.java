package com.archy.dezhou.service;

import com.archy.dezhou.beans.GameState;
import com.archy.dezhou.entity.room.PukerGame;
import com.archy.dezhou.entity.websocket.GameEventMessage;
import com.archy.dezhou.entity.websocket.WebSocketMessage;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * WebSocket Service for handling game-related WebSocket communications
 * 
 * This service provides methods to send WebSocket notifications to clients,
 * encapsulating the WebSocket messaging logic and providing a clean API
 * for other components to use.
 */
@Service
public class WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Send a game event notification to a specific room
     * 
     * @param roomId The room ID to send the notification to
     * @param eventType The type of event (e.g., "GAME_STARTED", "PLAYER_ACTION")
     * @param data The event data (can be any object that will be serialized to JSON)
     */
    public void sendGameEvent(String roomId, String eventType, Object data) {
        GameEventMessage message = new GameEventMessage(
                roomId, 
                eventType, 
                data, 
                "game-" + roomId, 
                1, // Default round number
                "system"
        );
        
        String destination = "/topic/game." + roomId + ".events";
        messagingTemplate.convertAndSend(destination, message);
    }

    /**
     * Send a player action notification to a specific room
     * 
     * @param roomId The room ID where the action occurred
     * @param playerId The player ID who performed the action
     * @param actionType The action type (e.g., "BET_FOLLOWED", "PLAYER_FOLDED")
     * @param actionData Additional action data (bet amount, etc.)
     */
    public void sendPlayerAction(String roomId, String playerId, String actionType, Object actionData) {
        GameEventMessage message = new GameEventMessage(
                roomId, 
                actionType, 
                actionData, 
                "game-" + roomId, 
                1, // Default round number
                playerId
        );
        
        String destination = "/topic/game." + roomId + ".events";
        messagingTemplate.convertAndSend(destination, message);
    }

    /**
     * Send a broadcast message to all connected clients
     * 
     * @param destination The destination topic/queue to send to
     * @param message The message to broadcast
     */
    public void sendBroadcast(String destination, WebSocketMessage message) {
        messagingTemplate.convertAndSend(destination, message);
    }

    /**
     * Send a message to a specific user
     * 
     * @param userId The user ID to send the message to
     * @param destination The destination within the user's queue
     * @param message The message to send
     */
    public void sendToUser(String userId, String destination, Object message) {
        String userDestination = "/user/" + userId + destination;
        messagingTemplate.convertAndSend(userDestination, message);
    }

    /**
     * Send a system notification to a specific room
     * 
     * @param roomId The room ID to notify
     * @param notificationType The type of notification
     * @param notificationData The notification data
     */
    public void sendSystemNotification(String roomId, String notificationType, Object notificationData) {
        WebSocketMessage message = new WebSocketMessage(
                "SYSTEM_NOTIFICATION",
                roomId,
                notificationType,
                notificationData
        );
        
        String destination = "/topic/game." + roomId + ".notifications";
        messagingTemplate.convertAndSend(destination, message);
    }

    /**
     * Send a game state update to a specific room
     * 
     * @param roomId The room ID to update
     * @param gameState The complete game state object
     */
    public void sendGameStateUpdate(PukerGame game) {
        
        GameState gameState = new GameState(game);
        WebSocketMessage message = new WebSocketMessage(
                "GAME_STATE_UPDATE",
                game.getRoomid() + "",
                "STATE_UPDATE",
                gameState
        );
        
        String destination = "/topic/game." + game.getRoomid() + ".state";
        messagingTemplate.convertAndSend(destination, message);
    }
}