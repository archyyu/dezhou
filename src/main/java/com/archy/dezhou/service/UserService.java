package com.archy.dezhou.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.archy.dezhou.dao.UserMapper;
import com.archy.dezhou.entity.Player;
import com.archy.dezhou.entity.User;

import jakarta.annotation.Resource;

@Service
public class UserService {

    @Resource
    private UserMapper userMapper;

    private Map<Integer, Player> usersMap = new HashMap<Integer, Player>();

    public User getUserById(int userId){
        return userMapper.selectByPrimaryKey(userId);
    }

    public Player getUserByUserId(int userId)
    {
        return usersMap.get(userId);
    }

}
