package com.archy.dezhou.Global;

import java.util.HashMap;

import com.archy.dezhou.container.ActionscriptObject;
import com.archy.dezhou.entity.Campaign;
import com.archy.dezhou.entity.UserInfo;

public class UserInfoMemoryCache
{
	public static UserInfoMemoryCache umcSingleton = new UserInfoMemoryCache();

	private UserInfoMemoryCache()
	{
	}

	synchronized public static UserInfoMemoryCache getInstance()
	{
		return umcSingleton;
	}
	
	/**
	 * 判断用户是否在线
	 **/
	public static boolean isUserInfoOnline(String uid)
	{
		return userinfo.containsKey(uid);
	}
	
	public static UserInfo getUserInfo(String uid)
	{
		return userinfo.get(uid);
	}
	
	public static void removeUserInfo(String uid)
	{
		userinfo.remove(uid);
	}
	
	public static void addUserInfo(UserInfo user)
	{
		userinfo.put(user.getUid(), user);
	}
	
	
	public static UserInfo[] toUserInfos()
	{
		return userinfo.values().toArray(new UserInfo[userinfo.size()]);
	}
	
	public static HashMap<String, UserInfo> getUserInfo()
	{
		return userinfo;
	}

	// 个人信息
	private static HashMap<String, UserInfo> userinfo = new HashMap<String, UserInfo>();
	// 队员信息
	public static HashMap<String, HashMap<String, Campaign>> memberInfo = new HashMap<String, HashMap<String, Campaign>>();
	// 队长信息
	public static HashMap<String, String> leadInfo = new HashMap<String, String>();
	public static ActionscriptObject RankList = new ActionscriptObject();

}
