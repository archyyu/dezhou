package com.archy.dezhou.controller.api;

import com.archy.dezhou.container.JsonObjectWrapper;
import com.archy.dezhou.entity.ApiResponse;
import com.archy.dezhou.entity.Player;
import com.archy.dezhou.service.RoomService;
import com.archy.dezhou.service.UserService;

import jakarta.annotation.Resource;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Message API Controller - Replaces PubMsgBacklet
 * Handles public messaging and chat functionality within rooms
 */
@RestController
@RequestMapping("/api/v1/messages")
public class MessageApiController extends BaseApiController {

    @Resource
    private UserService userService;

    @Resource
    private RoomService roomService;

    @GetMapping("/user")
    public ResponseEntity<ApiResponse<?>> getMethodName() {

        Player player = this.getAuthentificatedPlayer();

        player = this.userService.getUserByUserId(player.getUid());

        List<JsonObjectWrapper> list = player.retrieveMsg();
        return successResponse(list);
    }
    


    
}