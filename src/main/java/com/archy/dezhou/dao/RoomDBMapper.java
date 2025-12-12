package com.archy.dezhou.dao;

import java.util.List;

import com.archy.dezhou.entity.RoomDB;

public interface RoomDBMapper {

    int insertSelective(RoomDB record);

    RoomDB selectByPrimaryKey(Integer id);

    List<RoomDB> selectAllRooms();

    RoomDB selectByName(String name);

    int updateByPrimaryKeySelective(RoomDB record);

    int deleteByPrimaryKey(Integer id);
}