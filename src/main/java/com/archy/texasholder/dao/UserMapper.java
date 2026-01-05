package com.archy.texasholder.dao;

import com.archy.texasholder.entity.User;

public interface UserMapper {

    int insertSelective(User record);

    User selectByPrimaryKey(Integer uid);

    User selectByAccount(String account);

    User selectByPhone(String phone);

    int updateByPrimaryKeySelective(User record);

}