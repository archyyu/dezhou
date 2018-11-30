package com.archy.dezhou.container;

import com.archy.dezhou.entity.room.Room;

import java.util.*;

public class Zone
{

	public static final int STATUS_ACTIVE = 0;
	public static final int STATUS_INACTIVE = 1;
	public String userNameAvoidChars;
	public String roomNameAvoidChars;
	private String name;
	private HashMap<Integer, Room> roomList;
	private int autoJoinRoomId;
	private HashMap<String, Integer> userNames;
	private boolean customLogin;
	private boolean isDeactivating;
	private boolean updateUserCount;
	private int maxUsers;
	private int maxRooms;
	private int maxRoomNameLen;
	private int maxUserNameLen;
	private boolean buddyList;
	private boolean emptyNames;
	private int maxRoomsPerUser;
	private boolean sendVarsInRoomList;
	private boolean roomUpdates;
	private boolean receivePubMsgInternalEvents;
	private boolean receivePrivMsgInternalEvents;
	private boolean autoReloadExtensions;
	private int status;
	private Map<String, Object> disabledEvents;
	private Map<String, Object> disabledSysActions;



	public Zone(String name, String customLogin)
	{
		this.name = name;
		roomList = new HashMap<Integer, Room>();
		userNames = new HashMap<String, Integer>();
		autoJoinRoomId = 1;
		maxRooms = -1;
		maxUsers = -1;
		updateUserCount = true;
		buddyList = false;
		isDeactivating = false;
		emptyNames = true;
		status = 0;
		maxRoomsPerUser = 5;
		roomUpdates = true;
		maxRoomNameLen = 20;
		maxUserNameLen = 60;
		receivePubMsgInternalEvents = false;
		autoReloadExtensions = false;
        this.customLogin = "true".equalsIgnoreCase(customLogin);
		disabledEvents = new HashMap<String, Object>();
		disabledSysActions = new HashMap<String, Object>();
	}

	public void init(Object obj)
	{
	}

	public void destroy(Object param)
	{
		saveAllBuddyLists();
	}

	public void addDisabledEvent(String evt)
	{
		disabledEvents.put(evt.toLowerCase(), new Object());
	}

	public boolean isEventDisabled(String evt)
	{
		return disabledEvents.get(evt.toLowerCase()) != null;
	}

	public void clearDisabledEvents()
	{
		disabledEvents.clear();
	}

	public void addDisabledSysAction(String action)
	{
		disabledSysActions.put(action.toLowerCase(), new Object());
	}

	public boolean isSysActionDisabled(String action)
	{
		return disabledSysActions.get(action.toLowerCase()) != null;
	}

	public void clearDisabledSysActions()
	{
		disabledSysActions.clear();
	}

	public boolean isPubMsgInternalEventEnabled()
	{
		return receivePubMsgInternalEvents;
	}

	public void setPubMsgInternalEvent(boolean b)
	{
		receivePubMsgInternalEvents = b;
	}

	public boolean isPrivMsgInternalEventEnabled()
	{
		return receivePrivMsgInternalEvents;
	}

	public void setPrivMsgInternalEvent(boolean b)
	{
		receivePrivMsgInternalEvents = b;
	}

	public String getName()
	{
		return name;
	}

	public boolean hasCustomLogin()
	{
		return customLogin;
	}

	public int getStatus()
	{
		return status;
	}

	public boolean getVarsOnRoomList()
	{
		return sendVarsInRoomList;
	}

	public void setVarsOnRoomList(boolean b)
	{
		sendVarsInRoomList = b;
	}

	public boolean getRoomUpdates()
	{
		return roomUpdates;
	}

	public void setRoomUpdates(boolean b)
	{
		roomUpdates = b;
	}

	public boolean isActive()
	{
		return status == 0;
	}

	public int getMaxRoomsPerUser()
	{
		return maxRoomsPerUser;
	}

	public void setMaxRoomsPerUser(int m)
	{
		maxRoomsPerUser = m;
	}

	public boolean validateUserName(String name)
	{
		boolean res = false;
		synchronized (userNames)
		{
			res = userNames.containsKey(name);
		}
		return res;
	}

