package com.archy.dezhou.controller.websocket;

import com.archy.dezhou.entity.websocket.WebSocketMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

/**
 * Test WebSocket Controller for testing WebSocket functionality
 */
@Controller
public class TestWebSocketController {

    /**
     * Test endpoint for WebSocket messages
     * 
     * @param message The incoming message
     * @return The same message echoed back
     */
    @MessageMapping("/test.message")
    @SendTo("/topic/test")
    public WebSocketMessage handleTestMessage(WebSocketMessage message) {
        System.out.println("Received test message: " + message.getEventType());
        return message;
    }

    /**
     * Test endpoint for echo messages
     * 
     * @param message The incoming message
     * @return The echoed message
     */
    @MessageMapping("/echo")
    @SendTo("/topic/echo")
    public String handleEcho(String message) {
        System.out.println("Echo: " + message);
        return "Echo: " + message;
    }
}