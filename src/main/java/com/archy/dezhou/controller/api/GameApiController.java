package com.archy.dezhou.controller.api;

import com.archy.dezhou.command.GameCommand;
import com.archy.dezhou.command.GameCommandFactory;
import com.archy.dezhou.container.JsonObjectWrapper;
import com.archy.dezhou.entity.ApiResponse;
import com.archy.dezhou.entity.Player;
import com.archy.dezhou.entity.response.GameStateResponse;
import com.archy.dezhou.entity.room.GameRoom;
import com.archy.dezhou.entity.room.PukerGame;
import com.archy.dezhou.global.ConstList;
import com.archy.dezhou.security.JwtTokenProvider;
import com.archy.dezhou.service.RoomService;
import com.archy.dezhou.service.UserService;

import jakarta.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

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
     * Common game operations endpoint
     * Handles various game commands like look card, bet, follow, etc.
     */
    @PostMapping("/{roomId}/actions")
    public ResponseEntity<ApiResponse<?>> handleGameAction(
            @PathVariable String roomId,
            @RequestParam String cmd,
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

            JsonObjectWrapper result = null;

            GameCommand gameCommand = this.gameCommandFactory.getCommand(cmd);

            result = gameCommand.execute(room, player, additionalParams);
            
            return successResponse(result.toJSONString());
            
        } catch (Exception e) {
            logger.error("err", e);
            return errorResponse("GameActionProcessingFailed: " + e.getMessage());
        }
    }

    /**
     * Other game operations endpoint
     */
    @PostMapping("/{roomId}/other")
    public ResponseEntity<ApiResponse<?>> handleOtherGameOperations(
            @PathVariable String roomId,
            @RequestParam String uid,
            @RequestParam String cmd) {
        
        try {
            Player user = validateUserAndRoom(uid, roomId);
            if (user == null) {
                return errorResponse("UserNotLogined");
            }
            
            // Handle flush achievements command
            if (ConstList.CMD_FLUSHACH.equals(cmd)) {
                return successResponse(handleFlushAchievements());
            }
            
            return errorResponse("InvalidOtherCommand");
            
        } catch (Exception e) {
            return errorResponse("OtherGameOperationFailed: " + e.getMessage());
        }
    }

    // Helper methods for each game command

    private JsonObjectWrapper handleFlushAchievements() {
        return new JsonObjectWrapper();
    }

    // Utility methods
    private Player validateUserAndRoom(String uid, String roomIdStr) {
        try {
            // First try to get user from security context (JWT authentication)
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof Player) {
                Player authenticatedUser = (Player) authentication.getPrincipal();
                
                // Verify that the uid parameter matches the authenticated user
                if (uid != null && !uid.isEmpty()) {
                    int uidInt = Integer.parseInt(uid);
                    if (authenticatedUser.getUid() == uidInt) {
                        return authenticatedUser;
                    }
                }
                return authenticatedUser;
            }
            
            // Fallback to legacy authentication (for compatibility)
            int uidInt = Integer.parseInt(uid);
            Player user = this.userService.getUserByUserId(uidInt);

            if (user == null) {
                return null;
            }
            
            // Room validation is done in the specific handlers
            return user;
        } catch (NumberFormatException e) {
            return null;
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
            GameStateResponse gameState = new GameStateResponse(room);
            
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
            
            JsonObjectWrapper playerStatus = new JsonObjectWrapper();
            playerStatus.put("playerId", player.getUid());
            playerStatus.put("seatId", player.getSeatId());
            playerStatus.put("chips", player.getChips());
            playerStatus.put("currentBet", player.getCurrentBet());
            // playerStatus.put("hasLooked", player.hasLooked().toString());
            playerStatus.put("isActive", player.isActive());
            playerStatus.put("isAllIn", player.isAllIn());
            
            return successResponse(playerStatus);
        } catch (Exception e) {
            return errorResponse("FailedToGetPlayerStatus: " + e.getMessage());
        }
    }
}