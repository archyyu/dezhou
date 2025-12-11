package com.archy.dezhou.controller.api;

import com.archy.dezhou.container.ActionscriptObject;
import com.archy.dezhou.entity.ApiResponse;
import com.archy.dezhou.entity.User;
import com.archy.dezhou.global.ConstList;
import com.archy.dezhou.service.PlayerService;
import com.archy.dezhou.service.RoomService;
import com.archy.dezhou.service.UserService;

import jakarta.annotation.Resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

/**
 * Script API Controller - Replaces ScriptNameBacklet
 * Handles mobile device registration, auto-login, and script-related functionality
 */
@RestController
@RequestMapping("/api/v1/script")
public class ScriptApiController extends BaseApiController {


    @Resource
    private PlayerService playerService;

    @Resource
    private UserService userService;

    @Resource
    private RoomService roomService;

    /**
     * Mobile device registration and auto-login endpoint
     * Replaces the legacy mobile device handling functionality
     */
    @PostMapping("/mobile")
    public ResponseEntity<ApiResponse<?>> handleMobileDevice(
            @RequestParam String ismobile,
            @RequestParam(required = false) String userid,
            @RequestParam(required = false) String mobile,
            @RequestParam(required = false) String key,
            @RequestParam(required = false, defaultValue = "no") String resetPassword) {
        
        try {
            if ("yes".equals(ismobile)) {
                return handleMobileRegistration(userid, mobile, key, resetPassword);
            } else if (userid != null) {
                return handleUserIdRequest(userid, key);
            } else {
                return handleDefaultScriptRequest();
            }
        } catch (Exception e) {
            return errorResponse("MobileScriptProcessingFailed: " + e.getMessage());
        }
    }

    /**
     * Handle mobile device registration and auto-login
     */
    private ResponseEntity<ApiResponse<?>> handleMobileRegistration(
            String userid, String mobile, String key, String resetPassword) {
        
        try {
            String uid = userid;
            User user = null;
            
            // Check if user is already registered
            if (!uid.equals("-1")) {
                user = this.userService.getUserByUserId(Integer.parseInt(uid));
            }
            
            log.info("Mobile registration - userid=" + userid + ", mobile=" + mobile + 
                    ", key=" + (key != null ? "[REDACTED]" : "null") + ", uid=" + uid);
            
            // New user registration
            if (user == null && userid != null && userid.length() > 4 && 
                key != null && key.length() > 25 && uid.equals("-1")) {
                
                HashMap<String, String> userinfoList = PlayerService.AutoRegister(userid, key);
                
                if (userinfoList != null && userinfoList.get("name") != null && 
                    userinfoList.get("password") != null) {
                    
                    // Auto-register and login
                    byte[] loginResponse = PlayerService.UserLogin(
                            userinfoList.get("name"), 
                            userinfoList.get("password"), 
                            true, userid, key, 1, false);
                    
                    String responseString = new String(loginResponse);
                    return successResponse(responseString);
                } else {
                    return errorResponse("AutoRigesterFailed");
                }
            }
            // Existing user not logged in
            else if (user == null && userid != null && userid.length() > 4 && 
                     key != null && key.length() > 25 && !uid.equals("-1")) {
                
                boolean resetPwd = "yes".equals(resetPassword);
                byte[] loginResponse = PlayerService.UserLogin("", "", false, userid, key, 1, resetPwd);
                
                String responseString = new String(loginResponse);
                return successResponse(responseString);
            }
            // Existing user already logged in
            else if (user != null && key != null && key.length() > 25 && 
                     userid != null && userid.length() > 0) {
                
                log.info("User already online: " + userid);

                User uinfo = this.userService.getUserByUserId(Integer.parseInt(uid));
                ActionscriptObject response = PlayerService.getUinfo(uinfo, true);
                response.put("Ver", ConstList.gameVersion);
                
                if ("yes".equals(resetPassword)) {
                    response.put("password", PlayerService.resetPasswd(uinfo));
                }
                
                // Add diamond and VIP information
                ActionscriptObject djObjList = PlayerService.getUsedDj(uinfo, user);
                int[][] diamondList = PlayerService.getDiamondList(djObjList, uinfo);
                response.put("diamond", PlayerService.getDiamondListStr(diamondList));
                response.put("vip", PlayerService.getVipid(djObjList, uinfo) + "");
                
                // Convert to XML for backward compatibility
                String xmlResponse = actionscriptObjectToXml(response);
                return successResponse(xmlResponse);
            } else {
                return errorResponse("EmulatorCantAutoLogin");
            }
        } catch (Exception e) {
            return errorResponse("MobileRegistrationFailed: " + e.getMessage());
        }
    }