	public void addName(String name, Integer uid)
	{
		synchronized (userNames)
		{
			userNames.put(name, uid);
		}
	}

	public void removeName(String name, User u)
	{
		synchronized (userNames)
		{
			userNames.remove(name);
		}
	}

	public void destroyVariables(User u)
	{
		synchronized (roomList)
		{
			Collection<Room> c = roomList.values();
			for (Iterator<Room> i = c.iterator(); i.hasNext();)
			{
				Room r = i.next();
				int varCount = 0;
				StringBuffer varResponse = new StringBuffer();
				Map<String, RoomVariable> roomVars = r.getVariables();
				synchronized (roomVars)
				{
					LinkedList<Object> vNames = new LinkedList<Object>(
                            roomVars.keySet());
					for (Iterator<?> v = vNames.iterator(); v.hasNext();)
					{
						String varName = (String) v.next();
						RoomVariable rv = roomVars.get(varName);
						if (rv.getOwner() == u)
						{
							varResponse.append("<var n='").append(varName);
							varResponse.append("' t='x' />");
							varCount++;
							roomVars.remove(varName);
						}
					}

				}
				if (varCount > 0)
				{
					StringBuffer response = (new StringBuffer(
							"<msg t='sys'><body action='rVarsUpdate' r='"))
							.append(r.getRoomId()).append("'><vars>")
							.append(varResponse).append("</vars></body></msg>");
				}
			}

		}
	}

	public void clearAllExtensions()
	{

	}

	public AbstractExtension getExtension(String name)
	{
		return null;
	}

	public Object getChannelFromName(String name)
	{

		return null;
	}

	public User getUserByName(String name)
	{
		return null;
	}

	public Integer getUserIdByName(String name)
	{
		return userNames.get(name);
	}

	public int removeRoom(Room rm, boolean exitingAllRooms, String uid)
	{
		int roomid = rm.getRoomId();
		if (!rm.isTemp())// 不是临时房间不能删除
			return 0;
		int removeId = -1;
		for (int i = 0; i < roomList.size(); i++)
		{
			if (roomList.get(i).getRoomId() == roomid)
			{
				removeId = i;
				break;
			}
		}

		if (removeId > 0)
		{
			HashMap<Integer, Room> roomlistTmp = new HashMap<Integer, Room>();
			int k = 0;
			for (int j = 0; j < roomList.size(); j++)
			{
				if (j != removeId)
				{
					roomlistTmp.put(k, roomList.get(j));
					k++;
				}
			}
			roomList = roomlistTmp;
			return 1;
		}
		else
			return -1;

	}
	
	public Room findRoom(String rn)
	{
		Room rm = null;
		for (int j = 0; j < roomList.size(); j++)
		{
			if ((roomList.get(j).getName()).equals(rn))
			{
				rm = roomList.get(j);
				break;
			}
		}
		return rm;
	}

	public int addRoom(Room rm)
	{
		if(rm == null)
		{
			return -1;
		}
		roomList.put(rm.getRoomId(), rm);
		return 1;
	}

	public void activate()
	{
		if (!isDeactivating && status == 1)
		{
			synchronized (roomList)
			{
				Room r;
				for (Iterator<Room> i = roomList.values().iterator(); i
						.hasNext();)
					r = i.next();

			}
			status = 0;
		}
	}

	public void setAutoJoinRoom(int roomId)
	{
		autoJoinRoomId = roomId;
	}

	public int getAutoJoinRoom()
	{
		return autoJoinRoomId;
	}

	public Room getRoom(int roomId)
	{
		return roomList.get(roomId);
	}

	public Room getRoomByName(String name)
	{
		Room r = null;
		synchronized (roomList)
		{
			for (Iterator<Room> i = roomList.values().iterator(); i.hasNext();)
			{
				r = i.next();
				if (name.equals(r.getName()))
				{
					return r;
				}
			}

		}
		return r;
	}

	public List<Room> getRoomList()
	{
		List<Room> list;
		synchronized (roomList)
		{
			list = new LinkedList<Room>(roomList.values());
		}
		return list;
	}

	public HashMap<Integer, Room> getRoomListOriginal()
	{
		return roomList;
	}

