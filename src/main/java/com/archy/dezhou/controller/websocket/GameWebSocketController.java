package com.archy.dezhou.controller.websocket;

import com.archy.dezhou.entity.websocket.GameEventMessage;
import com.archy.dezhou.entity.websocket.WebSocketMessage;
import com.archy.dezhou.service.WebSocketService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

/**
 * WebSocket Controller for handling incoming WebSocket messages
 * 
 * This controller receives WebSocket messages from clients and can broadcast them.
 * For sending notifications, use the WebSocketService instead.
 */
@Controller
public class GameWebSocketController {

    private final WebSocketService webSocketService;

    public GameWebSocketController(WebSocketService webSocketService) {
        this.webSocketService = webSocketService;
    }

    /**
     * Handle game event messages from clients and broadcast to specific room
     * 
     * @param message The game event message from client
     * @return The same message (for potential broadcast)
     */
    @MessageMapping("/game.event")
    @SendTo("/topic/game.events")
    public GameEventMessage handleGameEvent(@Payload GameEventMessage message) {
        // Broadcast the message to all subscribers of the specific game room
        // The actual broadcasting is handled by the @SendTo annotation
        return message;
    }

    /**
     * Handle general WebSocket messages from clients
     * 
     * @param message The WebSocket message from client
     * @return The same message echoed back
     */
    @MessageMapping("/ws.message")
    @SendTo("/topic/public")
    public WebSocketMessage handleMessage(@Payload WebSocketMessage message) {
        return message;
    }

    /**
     * Handle test messages for debugging
     * 
     * @param message The test message
     * @return Echo response
     */
    @MessageMapping("/test.message")
    @SendTo("/topic/test")
    public WebSocketMessage handleTestMessage(@Payload WebSocketMessage message) {
        System.out.println("Received test message: " + message.getEventType());
        return message;
    }
}