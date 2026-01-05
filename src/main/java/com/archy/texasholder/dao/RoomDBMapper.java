package com.archy.texasholder.dao;

import java.util.List;

import com.archy.texasholder.entity.RoomDB;

public interface RoomDBMapper {

    int insertSelective(RoomDB record);

    RoomDB selectByPrimaryKey(Integer id);

    List<RoomDB> selectAllRooms();

    RoomDB selectByName(String name);

    int updateByPrimaryKeySelective(RoomDB record);

    int deleteByPrimaryKey(Integer id);
}