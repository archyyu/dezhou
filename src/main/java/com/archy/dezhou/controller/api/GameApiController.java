package com.archy.dezhou.controller.api;

import com.archy.dezhou.container.ActionscriptObject;
import com.archy.dezhou.entity.ApiResponse;
import com.archy.dezhou.entity.Player;
import com.archy.dezhou.entity.response.GameStateResponse;
import com.archy.dezhou.entity.room.GameRoom;
import com.archy.dezhou.entity.room.PukerGame;
import com.archy.dezhou.global.ConstList;
import com.archy.dezhou.service.RoomService;
import com.archy.dezhou.service.UserService;

import jakarta.annotation.Resource;

import org.springframework.http.ResponseEntity;
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

    /**
     * Common game operations endpoint
     * Handles various game commands like look card, bet, follow, etc.
     */
    @PostMapping("/{roomId}/actions")
    public ResponseEntity<ApiResponse<?>> handleGameAction(
            @PathVariable String roomId,
            @RequestParam String uid,
            @RequestParam String cmd,
            @RequestBody(required = false) Map<String, String> additionalParams) {
        
        try {
            // Validate user and room
            Player user = validateUserAndRoom(uid, roomId);
            if (user == null) {
                return errorResponse("UserNotLogined");
            }

            GameRoom room = this.roomService.getRoom(user.getRoomId());
            if (room == null) {
                return errorResponse("RoomNotFound");
            }
            
            PukerGame game = room.getPokerGame();
            Player player = game.findPlayerByUser(user);
            
            if (player != null) {
                player.clearDropCardNum();
            }
            
            ActionscriptObject result = null;
            
            // Handle different game commands
            switch (cmd) {
                case ConstList.CMD_ROOMINFO:
                    result = handleRoomInfo(room);
                    break;
                case ConstList.CMD_LOOK_CARD:
                    result = handleLookCard(game, player);
                    break;
                case ConstList.CMD_ADD_BET:
                    result = handleAddBet(game, player, additionalParams);
                    break;
                case ConstList.CMD_FOLLOW_BET:
                    result = handleFollowBet(game, player, additionalParams);
                    break;
                case ConstList.CMD_DROP_CARD:
                    result = handleDropCard(game, player);
                    break;
                case ConstList.CMD_ALL_IN:
                    result = handleAllIn(game, player, additionalParams);
                    break;
                case ConstList.CMD_SITDOWN:
                    result = handleSitDown(room, user, additionalParams);
                    break;
                case ConstList.CMD_STANDUP:
                    result = handleStandUp(room, user, game);
                    break;
                case ConstList.CMD_LEAVE:
                    result = handleLeave(room, user, game, player);
                    break;
                default:
                    return errorResponse("InvalidGameCommand");
            }
            
            if (result == null) {
                return errorResponse("GameActionFailed");
            }
            
            return successResponse(result);
            
        } catch (Exception e) {
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
                return handleFlushAchievements();
            }
            
            return errorResponse("InvalidOtherCommand");
            
        } catch (Exception e) {
            return errorResponse("OtherGameOperationFailed: " + e.getMessage());
        }
    }

    // Helper methods for each game command

    private ActionscriptObject handleRoomInfo(GameRoom room) {
        return room.toAsObj();
    }

    private ActionscriptObject handleLookCard(PukerGame game, Player player) {
        return game.playerLookCard(player);
    }

    private ActionscriptObject handleAddBet(PukerGame game, Player player, Map<String, String> params) {
        int bet = getIntParam(params, "cb", 0);
        return game.playerAddBet(player, bet);
    }

    private ActionscriptObject handleFollowBet(PukerGame game, Player player, Map<String, String> params) {
        int bet = getIntParam(params, "cb", 0);
        return game.playerFollowBet(player, bet);
    }

    private ActionscriptObject handleDropCard(PukerGame game, Player player) {
        return game.playerDropCard(player);
    }

    private ActionscriptObject handleAllIn(PukerGame game, Player player, Map<String, String> params) {
        int bet = getIntParam(params, "cb", 0);
        return game.playerAllIn(player, bet);
    }

    private ActionscriptObject handleSitDown(GameRoom room, Player user, Map<String, String> params) {
        int seatId = getIntParam(params, "sid", -1);
        int cb = getIntParam(params, "cb", 0);
        
        if (seatId < 0 || seatId > 8) {
            throw new IllegalArgumentException("InvalidSeatId");
        }
        
        return room.playerSitDown(seatId, user, cb);
    }

    private ActionscriptObject handleStandUp(GameRoom room, Player user, PukerGame game) {
        ActionscriptObject result = room.playerStandUp(user.getUid(), false);
        
        if (game != null && room.isGame() && game.isGameOverWhenDropCard()) {
            game.gameOverHandle();
        }
        
        return result;
    }

    private ActionscriptObject handleLeave(GameRoom room, Player user, PukerGame game, Player player) {
        room.playerLeave(user);
        
        if (game != null) {
            return game.playerLeave(player);
        }
        
        return new ActionscriptObject();
    }

    private ActionscriptObject handleFlushAchievements() {
        // TODO: Implement achievements flushing logic
        return new ActionscriptObject();
    }

    // Utility methods

    private Player validateUserAndRoom(String uid, String roomIdStr) {
        try {
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

    private int getIntParam(Map<String, String> params, String key, int defaultValue) {
        try {
            if (params != null && params.containsKey(key)) {
                return Integer.parseInt(params.get(key));
            }
        } catch (NumberFormatException e) {
            // Return default value if parsing fails
        }
        return defaultValue;
    }

    /**
     * Convert ActionscriptObject to XML string (for backward compatibility)
     */
    private String actionscriptObjectToXml(ActionscriptObject obj) {
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        byte[] xmlBytes = SFSObjectSerializer.obj2xml(obj, 0, "", sb);
        return new String(xmlBytes);
    }

    /**
     * Get current game state for a room
     */
    @GetMapping("/{roomId}/state")
    public ResponseEntity<ApiResponse<?>> getGameState(@PathVariable String roomId) {
        try {
            GameRoom room = this.roomService.getRoomByName(roomId);
            if (room == null) {
                return errorResponse("RoomNotFound");
            }
            
            PukerGame game = room.getPokerGame();
            if (game == null) {
                return errorResponse("NoActiveGameInRoom");
            }
            
            // Use the new GameStateResponse entity instead of ActionscriptObject
            GameStateResponse gameState = new GameStateResponse(room, game);
            
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
            @PathVariable String roomId,
            @PathVariable String uid) {
        
        try {
            Player user = getCurrentUser(uid);
            if (user == null) {
                return errorResponse("UserNotFound");
            }
            
            Room room = UserModule.getInstance().getRoomByName(roomId);
            if (room == null) {
                return errorResponse("RoomNotFound");
            }
            
            PukerGame game = room.getPokerGame();
            if (game == null) {
                return errorResponse("NoActiveGameInRoom");
            }
            
            Player player = game.findPlayerByUser(user);
            if (player == null) {
                return errorResponse("PlayerNotInGame");
            }
            
            ActionscriptObject playerStatus = new ActionscriptObject();
            playerStatus.put("playerId", player.getUid());
            playerStatus.put("seatId", player.getSeatId());
            playerStatus.put("chips", player.getChips());
            playerStatus.put("currentBet", player.getCurrentBet());
            playerStatus.put("hasLooked", player.isLooked());
            playerStatus.put("isActive", player.isActive());
            playerStatus.put("isAllIn", player.isAllIn());
            
            return successResponse(playerStatus);
        } catch (Exception e) {
            return errorResponse("FailedToGetPlayerStatus: " + e.getMessage());
        }
    }
}