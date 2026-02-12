package com.archy.texasholder.entity;

import java.util.logging.Logger;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;

@Data
@Entity
@Table(name = "dezhou_user")
public class User {

    @Transient
    protected Logger log = Logger.getLogger(getClass().getName());

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

    public User() {
    }

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

    public Integer getRoomid() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }

    public boolean isStandUpExpired(long time) {
        return false;
    }

    public boolean isLeaveExpired(long time) {
        return false;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean isPlaying) {
        this.isPlaying = isPlaying;
    }

    public int getAMoney() {
        return allmoney;
    }

    public void setAmoney(int mtmoneyoney) {
        this.allmoney = mtmoneyoney;
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

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public Integer getUid() {
        return uid;
    }

    public String getPassWord() {
        return password;
    }

    public void setPassWord(String password) {
        this.password = password;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getExprience() {
        return exprience;
    }

    public void setExprience(int exprience) {
        this.exprience = exprience;
    }

    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getRmoney() {
        return roommoney;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGendar() {
        return sex;
    }

    public void setGendar(String gendar) {
        this.sex = gendar;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getLogintime() {
        return logintime;
    }

    public void setLogintime(String logintime) {
        this.logintime = logintime;
    }

    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Long timeStamp) {
        this.lastUpdateTime = timeStamp;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getRoommoney() {
        return roommoney;
    }

    public void setRoommoney(int roommoney) {
        this.roommoney = roommoney;
    }

    public int getAllmoney() {
        return allmoney;
    }

    public void setAllmoney(int allmoney) {
        this.allmoney = allmoney;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getRegtime() {
        return regtime;
    }

    public void setRegtime(String regtime) {
        this.regtime = regtime;
    }

    public void setLastUpdateTime(long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
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
