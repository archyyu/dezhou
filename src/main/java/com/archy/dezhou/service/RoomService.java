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

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;

@Service
public class RoomService{

    @Resource
    private RoomDBMapper roomDBMapper;

    private Map<Integer,GameRoom> roomsMap = new HashMap<Integer,GameRoom>();
	
	private Map<Integer, Player> usersMap = new HashMap<Integer, Player>();

	public RoomDB getRoomById(int roomId){
        return roomDBMapper.selectByPrimaryKey(roomId);
    }

	// @PostConstruct
	// public void init() {
	// 	List<RoomDB> roomDBList = roomDBMapper.selectAllRooms();
	// 	roomDBList.forEach(roomDB -> {
	// 		GameRoom room = new GameRoom(roomDB);
	// 		this.roomsMap.put(room.getRoomid(), room);
	// 	});
	// }

	public List<RoomDB> getRoomTypeList() {
		return this.roomDBMapper.selectAllRooms();
	}
	
	public GameRoom getRoom(int id)
	{
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
	
	public GameRoom createGameRoom(String uid, String userName, int roomTypeId) {

		RoomDB roomDB = this.roomDBMapper.selectByPrimaryKey(roomTypeId);

		GameRoom gameRoom = new GameRoom(roomDB);
		gameRoom.setCreator(userName);

		this.roomsMap.put(gameRoom.getRoomid(), gameRoom);

		return gameRoom;
	}
	
	public void addRoom(GameRoom room)
	{
		roomsMap.put(room.getRoomid(),room);
	}
	
	public List<GameRoom> getRoomList()
	{
		return new ArrayList<GameRoom>(this.roomsMap.values());
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