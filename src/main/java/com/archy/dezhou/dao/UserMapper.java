package com.archy.dezhou.dao;

import com.archy.dezhou.entity.User;

public interface UserMapper {

    int insertSelective(User record);

    User selectByPrimaryKey(Integer uid);

    User selectByAccount(String account);

    User selectByPhone(String phone);

    int updateByPrimaryKeySelective(User record);

}