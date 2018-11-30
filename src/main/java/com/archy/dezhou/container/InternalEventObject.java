package com.archy.dezhou.container;

import java.util.HashMap;
import java.util.Map;

public class InternalEventObject
{

	public static final String EVENT_LOGIN = "loginRequest";
	public static final String EVENT_LOGOUT = "logOut";
	public static final String EVENT_JOIN = "userJoin";
	public static final String EVENT_USER_EXIT = "userExit";
	public static final String EVENT_USER_LOST = "userLost";
	public static final String EVENT_SPECTATOR_SWITCHED = "spectatorSwitched";
	public static final String EVENT_NEW_ROOM = "newRoom";
	public static final String EVENT_ROOM_LOST = "roomLost";
	public static final String EVENT_PUBLIC_MESSAGE = "pubMsg";
	public static final String EVENT_PRIVATE_MESSAGE = "privMsg";
	public static final String EVENT_FILE_UPLOAD = "fileUpload";
	public static final String EVENT_SERVER_READY = "serverReady";
	public static final String EVENT_PLAYER_SWITCHED = "playerSwitched";
	private String name;
	private Map params;
	private Map objects;

	public InternalEventObject(String evtName)
	{
		name = evtName;
		params = new HashMap();
		objects = new HashMap();
	}

	public String getEventName()
	{
		return name;
	}

	public void addParam(String key, String value)
	{
		params.put(key, value);
	}

	public String getParam(String key)
	{
		return (String) params.get(key);
	}

	public void addObject(String key, Object o)
	{
		objects.put(key, o);
	}

	public Object getObject(String key)
	{
		return objects.get(key);
	}

	public Object[] getTypedObject(String key)
	{
		Object result[] = new Object[2];
		Object o = objects.get(key);
		if (o != null)
		{
			result[0] = o;
			result[1] = result[0].getClass().getName();
		}
		return result;
	}
}