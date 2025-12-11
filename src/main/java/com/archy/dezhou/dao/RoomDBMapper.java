package com.archy.dezhou.dao;

import com.archy.dezhou.entity.RoomDB;

public interface RoomDBMapper {

    int insertSelective(RoomDB record);

    RoomDB selectByPrimaryKey(Integer id);

    RoomDB selectByName(String name);

    int updateByPrimaryKeySelective(RoomDB record);

    int deleteByPrimaryKey(Integer id);
}