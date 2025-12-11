package com.archy.dezhou.controller.api;

import com.archy.dezhou.container.ActionscriptObject;
import com.archy.dezhou.entity.ApiResponse;
import com.archy.dezhou.entity.Player;
import com.archy.dezhou.entity.User;
import com.archy.dezhou.entity.response.UserResponse;
import com.archy.dezhou.service.PlayerService;
import com.archy.dezhou.service.RoomService;
import com.archy.dezhou.service.UserService;

import jakarta.annotation.Resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * User API Controller - Replaces PlayerManageBacklet
 * Handles user authentication, registration, and profile management
 */
@RestController
@RequestMapping("/api/v1/user")
public class UserApiController extends BaseApiController {

    @Resource
    private PlayerService playerService;

    @Resource
    private RoomService roomService;

    @Resource
    private UserService userService;

    // User login endpoint - replaces USERLOGIN command
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> userLogin(
            @RequestParam String name,
            @RequestParam String password,
            @RequestParam(required = false) String userid,
            @RequestParam(required = false) String key) {
        
        if (!validateRequiredParams(name, password)) {
            return errorResponse("NameOrPasswordIdNull");
        }

        try {
            byte[] responseBytes = PlayerService.UserLogin(name, password, false, 
                    userid != null ? userid : "", 
                    key != null ? key : "", 
                    0, false);
            
            // Convert legacy byte[] response to modern format
            String responseString = new String(responseBytes);
            return successResponse(responseString);
        } catch (Exception e) {
            return errorResponse("LoginFailed: " + e.getMessage());
        }
    }

    // User registration endpoint - replaces REGISTER command
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<?>> registerUser(
            @RequestParam(required = false) String auto,
            @RequestParam(required = false) String userid,
            @RequestParam(required = false) String key,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String password,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String gendar,
            @RequestParam(required = false) String birthday) {
        
        if ("yes".equals(auto)) {
            // Auto registration
            return handleAutoRegistration(userid, key);
        } else {
            // Manual registration
            if (!validateRequiredParams(name, password)) {
                return errorResponse("NameOrPasswordRequired");
            }
            
            try {
                byte[] responseBytes = PlayerService.Register(name, password, email, gendar, birthday, 
                        userid != null ? userid : "", 
                        key != null ? key : "");
                
                String responseString = new String(responseBytes);
                return successResponse(responseString);
            } catch (Exception e) {
                return errorResponse("RegistrationFailed: " + e.getMessage());
            }
        }
    }

    private ResponseEntity<ApiResponse<?>> handleAutoRegistration(String userid, String key) {
        if (userid != null && !userid.isEmpty() && !userid.equals("-1")) {
            return errorResponse("UserHasRegistered");
        }

        try {
            HashMap<String, String> userinfoList = PlayerService.AutoRegister(
                    userid != null ? userid : "", 
                    key != null ? key : "");
            
            if (userinfoList != null && userinfoList.get("name") != null && userinfoList.get("password") != null) {
                byte[] loginResponse = PlayerService.UserLogin(
                        userinfoList.get("name"), 
                        userinfoList.get("password"), 
                        true, 
                        userid != null ? userid : "", 
                        key != null ? key : "", 
                        0, false);
                
                String responseString = new String(loginResponse);
                return successResponse(responseString);
            } else {
                return errorResponse("AutoRigesterFailed");
            }
        } catch (Exception e) {
            return errorResponse("AutoRegistrationFailed: " + e.getMessage());
        }
    }

