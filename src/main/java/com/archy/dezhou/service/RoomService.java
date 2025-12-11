package com.archy.dezhou.service;

import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Service;

import com.archy.dezhou.dao.RoomDBMapper;
import com.archy.dezhou.entity.Player;
import com.archy.dezhou.entity.RoomDB;
import com.archy.dezhou.entity.room.GameRoom;

import jakarta.annotation.Resource;

@Service
public class RoomService{

    @Resource
    private RoomDBMapper roomDBMapper;

    public RoomDB getRoomById(int roomId){
        return roomDBMapper.selectByPrimaryKey(roomId);
    }


    private Map<Integer,GameRoom> roomsMap = new HashMap<Integer,GameRoom>();
	
	private Map<Integer, Player> usersMap = new HashMap<Integer, Player>();
	
	public GameRoom getRoom(int id)
	{
		return roomsMap.get(id);
	}
	
	public GameRoom getRoomByName(String name)
	{
		for(Map.Entry<Integer, GameRoom> entry : this.roomsMap.entrySet())
		{
			if(entry.getValue().getName().equals(name))
			{
				return entry.getValue();
			}
		}
		return null;
	}
	
	public void addRoom(GameRoom room)
	{
		roomsMap.put(room.getRoomId(),room);
	}
	
	public List<GameRoom> getRoomList()
	{
		return new ArrayList<GameRoom>(this.roomsMap.values());
	}
	
	public int destroyRoom(GameRoom room)
	{
		roomsMap.remove(room.getRoomId());
		return 0;
	}
	
	public void destroyRoom(int roomId)
	{
		roomsMap.remove(roomId);
	}
	
	public Player getUserByUserId(int userId)
	{
		return this.usersMap.get(userId);
	}
	
	public void addUser(Player user)
	{
		this.usersMap.put(user.getUid(),user);
	}
	
	public void removeUser(int userId)
	{
		this.usersMap.remove(userId);
	}

	
	public void UserLogout(int userId)
	{
		this.usersMap.remove(userId);
		log.warn("userId: " + userId + " logout");
	}
	
	public Player[] userToArray()
	{
		return this.usersMap.values().toArray(new Player[this.usersMap.size()]);
	}



}