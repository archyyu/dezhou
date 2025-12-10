package com.archy.dezhou.controller.api;

import com.archy.dezhou.entity.ApiResponse;
import com.archy.dezhou.entity.User;
import com.archy.dezhou.entity.room.Room;
import com.archy.dezhou.global.UserModule;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Message API Controller - Replaces PubMsgBacklet
 * Handles public messaging and chat functionality within rooms
 */
@RestController
@RequestMapping("/api/v1/messages")
public class MessageApiController extends BaseApiController {

    /**
     * Send a message to a room
     * Replaces the legacy public messaging functionality
     */
    @PostMapping("/rooms/{roomName}")
    public ResponseEntity<ApiResponse<?>> sendRoomMessage(
            @PathVariable String roomName,
            @RequestParam String uid,
            @RequestParam String message,
            @RequestParam(required = false, defaultValue = "chat") String messageType,
            @RequestParam(required = false, defaultValue = "room") String command) {
        
        try {
            // Validate the room exists
            Room room = UserModule.getInstance().getRoomByName(roomName);
            if (room == null) {
                return errorResponse("RoomNotFound");
            }
            
            // Validate the user exists
            User user = getUserById(Integer.parseInt(uid));
            if (user == null) {
                return errorResponse("UserNotFound");
            }
            
            // Validate message content
            if (message == null || message.trim().isEmpty()) {
                return errorResponse("MessageCannotBeEmpty");
            }
            
            // Validate message type and command
            if (!validateRequiredParams(messageType, command)) {
                return errorResponse("InvalidMessageParameters");
            }
            
            // Handle world messages (broadcast to all rooms)
            if ("world".equals(command)) {
                return handleWorldMessage(user, message, messageType);
            }
            
            // Handle regular room messages
            return handleRoomMessage(room, user, message, messageType);
            
        } catch (Exception e) {
            return errorResponse("MessageSendingFailed: " + e.getMessage());
        }
    }

    /**
     * Handle world/broadcast messages
     */
    private ResponseEntity<ApiResponse<?>> handleWorldMessage(User user, String message, String messageType) {
        // TODO: Implement world message broadcasting logic
        // This would broadcast to all rooms/users
        
        // For now, return a success response
        return successResponse("World message would be broadcast to all users");
    }

    /**
     * Handle regular room messages
     */
    private ResponseEntity<ApiResponse<?>> handleRoomMessage(Room room, User user, String message, String messageType) {
        // TODO: Implement actual room message broadcasting
        // This would send the message to all users in the specified room
        
        // Create a message object to return
        MessageResponse messageResponse = new MessageResponse();
        messageResponse.setRoomId(room.getRoomId());
        messageResponse.setRoomName(room.getName());
        messageResponse.setSenderId(user.getUid());
        messageResponse.setSenderName(user.getAccount());
        messageResponse.setMessage(message);
        messageResponse.setMessageType(messageType);
        messageResponse.setTimestamp(System.currentTimeMillis());
        
        return successResponse(messageResponse);
    }

    /**
     * Get message history for a room
     */
    @GetMapping("/rooms/{roomName}/history")
    public ResponseEntity<ApiResponse<?>> getRoomMessageHistory(
            @PathVariable String roomName,
            @RequestParam(required = false, defaultValue = "50") int limit,
            @RequestParam(required = false) String since) {
        
        try {
            Room room = UserModule.getInstance().getRoomByName(roomName);
            if (room == null) {
                return errorResponse("RoomNotFound");
            }
            
            // TODO: Implement actual message history retrieval
            // For now, return a placeholder response
            
            MessageHistory history = new MessageHistory();
            history.setRoomId(room.getRoomId());
            history.setRoomName(room.getName());
            history.setMessageCount(0);
            history.setMessages(new MessageResponse[0]);
            
            return successResponse(history);
            
        } catch (Exception e) {
            return errorResponse("FailedToGetMessageHistory: " + e.getMessage());
        }
    }

    /**
     * Send a private message to a specific user
     */
    @PostMapping("/private")
    public ResponseEntity<ApiResponse<?>> sendPrivateMessage(
            @RequestParam String fromUid,
            @RequestParam String toUid,
            @RequestParam String message) {
        
        try {
            User fromUser = getUserById(Integer.parseInt(fromUid));
            User toUser = getUserById(Integer.parseInt(toUid));
            
            if (fromUser == null || toUser == null) {
                return errorResponse("UserNotFound");
            }
            
            if (message == null || message.trim().isEmpty()) {
                return errorResponse("MessageCannotBeEmpty");
            }
            
            // TODO: Implement actual private messaging
            
            PrivateMessageResponse response = new PrivateMessageResponse();
            response.setFromUserId(fromUser.getUid());
            response.setFromUsername(fromUser.getAccount());
            response.setToUserId(toUser.getUid());
            response.setToUsername(toUser.getAccount());
            response.setMessage(message);
            response.setTimestamp(System.currentTimeMillis());
            response.setDelivered(true);
            
            return successResponse(response);
            
        } catch (Exception e) {
            return errorResponse("PrivateMessageFailed: " + e.getMessage());
        }
    }

    /**
     * Message response DTO
     */
    private static class MessageResponse {
        private int roomId;
        private String roomName;
        private int senderId;
        private String senderName;
        private String message;
        private String messageType;
        private long timestamp;

        // Getters and Setters
        public int getRoomId() { return roomId; }
        public void setRoomId(int roomId) { this.roomId = roomId; }
        public String getRoomName() { return roomName; }
        public void setRoomName(String roomName) { this.roomName = roomName; }
        public int getSenderId() { return senderId; }
        public void setSenderId(int senderId) { this.senderId = senderId; }
        public String getSenderName() { return senderName; }
        public void setSenderName(String senderName) { this.senderName = senderName; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public String getMessageType() { return messageType; }
        public void setMessageType(String messageType) { this.messageType = messageType; }
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    }

    /**
     * Message history DTO
     */
    private static class MessageHistory {
        private int roomId;
        private String roomName;
        private int messageCount;
        private MessageResponse[] messages;

        // Getters and Setters
        public int getRoomId() { return roomId; }
        public void setRoomId(int roomId) { this.roomId = roomId; }
        public String getRoomName() { return roomName; }
        public void setRoomName(String roomName) { this.roomName = roomName; }
        public int getMessageCount() { return messageCount; }
        public void setMessageCount(int messageCount) { this.messageCount = messageCount; }
        public MessageResponse[] getMessages() { return messages; }
        public void setMessages(MessageResponse[] messages) { this.messages = messages; }
    }

    /**
     * Private message response DTO
     */
    private static class PrivateMessageResponse {
        private int fromUserId;
        private String fromUsername;
        private int toUserId;
        private String toUsername;
        private String message;
        private long timestamp;
        private boolean delivered;

        // Getters and Setters
        public int getFromUserId() { return fromUserId; }
        public void setFromUserId(int fromUserId) { this.fromUserId = fromUserId; }
        public String getFromUsername() { return fromUsername; }
        public void setFromUsername(String fromUsername) { this.fromUsername = fromUsername; }
        public int getToUserId() { return toUserId; }
        public void setToUserId(int toUserId) { this.toUserId = toUserId; }
        public String getToUsername() { return toUsername; }
        public void setToUsername(String toUsername) { this.toUsername = toUsername; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
        public boolean isDelivered() { return delivered; }
        public void setDelivered(boolean delivered) { this.delivered = delivered; }
    }
}