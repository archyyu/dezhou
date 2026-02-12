package com.archy.texasholder.service;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.archy.texasholder.entity.Player;
import com.archy.texasholder.entity.User;

import jakarta.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PlayerService {

    private static final Logger log = LoggerFactory.getLogger(PlayerService.class);

    @Resource
    private UserService userService;

    public User NewUserInfoFromDb(String uid) {
        try {
            return userService.getUserById(Integer.parseInt(uid));
        } catch (Exception e) {
            return null;
        }
    }

    public boolean ifRegistered(String userName, String uid) {
        if (uid == null || uid.isEmpty()) {
            return userService.getUserByAccount(userName) != null;
        }
        try {
            return userService.getUserById(Integer.parseInt(uid)) != null;
        } catch (Exception e) {
            return false;
        }
    }

    public String Register(String userName, String password, String email, String gendar, String birthday, String userId,
            String key) {
        if (userName == null || userName.isEmpty()) {
            return "userNameIsRequired";
        }
        if (password == null || password.isEmpty()) {
            return "userPassWordIsRequired";
        }
        if (ifRegistered(userName, "")) {
            return "NameIsRepeated";
        }

        User user = new User();
        user.setAccount(userName);
        user.setPassWord(password);
        user.setEmail(email);
        user.setGendar(gendar);
        user.setBirthday(birthday);

        try {
            int inserted = userService.registerUser(user);
            return inserted > 0 ? "registerOk" : "MysqlError";
        } catch (Exception e) {
            log.error("Register failed", e);
            return "MysqlError";
        }
    }

    public HashMap<String, String> AutoRegister(String userid, String key) {
        HashMap<String, String> userInfoMap = new HashMap<>();

        if (userid != null && !"-1".equals(userid)) {
            return userInfoMap;
        }

        String password = randomPassword(6);
        String email = "";
        String gendar = "-1";
        String birthday = "-1";

        String userName = createAutoUsername();

        User insertUser = new User();
        insertUser.setAccount(userName);
        insertUser.setPassWord(password);
        insertUser.setEmail(email);
        insertUser.setGendar(gendar);
        insertUser.setBirthday(birthday);
        insertUser.setMobile(userid);

        try {
            int inserted = userService.registerUser(insertUser);
            if (inserted > 0) {
                User created = userService.getUserByAccount(userName);
                if (created != null) {
                    userInfoMap.put("name", userName);
                    userInfoMap.put("uid", String.valueOf(created.getUid()));
                    userInfoMap.put("password", password);
                }
            }
        } catch (Exception ex) {
            log.error("AutoRegister failed", ex);
        }

        return userInfoMap;
    }

    public int CreateUserId() {
        int maxuid = 10001;
        try {
            User user = userService.getUserByAccount("C10001");
            if (user == null) {
                maxuid = 10001;
            }
        } catch (Exception e) {
            maxuid = 10001;
        }
        return maxuid;
    }

    public boolean isvalidEmail(String email, String[] userinfo) {
        if (email == null || email.isEmpty() || userinfo == null || userinfo.length < 2) {
            return false;
        }

        User user = userService.getUserByAccount(email);
        if (user == null) {
            user = userService.getUserByAccount(safeDecode(email));
        }
        if (user == null) {
            return false;
        }

        userinfo[0] = String.valueOf(user.getUid());
        userinfo[1] = user.getAccount();
        return true;
    }

    public String resetPasswd(User uinfo) {
        String resetpasswd = "a" + randomPassword(6);
        uinfo.setPassWord(resetpasswd);
        userService.registerUser(uinfo);
        return resetpasswd;
    }

    public static String getDiamondListStr(int[][] diamondList) {
        String str = "";

        for (int i = 0; i < diamondList.length; i++) {
            for (int j = 0; j < diamondList[i].length; j++) {
                if (i < 3 || j == 0) {
                    str += diamondList[i][j] + "@";
                } else {
                    str += diamondList[i][j] + "";
                }
            }
        }

        if (str.isEmpty()) {
            str = "-1";
        }
        return str;
    }

    public static String setDefaultUserKeyWithNullUserId(String uid) {
        return ("id" + uid + "Ts" + System.currentTimeMillis() + "EmulaterPlayerSetSession").substring(0, 32);
    }

    public static String setNowStandardTimeString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date());
    }

    public Player selectPlayerById(Integer uid) {
        User user = userService.getUserById(uid);
        return user == null ? null : new Player(user);
    }

    public Player selectPlayerByAccount(String account) {
        User user = userService.getUserByAccount(account);
        return user == null ? null : new Player(user);
    }

    public boolean savePlayer(Player player) {
        if (player == null) {
            return false;
        }
        return userService.registerUser(player) > 0;
    }

    private String randomPassword(int len) {
        String[] passwordList = { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n",
                "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z" };
        Random random = new Random();
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < len; i++) {
            int suffix = Math.abs(random.nextInt() % passwordList.length);
            password.append(passwordList[suffix]);
        }
        return password.toString();
    }

    private String createAutoUsername() {
        String userName = "C" + System.currentTimeMillis();
        if (userService.getUserByAccount(userName) != null) {
            userName = "A" + System.nanoTime();
        }
        return userName;
    }

    private String safeDecode(String value) {
        try {
            return URLDecoder.decode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return value;
        }
    }
}
