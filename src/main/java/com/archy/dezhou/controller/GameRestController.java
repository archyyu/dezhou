package com.archy.dezhou.controller;

import com.alibaba.fastjson.JSON;
import com.archy.dezhou.entity.RequestDto;
import com.archy.dezhou.entity.ResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/game")
public class GameRestController {

    @Autowired
    private GameController gameController;

    @PostMapping("/process")
    public ResponseEntity<ResponseDto> processRequest(@RequestBody RequestDto requestDto) {
        ResponseDto response = new ResponseDto();
        
        try {
            // Process the request using the existing GameController logic
            // We'll need to adapt this to work with Spring Boot's response handling
            gameController.process(requestDto, response);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setSuccess(false);
            response.setErrorMessage("Error processing request: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping("/{cmd}")
    public ResponseEntity<String> handleCommand(
            @PathVariable String cmd,
            @RequestParam(required = false) String params,
            @RequestBody(required = false) String body) {
        
        // This handles the legacy command-based interface
        // We'll need to adapt the BackletKit logic here
        try {
            String result = processLegacyCommand(cmd, params, body);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    private String processLegacyCommand(String cmd, String params, String body) {
        // TODO: Implement legacy command processing using BackletKit
        // This is a placeholder for the legacy command processing
        return "Legacy command processing for: " + cmd;
    }
}