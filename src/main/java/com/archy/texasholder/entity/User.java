package com.archy.texasholder.entity;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "dezhou_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "uid")
    private Integer uid;

    @Column(name = "account")
    private String account = "";

    @Column(name = "password")
    private String password = "";

    @Transient
    private boolean isPlaying = false;

    @Column(name = "roommoney")
    private int roommoney = 0;

    @Column(name = "allmoney")
    private int allmoney = 0;

    @Column(name = "exprience")
    private int exprience = 0;

    @Column(name = "gold")
    private int gold = 0;

    @Column(name = "mobile")
    private String mobile = "";

    @Transient
    private String email = "";

    @Column(name = "level")
    private int level = 0;

    @Column(name = "sex")
    private String sex = "";

    @Column(name = "address")
    private String address = "";

    @Column(name = "regtime")
    private String regtime = "";

    @Column(name = "birthday")
    private String birthday = "";

    @Column(name = "logintime")
    private String logintime = "";

    @Transient
    private long lastUpdateTime = System.currentTimeMillis();

    @Transient
    private Integer roomId;

    public User(User user) {
        this.uid = user.getUid();
        this.account = user.getAccount();
        this.password = user.getPassword();
        this.isPlaying = user.isPlaying();
        this.roommoney = user.getRoommoney();
        this.allmoney = user.getAllmoney();
        this.exprience = user.getExprience();
        this.gold = user.getGold();
        this.mobile = user.getMobile();
        this.email = user.getEmail();
        this.level = user.getLevel();
    }

    public boolean isStandUpExpired(long time) {
        return false;
    }

    public boolean isLeaveExpired(long time) {
        return false;
    }

    public void addAmoney(int money) {
        if (money > 0) {
            this.allmoney += money;
        }
    }

    public void deductAmoney(int money) {
        if (money < this.allmoney) {
            this.allmoney -= money;
        } else {
            this.allmoney = 0;
        }
    }

    public void clearRoomMoney() {
        this.roommoney = 0;
    }

    public void addRmoney(int rmoney) {
        if (rmoney >= 0) {
            this.roommoney += rmoney;
        }
    }

    public void deductRmoney(int rmoney) {
        if (rmoney >= 0 && rmoney <= this.roommoney) {
            this.roommoney -= rmoney;
        }
    }

    public boolean isOffLine() {
        return false;
    }

    public void setBackupRmoney(int rmoney) {
    }

    @Override
    public String toString() {
        return "User{" +
                "uid=" + uid +
                ", account='" + account + '\'' +
                ", password='" + password + '\'' +
                ", isPlaying=" + isPlaying +
                ", roommoney=" + roommoney +
                ", allmoney=" + allmoney +
                ", exprience=" + exprience +
                ", gold=" + gold +
                ", mobile='" + mobile + '\'' +
                ", email='" + email + '\'' +
                ", level=" + level +
                ", sex='" + sex + '\'' +
                ", address='" + address + '\'' +
                ", regtime='" + regtime + '\'' +
                ", birthday='" + birthday + '\'' +
                ", logintime='" + logintime + '\'' +
                ", lastUpdateTime=" + lastUpdateTime +
                ", roomId=" + roomId +
                '}';
    }
}
