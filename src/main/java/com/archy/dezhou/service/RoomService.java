package com.archy.dezhou.service;

import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Service;

import com.archy.dezhou.dao.RoomDBMapper;
import com.archy.dezhou.entity.Player;
import com.archy.dezhou.entity.RoomDB;
import com.archy.dezhou.entity.room.GameRoom;
import com.archy.dezhou.entity.room.PukerGame;

import jakarta.annotation.Resource;

@Service
public class RoomService{

    @Resource
    private RoomDBMapper roomDBMapper;

    private Map<Integer,PukerGame> roomsMap = new HashMap<Integer,PukerGame>();
	
	private Map<Integer, Player> usersMap = new HashMap<Integer, Player>();

	public RoomDB getRoomById(int roomId){
        return roomDBMapper.selectByPrimaryKey(roomId);
    }

	public List<RoomDB> getRoomTypeList() {
		return this.roomDBMapper.selectAllRooms();
	}
	
	public PukerGame getRoom(Integer id)
	{
		if (id == null) {
			return null;
		}
		return roomsMap.get(id);
	}
	
	public GameRoom getRoomByName(String roomName)
	{
		for (GameRoom room : this.roomsMap.values()) {
			if (room.getName().equals(roomName)) {
				return room;
			}
		}
		return null;
	}
	
	public GameRoom createGameRoom(String uid, String userName, int roomTypeId, String roomName) {

		RoomDB roomDB = this.roomDBMapper.selectByPrimaryKey(roomTypeId);

		PukerGame gameRoom = new PukerGame(roomDB);
		gameRoom.setCreator(userName);
		gameRoom.setName(roomName);

		this.roomsMap.put(gameRoom.getRoomid(), gameRoom);

		return gameRoom;
	}
	
	public void addRoom(PukerGame room)
	{
		roomsMap.put(room.getRoomid(),room);
	}
	
	public List<PukerGame> getRoomList()
	{
		return new ArrayList<PukerGame>(this.roomsMap.values());
	}

	public List<PukerGame> getRoomListByTypeId(int roomTypeId) {
		return this.roomsMap.values().stream().filter(item -> item.getRoomTypeId() == roomTypeId).collect(Collectors.toList());
	}
	
	public int destroyRoom(GameRoom room)
	{
		roomsMap.remove(room.getRoomid());
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
	}
	
	public Player[] userToArray()
	{
		return this.usersMap.values().toArray(new Player[this.usersMap.size()]);
	}



}