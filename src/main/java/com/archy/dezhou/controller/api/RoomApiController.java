package com.archy.dezhou.controller.api;

import com.archy.dezhou.entity.ApiResponse;
import com.archy.dezhou.entity.Player;
import com.archy.dezhou.entity.room.GameRoom;
import com.archy.dezhou.service.PlayerService;
import com.archy.dezhou.service.RoomService;
import com.archy.dezhou.service.UserService;

import jakarta.annotation.Resource;

import com.archy.dezhou.entity.response.RoomResponse;
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

    // Get room list endpoint - replaces LIST command
    @GetMapping("/list")
    public ResponseEntity<ApiResponse<?>> getRoomList(
            @RequestParam(required = false, defaultValue = "rg") String rt,
            @RequestParam(required = false, defaultValue = "-1") String bb,
            @RequestParam(required = false, defaultValue = "-1") String sb) {
        
        try {
            List<RoomResponse.RoomListItem> roomList = getRoomListFromMemory(rt, bb, sb);
            return successResponse(roomList);
        } catch (Exception e) {
            return errorResponse("RoomListRetrievalFailed: " + e.getMessage());
        }
    }

    // Join room endpoint - replaces JOIN command
    @PostMapping("/{roomName}/join")
    public ResponseEntity<ApiResponse<?>> joinRoom(
            @PathVariable String roomName,
            @RequestParam String uid) {
        
        try {
            GameRoom room = this.roomService.getRoomByName(roomName);
            Player user = this.userService.getUserByUserId(Integer.parseInt(uid));
            
            if (room == null || user == null) {
                return errorResponse("UserNotLogined");
            }
            
            // Leave old room if user is already in one
            GameRoom oldRoom = this.roomService.getRoom(user.getRoomId());
            if (oldRoom != null) {
                oldRoom.playerLeave(user);
            }
            
            // Check if user is already in the target room
            if (room.isPlayerInRoom(user)) {
                return errorResponse("haveBeenEntered");
            }
            
            // Join the new room
            int ret = room.userJoin(user);
            user.setRoomId(room.getRoomId());
            
            if (ret == 0) {
                return successResponse("UserEnterRoomOk");
            } else {
                return successResponse("haveBeenEntered");
            }
        } catch (Exception e) {
            return errorResponse("RoomJoinFailed: " + e.getMessage());
        }
    }

    // Leave room endpoint - replaces LEAVE command
    @PostMapping("/{roomName}/leave")
    public ResponseEntity<ApiResponse<?>> leaveRoom(
            @PathVariable String roomName,
            @RequestParam String uid) {
        
        try {
            Player user = this.userService.getUserByUserId(Integer.parseInt(uid));
            if (user == null) {
                return errorResponse("UserNotLogined");
            }

            GameRoom room = this.roomService.getRoomByName(roomName);
            if (room == null) {
                return errorResponse("YourParmsIsInValid");
            }
            
            int lea = room.playerLeave(user);
            
            if (lea == 0) {
                return successResponse("UserLeaveRoomOk");
            } else {
                return successResponse("haveBeenLeaved");
            }
        } catch (Exception e) {
            return errorResponse("RoomLeaveFailed: " + e.getMessage());
        }
    }

    // Helper method to generate room list using modern entities
    private List<RoomResponse.RoomListItem> getRoomListFromMemory(String roomtype, String bb, String sb) {
        List<GameRoom> roomlist = this.roomService.getRoomList();
        List<RoomResponse.RoomListItem> result = new ArrayList<>();
        
        try {
            int minBet = Integer.parseInt(sb);
            int maxBet = Integer.parseInt(bb);

            for (GameRoom room : roomlist) {
                if (room.getBbet() >= minBet && room.getBbet() <= maxBet) {
                    result.add(new RoomResponse.RoomListItem(room));
                }
            }
        } catch (NumberFormatException e) {
            // If parsing fails, return all rooms
            for (GameRoom room : roomlist) {
                result.add(new RoomResponse.RoomListItem(room));
            }
        }
        
        return result;
    }

    // Get room details endpoint
    @GetMapping("/{roomName}")
    public ResponseEntity<ApiResponse<?>> getRoomDetails(@PathVariable String roomName) {
        GameRoom room = this.roomService.getRoomByName(roomName);
        if (room != null) {
            RoomResponse response = new RoomResponse(room);
            return successResponse(response);
        } else {
            return errorResponse("RoomNotFound");
        }
    }

    // Get user's current room endpoint
    @GetMapping("/current")
    public ResponseEntity<ApiResponse<?>> getCurrentRoom(@RequestParam String uid) {
        Player user = this.userService.getUserByUserId(Integer.parseInt(uid));
        if (user != null) {
            GameRoom room = this.roomService.getRoom(user.getRoomId());
            if (room != null) {
                return successResponse(room);
            } else {
                return successResponse("UserNotInAnyRoom");
            }
        } else {
            return errorResponse("UserNotLogined");
        }
    }
}