    /**
     * Handle user ID request
     */
    private ResponseEntity<ApiResponse<?>> handleUserIdRequest(String userid, String key) {
        // This seems to be a simple response for user ID validation
        return successResponse("0");
    }

    /**
     * Handle default script request
     */
    private ResponseEntity<ApiResponse<?>> handleDefaultScriptRequest() {
        // Default response for script requests without specific parameters
        return successResponse("0");
    }

    /**
     * Get game version information
     */
    @GetMapping("/version")
    public ResponseEntity<ApiResponse<?>> getGameVersion() {
        try {
            String versionInfo = ConstList.gameVersion != null ? 
                                ConstList.gameVersion : "unknown";
            
            return successResponse(versionInfo);
        } catch (Exception e) {
            return errorResponse("FailedToGetVersion: " + e.getMessage());
        }
    }

    /**
     * Get user script information (mobile device sync)
     */
    @GetMapping("/users/{uid}/sync")
    public ResponseEntity<ApiResponse<?>> getUserScriptSyncInfo(@PathVariable String uid) {
        try {
            User user = this.userService.getUserById(Integer.parseInt(uid));
            if (user == null) {
                return errorResponse("UserNotFound");
            }
            
            // Get user info with script-related data
            ActionscriptObject response = PlayerService.getUinfo(user, true);
            response.put("Ver", ConstList.gameVersion);
            
            // Add diamond and VIP information
            ActionscriptObject djObjList = PlayerService.getUsedDj(user, user);
            int[][] diamondList = PlayerService.getDiamondList(djObjList, user);
            response.put("diamond", PlayerService.getDiamondListStr(diamondList));
            response.put("vip", PlayerService.getVipid(djObjList, user) + "");
            
            return successResponse(response);
        } catch (Exception e) {
            return errorResponse("FailedToGetScriptSyncInfo: " + e.getMessage());
        }
    }

    /**
     * Reset user password via mobile device
     */
    @PostMapping("/users/{uid}/password/reset")
    public ResponseEntity<ApiResponse<?>> resetUserPassword(
            @PathVariable String uid,
            @RequestParam String userid,
            @RequestParam String key) {
        
        try {
            User user = this.userService.getUserById(Integer.parseInt(uid));
            if (user == null) {
                return errorResponse("UserNotFound");
            }
            
            String newPassword = PlayerService.resetPasswd(user);
            
            // Also perform login to get full user info
            byte[] loginResponse = PlayerService.UserLogin("", "", false, userid, key, 1, true);
            String responseString = new String(loginResponse);
            
            return successResponse(responseString);
        } catch (Exception e) {
            return errorResponse("PasswordResetFailed: " + e.getMessage());
        }
    }


    /**
     * Get device compatibility information
     */
    @GetMapping("/device/compatibility")
    public ResponseEntity<ApiResponse<?>> checkDeviceCompatibility(
            @RequestParam(required = false, defaultValue = "no") String ismobile) {
        
        try {
            if ("yes".equals(ismobile)) {
                return successResponse("Mobile device supported");
            } else {
                return successResponse("Desktop device supported");
            }
        } catch (Exception e) {
            return errorResponse("CompatibilityCheckFailed: " + e.getMessage());
        }
    }
}