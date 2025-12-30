package com.archy.dezhou.controller.api;


import com.archy.dezhou.beans.GameState;
import com.archy.dezhou.entity.ApiResponse;
import com.archy.dezhou.entity.Player;
import com.archy.dezhou.entity.RoomDB;
import com.archy.dezhou.entity.room.GameRoom;
import com.archy.dezhou.entity.room.PukerGame;
import com.archy.dezhou.security.JwtTokenProvider;
import com.archy.dezhou.service.PlayerService;
import com.archy.dezhou.service.RoomService;
import com.archy.dezhou.service.UserService;

import jakarta.annotation.Resource;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;



/**
 * Room API Controller - Replaces RoomListBacklet
 * Handles room listing, joining, and leaving functionality
 */
@RestController
@RequestMapping("/api/v1/room")
public class RoomApiController extends BaseApiController {

    @Resource
    private RoomService roomService;

    @Resource
    private PlayerService playerService;

    @Resource
    private UserService userService;

    @Resource
    private JwtTokenProvider jwtTokenProvider;

    private Logger log = LoggerFactory.getLogger(getClass());

    @GetMapping("/roomTypeList")
    public ResponseEntity<ApiResponse<?>> getTypeList() {
        try {
            List<RoomDB> dbList = this.roomService.getRoomTypeList();
            if (dbList != null) {
                return successResponse(dbList);
            } else {
                return successResponse(new ArrayList<>()); // Return empty list if null
            }
        } catch (Exception e) {
            log.error("Error getting room type list", e);
            return ResponseEntity.status(500).body(ApiResponse.error("FailedToGetRoomTypeList"));
        }
    }

    @GetMapping("/{roomTypeId}/list")
    public ResponseEntity<ApiResponse<?>> getMethodName(@PathVariable Integer roomTypeId) {
        try {
            List<PukerGame> list = this.roomService.getRoomListByTypeId(roomTypeId);
            if (list != null) {
                return successResponse(list);
            } else {
                return successResponse(new ArrayList<>()); // Return empty list if null
            }
        } catch (Exception e) {
            log.error("Error getting room list by type", e);
            return ResponseEntity.status(500).body(ApiResponse.error("FailedToGetRoomListByType"));
        }
    }
    

    // user create a room
    @PostMapping("/create/{roomTypeId}/{roomName}")
    public ResponseEntity<ApiResponse<?>> postMethodName(@PathVariable String roomTypeId, @PathVariable String roomName) {

        Player player = getAuthentificatedPlayer();

        GameRoom gameRoom = this.roomService.createGameRoom(player.getUid() + "", player.getAccount(), Integer.parseInt(roomTypeId), roomName);
        return successResponse(gameRoom);

    }
    

    // Join room endpoint - replaces JOIN command
    @PostMapping("/{roomId}/join")
    public ResponseEntity<ApiResponse<?>> joinRoom(
            @PathVariable String roomId) {
        
        try {
            // Get user from JWT authentication or fallback to legacy
            Player user = this.getAuthentificatedPlayer();
            
            if (user == null) {
                return errorResponse("UserNotLogined");
            }

            Player player = this.userService.getUserByUserId(user.getUid());
            
            GameRoom room = this.roomService.getRoom(Integer.parseInt(roomId));
            
            if (room == null) {
                return errorResponse("RoomNotFound");
            }
            
            // Leave old room if user is already in one
            PukerGame oldRoom = this.roomService.getRoom(user.getRoomid());
            if (oldRoom != null) {
                oldRoom.playerLeave(user);
            }
            
            // Check if user is already in the target room
            if (room.isPlayerInRoom(user)) {
                return errorResponse("haveBeenEntered");
            }
            
            // Join the new room
            int ret = room.userJoin(player);
            player.setRoomId(room.getRoomid());
            
            if (ret == 0) {
                return successResponse("UserEnterRoomOk");
            } else {
                return successResponse("haveBeenEntered");
            }
        } catch (Exception e) {
            log.error("RoomJoinFailed", e);
            return errorResponse("RoomJoinFailed: " + e.getMessage());
        }
    }

    // Leave room endpoint - replaces LEAVE command
    @PostMapping("/{roomName}/leave")
    public ResponseEntity<ApiResponse<?>> leaveRoom(
            @PathVariable String roomId,
            @RequestParam String uid) {
        
        try {
            Player user = this.userService.getUserByUserId(Integer.parseInt(uid));
            if (user == null) {
                return errorResponse("UserNotLogined");
            }

            PukerGame room = this.roomService.getRoom(Integer.parseInt(roomId));
            if (room == null) {
                return errorResponse("YourParmsIsInValid");
            }
            
            boolean lea = room.playerLeave(user);
            
            if (lea) {
                return successResponse("UserLeaveRoomOk");
            } else {
                return successResponse("haveBeenLeaved");
            }
        } catch (Exception e) {
            return errorResponse("RoomLeaveFailed: " + e.getMessage());
        }
    }

    
    // Get room details endpoint
    @GetMapping("/info/{roomId}")
    public ResponseEntity<ApiResponse<?>> getRoomDetails(@PathVariable String roomId) {
        try {
            PukerGame room = this.roomService.getRoom(Integer.parseInt(roomId));
            if (room != null) {
                GameState response = room.toGameState();
                return successResponse(response);
            } else {
                return errorResponse("roomNotFound");
                // return ResponseEntity.status(404).body(ApiResponse.error("RoomNotFound"));
            }
        } catch (NumberFormatException e) {
            return errorResponse("InvalidRoomId");
            // return ResponseEntity.status(400).body(ApiResponse.error("InvalidRoomId"));
        }
    }
    
}