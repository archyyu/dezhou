package com.archy.dezhou.controller;

import com.archy.dezhou.controller.base.Controller;
import com.archy.dezhou.entity.RequestDto;
import com.archy.dezhou.entity.ResponseDto;
import io.netty.handler.codec.http.FullHttpResponse;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;

@Component
public class GameController extends Controller {

    @Override
    public void access(RequestDto requestDto, FullHttpResponse response) {
        ResponseDto springResponse = new ResponseDto();
        
        // Process the request and set data in springResponse
        processRequest(requestDto, springResponse);
        
        // Convert Spring Boot response to Netty response for backward compatibility
        response.content().writeCharSequence(JSON.toJSONString(springResponse), Charset.defaultCharset());
    }

    /**
     * Spring Boot compatible method for processing requests
     */
    public void process(RequestDto requestDto, ResponseDto response) {
        processRequest(requestDto, response);
    }

    private void processRequest(RequestDto requestDto, ResponseDto response) {
        String function = requestDto.getFn();

        if("login".equals(function)){
            handleLogin(requestDto, response);
        }
        else if("".equals(function)) {
            // Empty function - handle appropriately
            response.setSuccess(false);
            response.setErrorMessage("Invalid function");
        }
        else if("".equals(function)){
            // Another empty function - handle appropriately
            response.setSuccess(false);
            response.setErrorMessage("Invalid function");
        }
        else{
            // Default handling for unknown functions
            response.setSuccess(false);
            response.setErrorMessage("Unknown function: " + function);
        }
    }

    private void handleLogin(RequestDto requestDto, ResponseDto response) {
        // TODO: Implement actual login logic
        // This is a placeholder for the login functionality
        response.setData("Login successful");
    }

    // Import JSON class
    private static final com.alibaba.fastjson.JSON JSON = com.alibaba.fastjson.JSON;
}