	public Object[] getRooms()
	{
		Object rList[] = new Room[roomList.size()];
		synchronized (roomList)
		{
			rList = roomList.values().toArray();
		}
		return rList;
	}

	public boolean roomNameAlreadyExist(String roomName)
	{
		List<Room> rmList = getRoomList();
		for (Iterator<Room> it = rmList.iterator(); it.hasNext();)
		{
			if ((it.next()).getName().equals(roomName))
			{	
				return true;
			}
		}
		return false;
	}

	public int getRoomCount()
	{
		return roomList.size();
	}

	public int getUserCount()
	{
		return userNames.size();
	}

	public boolean isThereRoom()
	{
		boolean response = true;
		if (maxUsers > -1 && maxUsers <= userNames.size())
			response = false;
		return response;
	}

//	public LinkedList<Room> getAllUsersInZone()
//	{
//		List<Room> allRooms = getRoomList();
//		HashSet<?> allChannells = new HashSet<Object>();
//		for (Iterator<?> it = allRooms.iterator(); it.hasNext();)
//		{
//			Room tempRoom = (Room) it.next();
//
//		}
//
//		return new LinkedList<Object>(allChannells);
//	}

//	public LinkedList<?> getChannelList()
//	{
//		LinkedList<?> ll = new LinkedList<Object>();
//		synchronized (userNames)
//		{
//			Collection<Integer> c = userNames.values();
//			for (Iterator<Integer> i = c.iterator(); i.hasNext();)
//			{
//				Integer id = (Integer) i.next();
//			}
//
//		}
//		return ll;
//	}

	public Map<String, Integer> getUserNames()
	{
		Map<String, Integer> names = new HashMap<String,Integer>();
		synchronized (userNames)
		{
			names.putAll(userNames);
		}
		return names;
	}

	public boolean hasBuddyList()
	{
		return buddyList;
	}

	public void initBuddyList(int listLength)
	{

	}

	public void addBuddy(String owner, String buddyName)
	{

	}

	public boolean removeBuddy(String owner, String buddyName)
	{

		return false;
	}

	public Object loadBuddyList(String owner)

	{
		if (hasBuddyList())
			return null;
		else
			return null;
	}

	public void saveBuddyList(String name)
	{

	}

	public void saveAllBuddyLists()
	{

	}

	public void clearBuddyList(String owner)
	{

	}

	public boolean checkBuddy(String name)
	{
		return validateUserName(name);
	}

	public String getXmlBuddy(String name)
	{
		StringBuffer xmlBuddy = new StringBuffer();
		String status = checkBuddy(name) ? "1" : "0";
		xmlBuddy.append("<b s='").append(status);
		xmlBuddy.append("'><![CDATA[").append(name).append("]]></b>");
		return xmlBuddy.toString();
	}

	public boolean getCountUpdate()
	{
		return updateUserCount;
	}

	public void setCountUpdate(boolean b)
	{
		updateUserCount = b;
	}

	public int getMaxUsers()
	{
		return maxUsers;
	}

	public void setMaxUsers(int i)
	{
		if (i == 0)
			i = 1;
		maxUsers = i;
	}

	public int getMaxRooms()
	{
		return maxRooms;
	}

	public void setMaxRooms(int i)
	{
		maxRooms = i;
	}

	public void setEmptyNames(boolean b)
	{
		emptyNames = b;
	}

	public boolean getEmptyNames()
	{
		return emptyNames;
	}

	public int getMaxRoomNameLen()
	{
		return maxRoomNameLen;
	}

	public void setMaxRoomNameLen(int maxRoomNameLen)
	{
		this.maxRoomNameLen = maxRoomNameLen;
	}

	public int getMaxUserNameLen()
	{
		return maxUserNameLen;
	}

	public void setMaxUserNameLen(int maxUserNameLen)
	{
		this.maxUserNameLen = maxUserNameLen;
	}

	public boolean isAutoReloadExtensions()
	{
		return autoReloadExtensions;
	}

	public void setAutoReloadExtensions(boolean autoReloadExtensions)
	{
		this.autoReloadExtensions = autoReloadExtensions;
	}
}
