package com.archy.dezhou.controller.api;

import com.archy.dezhou.container.JsonObjectWrapper;
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

import com.archy.dezhou.entity.response.RoomResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;



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

    // Get room list endpoint - replaces LIST command
    @GetMapping("/list")
    public ResponseEntity<ApiResponse<?>> getRoomList(
            @RequestParam(required = false, defaultValue = "rg") String rt,
            @RequestParam(required = false, defaultValue = "-1") String bb,
            @RequestParam(required = false, defaultValue = "-1") String sb) {
        
        try {
            List<RoomResponse.RoomListItem> roomList = getRoomListFromMemory(rt, bb, sb);
            log.info("room/list size:" + roomList.size());
            return successResponse(roomList);
        } catch (Exception e) {
            return errorResponse("RoomListRetrievalFailed: " + e.getMessage());
        }
    }

    @GetMapping("/roomTypeList")
    public ResponseEntity<ApiResponse<?>> getTypeList() {
        List<RoomDB> dbList = this.roomService.getRoomTypeList();
        return successResponse(dbList);
    }

    @GetMapping("/{roomTypeId}/list")
    public ResponseEntity<ApiResponse<?>> getMethodName(@PathVariable Integer roomTypeId) {

        List<PukerGame> list = this.roomService.getRoomListByTypeId(roomTypeId);
        // List<JsonObjectWrapper> result = list.stream().map(item -> item.toAsObj()).collect(Collectors.toList());
        return successResponse(list);
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

    // Helper method to generate room list using modern entities
    private List<RoomResponse.RoomListItem> getRoomListFromMemory(String roomtype, String bb, String sb) {
        List<PukerGame> roomlist = this.roomService.getRoomList();
        List<RoomResponse.RoomListItem> result = new ArrayList<>();
        
        if (StringUtils.isNumeric(bb) && StringUtils.isNumeric(sb)) {
            int minBet = Integer.parseInt(sb);
            int maxBet = Integer.parseInt(bb);

            roomlist.forEach(room -> {
                if (room.getBbet() >= minBet && room.getBbet() <= maxBet) {
                    result.add(new RoomResponse.RoomListItem(room));
                }
            });
        } else {
            roomlist.forEach(room -> result.add(new RoomResponse.RoomListItem(room)));
            return result;
        }
        
        return result;
    }

    // Get room details endpoint
    @GetMapping("/{roomId}")
    public ResponseEntity<ApiResponse<?>> getRoomDetails(@PathVariable String roomId) {
        PukerGame room = this.roomService.getRoom(Integer.parseInt(roomId));
        if (room != null) {
            RoomResponse response = new RoomResponse(room);
            return successResponse(response);
        } else {
            return errorResponse("RoomNotFound");
        }
    }
    
}