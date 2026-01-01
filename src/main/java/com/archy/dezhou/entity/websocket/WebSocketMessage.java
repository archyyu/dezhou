package com.archy.dezhou.entity.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Base WebSocket message structure for game events
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WebSocketMessage {
    private String type;
    private LocalDateTime timestamp;
    private String roomId;
    private String eventType;
    private Object data;
    
    public WebSocketMessage(String type, String roomId, String eventType, Object data) {
        this.type = type;
        this.timestamp = LocalDateTime.now();
        this.roomId = roomId;
        this.eventType = eventType;
        this.data = data;
    }
}