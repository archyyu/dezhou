package com.archy.dezhou.controller.websocket;

import com.archy.dezhou.entity.websocket.GameEventMessage;
import com.archy.dezhou.entity.websocket.WebSocketMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

/**
 * WebSocket Controller for handling game-related WebSocket messages
 * 
 * This controller handles real-time game events and broadcasts them to connected clients.
 */
@Controller
public class GameWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    public GameWebSocketController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Handle game event messages and broadcast to specific room
     * 
     * @param message The game event message
     * @param headerAccessor Accessor for message headers
     */
    @MessageMapping("/game.event")
    public void handleGameEvent(@Payload GameEventMessage message, SimpMessageHeaderAccessor headerAccessor) {
        // Broadcast the message to all subscribers of the specific game room
        String destination = "/topic/game." + message.getRoomId() + ".events";
        messagingTemplate.convertAndSend(destination, message);
    }

    /**
     * Handle general WebSocket messages
     * 
     * @param message The WebSocket message
     */
    @MessageMapping("/ws.message")
    @SendTo("/topic/public")
    public WebSocketMessage handleMessage(@Payload WebSocketMessage message) {
        return message;
    }

    /**
     * Send a game event notification to a specific room
     * 
     * @param roomId The room ID to send the notification to
     * @param eventType The type of event
     * @param data The event data
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
     * Send a player action notification
     * 
     * @param roomId The room ID
     * @param playerId The player ID who performed the action
     * @param actionType The action type (e.g., "BET_FOLLOWED", "PLAYER_FOLDED")
     * @param actionData Additional action data
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
}