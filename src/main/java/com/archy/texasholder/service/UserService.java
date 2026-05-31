package com.archy.texasholder.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.archy.texasholder.entity.Player;
import com.archy.texasholder.entity.User;
import com.archy.texasholder.repo.UserRepository;

import jakarta.annotation.Resource;

@Service
public class UserService {

    @Resource
    private UserRepository userRepository;

    private Map<Integer, Player> playersMap = new HashMap<Integer, Player>();

    public Optional<User> getUserById(int userId) {
        return this.userRepository.findById(userId);
    }

    public Optional<User> getUserByAccount(String account) {
        return this.userRepository.findByAccount(account);
    }

    public int registerUser(User user) {
        this.userRepository.save(user);
        return 1;
    }

    public Player getUserByUserId(int userId)
    {
        Player player = playersMap.get(userId);
        if (player == null)
        {
            Optional<User> user = this.getUserById(userId);
            if(user.isPresent())
            {
                player = new Player(user.get());
                playersMap.put(userId, player);
            }
        }
        return player;
    }
    
    public Player getUserByUsername(String username)
    {
        Optional<User> user = this.getUserByAccount(username);
        if(user.isPresent())
        {
            Player player = new Player(user.get());
            playersMap.put(user.get().getUid(), player);
            return player;
        }
        return null;
    }

    public boolean addUser(User user){
        this.userRepository.save(user);
        return true;
    }
}
