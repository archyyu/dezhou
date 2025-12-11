package com.archy.dezhou.controller.api;

import com.archy.dezhou.entity.ApiResponse;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Legacy API Controller - Provides backward compatibility for legacy Backlet commands
 * This controller handles the legacy command-based interface and routes to the appropriate services
 */
@RestController
@RequestMapping("/api/legacy")
public class LegacyApiController extends BaseApiController {

    // Note: All Backlets have been migrated to Spring Boot controllers
    // This controller is kept for potential backward compatibility needs

    /**
     * Handle legacy Backlet commands
     * This endpoint provides backward compatibility for the original command-based interface
     */
    @PostMapping("/{cmd}")
    public ResponseEntity<ApiResponse<?>> handleLegacyCommand(
            @PathVariable String cmd,
            @RequestParam(required = false) String subCmd,
            @RequestBody(required = false) Map<String, String> params) {
        
        // All Backlets have been migrated to modern Spring Boot controllers
        // This endpoint is kept for potential backward compatibility
        return errorResponse("All legacy Backlets have been migrated to modern REST endpoints. " +
                           "Please use the new API endpoints instead.");
    }

    /**
     * Extract error message from legacy XML format
     */
    private String extractErrorFromXml(String xml) {
        int start = xml.indexOf("<error>") + 7;
        int end = xml.indexOf("</error>");
        if (start > 6 && end > start) {
            return xml.substring(start, end);
        }
        return "UnknownError";
    }

    /**
     * Extract info message from legacy XML format
     */
    private String extractInfoFromXml(String xml) {
        int start = xml.indexOf("<info>") + 6;
        int end = xml.indexOf("</info>");
        if (start > 5 && end > start) {
            return xml.substring(start, end);
        }
        return "UnknownInfo";
    }

    /**
     * Migration guide DTO
     */
    private static class MigrationGuide {
        private String message;
        private String migrationDate;
        private boolean newEndpointsAvailable;
        private String[] endpointMappings;

        // Getters and Setters
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public String getMigrationDate() { return migrationDate; }
        public void setMigrationDate(String migrationDate) { this.migrationDate = migrationDate; }
        public boolean isNewEndpointsAvailable() { return newEndpointsAvailable; }
        public void setNewEndpointsAvailable(boolean newEndpointsAvailable) { this.newEndpointsAvailable = newEndpointsAvailable; }
        public String[] getEndpointMappings() { return endpointMappings; }
        public void setEndpointMappings(String[] endpointMappings) { this.endpointMappings = endpointMappings; }
    }

    /**
     * Health check for legacy API
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<?>> legacyHealthCheck() {
        return successResponse("Legacy API endpoints have been migrated to modern REST controllers");
    }

    /**
     * List available legacy commands
     */
    @GetMapping("/commands")
    public ResponseEntity<ApiResponse<?>> listAvailableCommands() {
        // All Backlets have been migrated - return migration guide
        MigrationGuide guide = new MigrationGuide();
        guide.setMessage("All legacy Backlet commands have been migrated to modern REST endpoints.");
        guide.setMigrationDate("2024");
        guide.setNewEndpointsAvailable(true);
        
        // Provide mapping of old commands to new endpoints
        guide.setEndpointMappings(new String[] {
            "PlayerManageBacklet → /api/v1/user/*",
            "RoomListBacklet → /api/v1/room/*",
            "PukeLogicBacket → /api/v1/game/*",
            "PubMsgBacklet → /api/v1/messages/*",
            "ScriptNameBacklet → /api/v1/script/*"
        });
        
        return successResponse(guide);
    }
}