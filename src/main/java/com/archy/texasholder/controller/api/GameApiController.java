package com.archy.texasholder.controller.api;

import com.archy.texasholder.GameCmdException;
import com.archy.texasholder.beans.GameState;
import com.archy.texasholder.beans.PlayerState;
import com.archy.texasholder.command.GameCommand;
import com.archy.texasholder.command.GameCommandFactory;

import com.archy.texasholder.controller.websocket.GameWebSocketController;
import com.archy.texasholder.entity.ApiResponse;
import com.archy.texasholder.entity.Player;
import com.archy.texasholder.entity.room.GameRoom;
import com.archy.texasholder.entity.room.PukerGame;
import com.archy.texasholder.global.ConstList;
import com.archy.texasholder.security.JwtTokenProvider;
import com.archy.texasholder.service.RoomService;
import com.archy.texasholder.service.UserService;
import com.archy.texasholder.service.WebSocketService;

import jakarta.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Game API Controller - Replaces PukeLogicBacket
 * Handles all poker game logic including betting, card operations, and game state management
 */
@RestController
@RequestMapping("/api/v1/game")
public class GameApiController extends BaseApiController {


    @Resource
    private RoomService roomService;

    @Resource
    private UserService userService;

    @Resource
    private JwtTokenProvider jwtTokenProvider;

    @Resource
    private GameCommandFactory gameCommandFactory;

    @Resource
    private WebSocketService webSocketService;

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Protected getter for roomService - used for testing
     */
    protected RoomService getRoomService() {
        return roomService;
    }

    /**
     * Protected getter for userService - used for testing
     */
    protected UserService getUserService() {
        return userService;
    }

    /**
     * Send WebSocket notification about game actions
     * 
     * @param roomId The room ID where the action occurred
     * @param actionType The type of action performed
     * @param player The player who performed the action
     * @param additionalParams Additional parameters from the request
     */
    private void sendGameActionNotification(String roomId, String actionType, Player player) {
        try {
            // Create notification data
            PlayerState playerState = new PlayerState(player);
            
            // Send the notification via WebSocket service
            webSocketService.sendPlayerAction(
                roomId, 
                player.getUid() + "", 
                "PLAYER_ACTION_" + actionType.toUpperCase(), 
                playerState
            );
            
            logger.info("Sent WebSocket notification for action {} by player {} in room {}", 
                actionType, player.getUid(), roomId);
                
        } catch (Exception e) {
            logger.error("Failed to send WebSocket notification for action {}: {}", actionType, e.getMessage());
        }
    }

    /**
     * Common game operations endpoint
     * Handles various game commands like look card, bet, follow, etc.
     */
    @PostMapping("/{roomId}/action/{cmd}")
    public ResponseEntity<ApiResponse<?>> handleGameAction(
            @PathVariable String roomId,
            @PathVariable String cmd,
            @RequestBody(required = false) Map<String, String> additionalParams) {
        
        try {
            // Validate user and room
            Player user = getAuthentificatedPlayer();
            if (user == null) {
                return errorResponse("UserNotLogined");
            }

            PukerGame room = this.roomService.getRoom(user.getRoomid());
            if (room == null) {
                return errorResponse("RoomNotFound");
            }

            Player player = this.userService.getUserByUserId(user.getUid());
            
            if (player != null) {
                player.clearDropCardNum();
            }


            GameCommand gameCommand = this.gameCommandFactory.getCommand(cmd);

            boolean result = gameCommand.execute(room, player, additionalParams);
            
            if (result) {
                // Send WebSocket notification about the game action
                sendGameActionNotification(roomId, cmd, player);
                
                return successResponse( new GameState(room) );
            } else {
                return errorResponse("failed");
            }
            
        } catch (GameCmdException e) {
            logger.error("err", e);
            return errorResponse("GameActionProcessingFailed: " + e.getMessage());
        } 
    }

    /**
     * Get current game state for a room
     */
    @GetMapping("/{roomId}/state")
    public ResponseEntity<ApiResponse<?>> getGameState(@PathVariable String roomId) {
        try {
            PukerGame room = this.roomService.getRoom(Integer.parseInt(roomId));
            if (room == null) {
                return errorResponse("RoomNotFound");
            }

            // Use the new GameStateResponse entity instead of JsonObjectWrapper
            GameState gameState = new GameState(room);
            
            return successResponse(gameState);
        } catch (Exception e) {
            
            return errorResponse("FailedToGetGameState: " + e.getMessage());
        }
    }

    /**
     * Get player's current game status
     */
    @GetMapping("/{roomId}/players/{uid}/status")
    public ResponseEntity<ApiResponse<?>> getPlayerGameStatus(
            @PathVariable String roomId) {
        
        try {
            Player user = getAuthentificatedPlayer();
            if (user == null) {
                return errorResponse("UserNotFound");
            }

            GameRoom room = roomService.getRoomByName(roomId);
            if (room == null) {
                return errorResponse("RoomNotFound");
            }
                        
            Player player = room.findPlayerByUser(user);
            if (player == null) {
                return errorResponse("PlayerNotInGame");
            }
            
            PlayerState playerStatus = player.toPlayerState();
            return successResponse(playerStatus);
        } catch (Exception e) {
            return errorResponse("FailedToGetPlayerStatus: " + e.getMessage());
        }
    }
}