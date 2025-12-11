package com.archy.dezhou.entity.response;

import com.archy.dezhou.entity.User;
import com.archy.dezhou.entity.Player;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * User Response Entity
 * Replaces XML responses for user information
 */
@Data
@NoArgsConstructor
public class UserResponse {
    private int userId;
    private String username;
    private String email;
    private String mobile;
    private String gender;
    private String birthday;
    private String address;
    private String registrationDate;
    private String lastLogin;
    private int roomMoney;
    private int allMoney;
    private int experience;
    private int gold;
    private int level;
    private String status;
    private String vipLevel;
    private String avatarUrl;
    private UserStats stats;
    private UserAchievements achievements;
    private List<UserDiamond> diamonds;

    /**
     * Create UserResponse from User
     */
    public UserResponse(User user) {
        this.userId = user.getUid();
        this.username = user.getAccount();
        this.email = user.getEmail();
        this.mobile = user.getMobile();
        this.gender = user.getGendar();
        this.birthday = user.getBirthday();
        this.address = user.getAddress();
        this.roomMoney = user.getRoommoney();
        this.allMoney = user.getAllmoney();
        this.experience = user.getExprience();
        this.gold = user.getGold();
        this.level = user.getLevel();
        this.status = "active";
        this.vipLevel = "VIP1"; // Default, would be calculated
        this.avatarUrl = "/avatars/default.png"; // Default avatar
        
        // Initialize sub-objects
        this.stats = new UserStats(user);
        this.achievements = new UserAchievements();
        this.diamonds = List.of(); // Would be populated from database
    }

    /**
     * Create detailed UserResponse from Player
     */
    public UserResponse(Player player, boolean includeSensitiveData) {
        this.userId = player.getUid();
        this.username = player.getAccount();
        this.email = includeSensitiveData ? player.getEmail() : "*****@*****.***";
        this.mobile = includeSensitiveData ? player.getMobile() : "***********";
        this.gender = player.getGendar();
        this.birthday = player.getBirthday();
        this.address = player.getAddress();
        this.roomMoney = player.getRoommoney();
        this.allMoney = player.getAllmoney();
        this.experience = player.getExprience();
        this.gold = player.getGold();
        this.level = player.getLevel();
        this.status = player.isOnline() ? "online" : "offline";
        this.vipLevel = calculateVipLevel(player);
        this.avatarUrl = player.getAvatarUrl();
        
        // Initialize sub-objects
        this.stats = new UserStats(player);
        this.achievements = new UserAchievements();
        this.diamonds = List.of(); // Would be populated from database
    }

    private String calculateVipLevel(Player player) {
        int totalMoney = player.getRoommoney() + player.getAllmoney();
        if (totalMoney > 1000000) return "VIP5";
        if (totalMoney > 500000) return "VIP4";
        if (totalMoney > 100000) return "VIP3";
        if (totalMoney > 50000) return "VIP2";
        return "VIP1";
    }

    /**
     * User statistics
     */
    @Data
    @NoArgsConstructor
    public static class UserStats {
        private int totalGamesPlayed;
        private int gamesWon;
        private int gamesLost;
        private int handsPlayed;
        private int handsWon;
        private double winRate;
        private int biggestWin;
        private int biggestLoss;
        private int totalBets;
        private int totalWinnings;
        
        public UserStats(User user) {
            // These would be populated from actual statistics in the database
            this.totalGamesPlayed = 100;
            this.gamesWon = 45;
            this.gamesLost = 55;
            this.handsPlayed = 1000;
            this.handsWon = 450;
            this.winRate = 45.0;
            this.biggestWin = 5000;
            this.biggestLoss = -2000;
            this.totalBets = 50000;
            this.totalWinnings = 45000;
        }
        
        public UserStats(Player player) {
            // These would be populated from actual statistics in the database
            this.totalGamesPlayed = player.getTotalGames();
            this.gamesWon = player.getGamesWon();
            this.gamesLost = player.getGamesLost();
            this.handsPlayed = player.getHandsPlayed();
            this.handsWon = player.getHandsWon();
            this.winRate = calculateWinRate();
            this.biggestWin = player.getBiggestWin();
            this.biggestLoss = player.getBiggestLoss();
            this.totalBets = player.getTotalBets();
            this.totalWinnings = player.getTotalWinnings();
        }
        
        private double calculateWinRate() {
            if (handsPlayed == 0) return 0.0;
            return ((double) handsWon / handsPlayed) * 100;
        }
    }

    /**
     * User achievements
     */
    @Data
    public static class UserAchievements {
        private List<String> unlockedAchievements;
        private List<String> availableAchievements;
        private int achievementPoints;
        private Map<String, Boolean> achievementStatus;
        
        public UserAchievements() {
            // Initialize with default achievements
            this.unlockedAchievements = List.of("first_win", "welcome");
            this.availableAchievements = List.of("first_win", "welcome", "big_winner", "high_roller");
            this.achievementPoints = 50;
            this.achievementStatus = Map.of(
                "first_win", true,
                "welcome", true,
                "big_winner", false,
                "high_roller", false
            );
        }
    }

    /**
     * User diamonds/items
     */
    @Data
    @NoArgsConstructor
    public static class UserDiamond {
        private String diamondId;
        private String diamondName;
        private String diamondType;
        private int quantity;
        private String description;
        private boolean isEquipped;
        
        public UserDiamond(String diamondId, String diamondName, String diamondType, int quantity) {
            this.diamondId = diamondId;
            this.diamondName = diamondName;
            this.diamondType = diamondType;
            this.quantity = quantity;
            this.description = "Diamond item";
            this.isEquipped = false;
        }
    }

    /**
     * Simplified user info for lists
     */
    @Data
    @NoArgsConstructor
    public static class UserInfo {
        private int userId;
        private String username;
        private String avatarUrl;
        private String status;
        private int level;
        private String vipLevel;
        
        public UserInfo(User user) {
            this.userId = user.getUid();
            this.username = user.getAccount();
            this.avatarUrl = "/avatars/default.png";
            this.status = "active";
            this.level = user.getLevel();
            this.vipLevel = "VIP1";
        }
        
        public UserInfo(Player player) {
            this.userId = player.getUid();
            this.username = player.getAccount();
            this.avatarUrl = player.getAvatarUrl();
            this.status = player.isOnline() ? "online" : "offline";
            this.level = player.getLevel();
            this.vipLevel = calculateVipLevel(player);
        }
        
        private String calculateVipLevel(Player player) {
            int totalMoney = player.getRoommoney() + player.getAllmoney();
            if (totalMoney > 1000000) return "VIP5";
            if (totalMoney > 500000) return "VIP4";
            if (totalMoney > 100000) return "VIP3";
            if (totalMoney > 50000) return "VIP2";
            return "VIP1";
        }
    }
}