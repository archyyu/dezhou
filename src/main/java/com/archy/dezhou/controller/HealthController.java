package com.archy.dezhou.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
public class HealthController {

    @GetMapping
    public String healthCheck() {
        return "Dezhou Poker Server is running!";
    }

    @GetMapping("/ready")
    public String readinessCheck() {
        return "Server is ready to accept requests!";
    }
}