package com.archy.texasholder.entity;

public class RoomDB {
    private Integer id;
    private String showname;
    private String name;
    private Integer bbet;
    private Integer sbet;
    private Integer maxbuy;
    private Integer minbuy;
    private String roomtype;

    public RoomDB() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getShowname() {
        return showname;
    }

    public void setShowname(String showname) {
        this.showname = showname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getBbet() {
        return bbet;
    }

    public void setBbet(Integer bbet) {
        this.bbet = bbet;
    }

    public Integer getSbet() {
        return sbet;
    }

    public void setSbet(Integer sbet) {
        this.sbet = sbet;
    }

    public Integer getMaxbuy() {
        return maxbuy;
    }

    public void setMaxbuy(Integer maxbuy) {
        this.maxbuy = maxbuy;
    }

    public Integer getMinbuy() {
        return minbuy;
    }

    public void setMinbuy(Integer minbuy) {
        this.minbuy = minbuy;
    }

    public String getRoomtype() {
        return roomtype;
    }

    public void setRoomtype(String roomtype) {
        this.roomtype = roomtype;
    }

    @Override
    public String toString() {
        return "Room{" +
                "id=" + id +
                ", showname='" + showname + '\'' +
                ", name='" + name + '\'' +
                ", bbet=" + bbet +
                ", sbet=" + sbet +
                ", maxbuy=" + maxbuy +
                ", minbuy=" + minbuy +
                ", roomtype='" + roomtype + '\'' +
                '}';
    }
}