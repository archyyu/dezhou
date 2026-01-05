package com.archy.texasholder.controller.websocket;

import com.archy.texasholder.entity.websocket.WebSocketMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

/**
 * Test WebSocket Controller for testing WebSocket functionality
 */
@Controller
public class TestWebSocketController {



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