    // User profile update endpoint - replaces REGISTERUPDATE command
    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<?>> updateUserProfile(
            @RequestParam String uid,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String birthday,
            @RequestParam(required = false) String gendar,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String address,
            @RequestParam(required = false) String mobile,
            @RequestParam(required = false) String np,  // new password
            @RequestParam(required = false) String op) { // old password
        
        Player currentUser = getCurrentUser(uid);
        if (currentUser == null) {
            return errorResponse("UserNotLogined");
        }

        try {
            User uinfo = PlayerService.selectPlayerById(Integer.parseInt(uid));
            
            // Update user information
            if (email != null && !email.isEmpty()) uinfo.setEmail(email);
            if (birthday != null && !birthday.isEmpty()) uinfo.setBirthday(birthday);
            if (gendar != null && !gendar.isEmpty()) uinfo.setGendar(gendar);
            if (name != null && !name.isEmpty()) {
                if (PlayerService.ifRegistered(name, "")) {
                    return errorResponse("userNameIsRepeat");
                }
                uinfo.setAccount(name);
            }
            if (address != null && !address.isEmpty()) uinfo.setAddress(address);
            if (mobile != null && !mobile.isEmpty()) uinfo.setMobile(mobile);

            // Handle password update
            ActionscriptObject PassWordInfo = new ActionscriptObject();
            if (np != null && !np.isEmpty() && op != null && !op.isEmpty()) {
                PassWordInfo.put("op", op);
                PassWordInfo.put("np", np);
            }

            ActionscriptObject updateStatus = PlayerService.UpdateUserInfo(uinfo, PassWordInfo);
            ActionscriptObject asResponse = PlayerService.getUinfo(uinfo, true);

            if (updateStatus == null) {
                asResponse.put("status", "registerUpdatefail");
                asResponse.put("code", "0");
                asResponse.put("cnt", "用户资料修改失败！");
            } else {
                asResponse.put("status", updateStatus.get("status"));
                asResponse.put("code", updateStatus.get("code"));
                asResponse.put("cnt", updateStatus.get("cnt"));
            }

            return successResponse(asResponse);
        } catch (Exception e) {
            return errorResponse("ProfileUpdateFailed: " + e.getMessage());
        }
    }

    // Password update endpoint - replaces PASSWORDUPDATE command
    @PutMapping("/password")
    public ResponseEntity<ApiResponse<?>> updatePassword(
            @RequestParam String password,
            @RequestParam String email) {
        
        if (!validateRequiredParams(password, email)) {
            return errorResponse("parmsInInvalid");
        }

        try {
            String[] userID = {"uid", "name"};
            if (!PlayerService.isvalidEmail(email, userID)) {
                return errorResponse("EmailIsinValid");
            }
            
            // TODO: Implement actual password update logic
            return successResponse("Dealing");
        } catch (Exception e) {
            return errorResponse("PasswordUpdateFailed: " + e.getMessage());
        }
    }

    // User info endpoint - replaces UINFO command
    @GetMapping("/info")
    public ResponseEntity<ApiResponse<?>> getUserInfo(
            @RequestParam String uid,
            @RequestParam String cuid) {
        
        if (!validateRequiredParams(uid, cuid)) {
            return errorResponse("ParmsIsInvalid");
        }

        try {
            Player uinfo = userService.getUserByUserId(Integer.parseInt(uid));
            Player cuinfo = userService.getUserByUserId(Integer.parseInt(cuid));

            if (uinfo == null) {
                return errorResponse("YouAreNotLogined!");
            }
            if (cuinfo == null) {
                return errorResponse("HeIsNotLogined!!");
            }
            
            // Use the new UserResponse entity instead of ActionscriptObject
            boolean includeSensitiveData = uid.equals(cuid);
            UserResponse response = new UserResponse(includeSensitiveData ? uinfo : cuinfo, includeSensitiveData);
            
            return successResponse(response);
        } catch (Exception e) {
            return errorResponse("UserInfoRetrievalFailed: " + e.getMessage());
        }
    }

    // User logout endpoint - replaces LOGOUT command
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<?>> userLogout(@RequestParam String uid) {
        Player user = this.userService.getUserByUserId(Integer.parseInt(uid));
        if (user != null) {
            return successResponse("loginoutOk");
        } else {
            return successResponse("HasLoginOutBefore");
        }
    }

    // User achievements endpoint - replaces RUSHACH command
    @GetMapping("/achievements")
    public ResponseEntity<ApiResponse<?>> getUserAchievements(
            @RequestParam String uid,
            @RequestParam(required = false) String rn) {
        
        // TODO: Implement achievements logic
        return successResponse("Achievements endpoint");
    }
}