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

    private Map<Integer, Player> playersMap = new HashMap<Integer, Player>();

    public User getUserById(int userId){
        return userMapper.selectByPrimaryKey(userId);
    }

    public User getUserByAccount(String account) {
        return this.userMapper.selectByAccount(account);
    }

    public int registerUser(User user) {
        return this.userMapper.insertSelective(user);
    }

    public Player getUserByUserId(int userId)
    {
        Player player = playersMap.get(userId);
        if (player == null)
        {
            User user = getUserById(userId);
            if(user != null)
            {
                player = new Player(user);
                playersMap.put(userId, player);
            }
        }
        return player;
    }
    
    public Player getUserByUsername(String username)
    {
        User user = userMapper.selectByAccount(username);
        if(user != null)
        {
            Player player = new Player(user);
            playersMap.put(user.getUid(), player);
            return player;
        }
        return null;
    }

    public boolean addUser(User user){
        return this.userMapper.insertSelective(user) > 0;
    }
}
