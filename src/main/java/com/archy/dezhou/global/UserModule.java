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
import com.archy.dezhou.container.User;
import com.archy.dezhou.entity.Puke;
import com.archy.dezhou.service.Imp.PukeModuleServiceImp;
import com.archy.dezhou.util.XLoad;
import org.apache.log4j.Logger;


import com.archy.dezhou.entity.room.Room;
import com.archy.dezhou.entity.room.base.IRoom;
import com.archy.dezhou.service.PukeModuleService;
import com.archy.dezhou.thread.roomUnit.OfflineDealUnit;
import com.archy.dezhou.thread.roomUnit.RoomDealUnit;
import com.archy.dezhou.entity.Prop;

public class UserModule extends AbstractExtension
{
	public Map<Integer, Puke> randomPuke; // 生成牌的集合

 	private Logger log = Logger.getLogger(getClass());

	private static UserModule instance = null;
	
	private Map<Integer,IRoom> roomsMap = new HashMap<Integer,IRoom>();
	
	private HashMap<Integer, User> usersMap = new HashMap<Integer, User>();
	
	public IRoom getRoom(int id)
	{
		return roomsMap.get(id);
	}
	
	public IRoom getRoomByName(String name)
	{
		for(Map.Entry<Integer, IRoom> entry : this.roomsMap.entrySet())
		{
			if(entry.getValue().getName().equals(name))
			{
				return entry.getValue();
			}
		}
		return null;
	}
	
	public void addRoom(IRoom room)
	{
		roomsMap.put(room.getRoomId(),room);
	}
	
	public List<IRoom> getRoomList()
	{
		return new ArrayList<IRoom>(this.roomsMap.values());
	}
	
	public int destroyRoom(IRoom room)
	{
		roomsMap.remove(room.getRoomId());
		return 0;
	}
	
	public void destroyRoom(int roomId)
	{
		roomsMap.remove(roomId);
	}
	
	public User getUserByUserId(int userId)
	{
		return this.usersMap.get(userId);
	}
	
	public void addUser(User user)
	{
		this.usersMap.put(user.getUserId(),user);
	}
	
	public void removeUser(int userId)
	{
		this.usersMap.remove(userId);
	}
	
	public void UserLogout(int userId)
	{
		this.usersMap.remove(userId);
		UserInfoMemoryCache.removeUserInfo(userId + "");
		log.warn("userId: " + userId + " logout");
	}
	
	public User[] userToArray()
	{
		return this.usersMap.values().toArray(new User[this.usersMap.size()]);
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

		userInfoInit();
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

	private void userInfoInit()
	{
		PukeModuleService pms = new PukeModuleServiceImp();
		randomPuke = pms.Puke();

	}

	private void roomListInit()
	{
		String content = new String(XLoad.getResource("room.json"));

        JSONArray array = JSONArray.parseArray(content);

        for(int i = 0 ; i < array.size() ; i++){

            JSONObject obj = array.getJSONObject(i);

            try
            {
                IRoom room = new Room(obj);
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


	// 发数据包给客户
	public void sendMessageBag(ActionscriptObject aObj, Room r)
	{
		LinkedList<SocketChannel> recipientList = new LinkedList<SocketChannel>();
		sendResponse(aObj, -1, null, recipientList);
	}

	public HashMap<String, Prop> getPropMap()
	{
		return new HashMap<String, Prop>();
	}


	public int[] getplayerNum(String roomKey)
	{
		int[] playerNum = new int[] { 0, 0, 0 };
		
		IRoom room = this.getRoomByName(roomKey);
		if(room != null)
		{
			playerNum[1] = room.getUserCount();
			playerNum[2] = room.getSpectatorCount();
		}
		
		return playerNum;
	}

}