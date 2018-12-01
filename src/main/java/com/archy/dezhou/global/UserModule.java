package com.archy.dezhou.global;

/**
 * @author archy_yu
 * 调整方向: 工具类，成为管理房间和玩家的单例类
 **/

import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.List;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.archy.dezhou.container.AbstractExtension;
import com.archy.dezhou.container.ActionscriptObject;
import com.archy.dezhou.entity.Player;
import com.archy.dezhou.entity.Puke;
import com.archy.dezhou.entity.puker.PukerKit;
import com.archy.dezhou.util.XLoad;
import org.apache.log4j.Logger;


import com.archy.dezhou.entity.room.Room;
import com.archy.dezhou.thread.roomUnit.OfflineDealUnit;
import com.archy.dezhou.thread.roomUnit.RoomDealUnit;

public class UserModule extends AbstractExtension
{

 	private Logger log = Logger.getLogger(getClass());

	private static UserModule instance = null;
	
	private Map<Integer,Room> roomsMap = new HashMap<Integer,Room>();
	
	private Map<Integer, Player> usersMap = new HashMap<Integer, Player>();
	
	public Room getRoom(int id)
	{
		return roomsMap.get(id);
	}
	
	public Room getRoomByName(String name)
	{
		for(Map.Entry<Integer, Room> entry : this.roomsMap.entrySet())
		{
			if(entry.getValue().getName().equals(name))
			{
				return entry.getValue();
			}
		}
		return null;
	}
	
	public void addRoom(Room room)
	{
		roomsMap.put(room.getRoomId(),room);
	}
	
	public List<Room> getRoomList()
	{
		return new ArrayList<Room>(this.roomsMap.values());
	}
	
	public int destroyRoom(Room room)
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
	
	public static UserModule getInstance()
	{
		if(instance == null)
		{
			instance = new UserModule();
		}
		return instance;
	}
	
	
	private UserModule()
	{

	}

	public void init()
	{
		log.info("user module **init**");

		roomListInit();

		this.startRoomThread();
		
	}
	
	private void startRoomThread()
	{
		Thread thread = new Thread(new RoomDealUnit());
		thread.setName("RoomDealUnit");
		thread.start();
		log.warn("Room deal unit thread started");
	}
	
	private void startCommonThread()
	{
		Thread thread = new Thread(new OfflineDealUnit());
		thread.setName("RoomDealUnit");
		thread.start();
		log.warn("Offline Deal Unit thread started");
	}

	private void roomListInit()
	{
		String content = new String(XLoad.getResource("room.json"));

        JSONArray array = JSONArray.parseArray(content);

        for(int i = 0 ; i < array.size() ; i++){

            JSONObject obj = array.getJSONObject(i);

            try
            {
                Room room = new Room(obj);
                this.addRoom(room);
            }
            catch (Exception ex)
            {

            }
        }


		log.warn("room created ok");
	}


	public void destroy()
	{

	}

	public int[] getplayerNum(String roomKey)
	{
		int[] playerNum = new int[] { 0, 0, 0 };
		
		Room room = this.getRoomByName(roomKey);
		if(room != null)
		{
			playerNum[1] = room.getUserCount();
			playerNum[2] = room.getSpectatorCount();
		}
		
		return playerNum;
	}

}