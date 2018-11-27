package com.yl.Global;

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


import org.apache.log4j.Logger;


import com.yl.room.Room;
import com.yl.room.base.IRoom;
import com.yl.service.PukeModuleService;
import com.yl.service.Imp.PukeModuleServiceImp;
import com.yl.thread.MemberRemoveBind;
import com.yl.thread.roomUnit.OfflineDealUnit;
import com.yl.thread.roomUnit.RoomDealUnit;
import com.yl.util.*;
import com.yl.entity.Prop;
import com.yl.entity.Puke;
import com.yl.container.*;

public class UserModule extends AbstractExtension
{
	public Map<Integer,Puke> randomPuke; // 生成牌的集合
	public String handleName = "httphander";
	private HashMap<String, HashMap<String, String>> roomset;// 房间设置信息
	private HashMap<String, Prop> propMap;

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
		roomMsgInit();
		achieveInit(); // edited by ai 2014-7-15
		threadStartInit();
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
		propMap = XmlReaderUtils.retXmlReaderByProp(ConstList.PropertyFileName);
		log.info("**propMap init Ok");
		
		ConstList.blackWordList = new HashMap<Integer, String>();
		log.info("**blackWordList init Ok");
	}
	
	public Map<String,String> getRoomConfigByType(String roomType)
	{
		return roomset.get(roomType);
	}
	
	private void roomMsgInit()
	{
		roomset = XmlReaderUtils.retXmlReaderByRoom(ConstList.roomConfigFileName);
		for (String rkey : roomset.keySet())
		{
			HashMap<String, String> roomConfig = roomset.get(rkey);
			try
			{
				IRoom room = new Room(roomConfig.get("name"),"","admin",roomConfig.get("name"));
				this.addRoom(room);
			}
			catch (Exception e)
			{
				log.error("**buildroom error**",e);
			}
		}
		log.warn("room created ok");
	}

	private void achieveInit()
	{
		
		new AchValue().init();
	}


	private void threadStartInit()
	{
		// 定时永久写入数据库文件。
		MemberRemoveBind write2Db = new MemberRemoveBind("write2Db", handleName);
		write2Db.mrbThread.start();
	}

	public void destroy()
	{

	}

	public void handleRequest(String cmd, String[] params, User who, int roomId)
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
		return propMap;
	}

	public boolean isValidDaoju(int djid)
	{
		int[] djlist = new int[]
		{ 3, 10, 13, 17, 19, 21, 22, 23, 25, 33, 34, 39, 47, 57, 61, 79, 91,
				95, 96, 102, 104, 106, 108, 109, 110, 111, 112, 113, 116, 118,
				119, 120, 121, 122, 123, 126, 127, 128, 129, 130, };
		for (int i = 0; i < djlist.length; i++)
		{
			if (djlist[i] == djid)
			{
				return true;
			}
		}
		return false;
	}

	public String GetConsumeId(int djid)
	{
		if (djid < 1 || djid > 131)
			return "";

		String key = "" + djid;
		HashMap<String, Prop> propMapList = getPropMap();
		Prop prop = propMapList.get(key);
		String consumeIdString = prop.getConsumeCode();
		try
		{
			long consumeId = Long.parseLong(consumeIdString);
			if (consumeId > 0)
				return consumeIdString;
			else
				return "";
		}
		catch (Exception e)
		{
			return "";
		}
	}

	public int GetConsumeGold(int djid)
	{
		log.warn("GetConsumeGold(int " + djid + ") called!");
		if (djid < 1 || djid > 131)
			return -2;
		String key = "" + djid;
		HashMap<String, Prop> propMapList = getPropMap();
		log.warn("propMapList.size=" + propMapList.size());
		for (String myKey : propMapList.keySet())
		{
			log.warn("" + myKey + ":"
					+ propMapList.get(myKey).getName());
		}
		Prop prop = propMapList.get(key);
		int CurrencyAmount = prop.getAmount();
		log.warn("CurrencyAmount=" + CurrencyAmount);
		try
		{
			if (CurrencyAmount > 0)
				return CurrencyAmount;
			else
				return -3;
		}
		catch (Exception e)
		{
			return -4;
		}
	}

	public int getAdminRoomId()
	{
		IRoom room = this.getRoomByName("admin");
		if (room != null)
		{
			return room.getRoomId();
		}
		else
		{
			return 1;
		}
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