package com.archy.dezhou.container;

import java.util.HashMap;
import java.util.Map;

import com.archy.dezhou.Global.ConstList;
import com.archy.dezhou.httpLogic.ConnectInstance;

public class TimerMessageQuene
{

	public static volatile long totalLinkid = 0;

	public int expireNum = 0;
	public int normalNum = 0;
	public boolean ifSendRoomInfo = false;
	public String[] orderStrings = new String[]
	{ "9", "gd_ri", "rinfo", "6", "gd_sd", "sbot", "start", "wt", "1", "3",
			"4", "5", "2", "7", "106", "look", "follow", "drop", "allin",
			"add", "gd_lc", "gd_fb", "gd_dc", "gd_ai", "gd_ab", "gd_su",
			"rover", "dbt", };

	public int getOrderofCmd(String cmd)
	{
		int rtn = 100;
		for (int i = 0; i < orderStrings.length; i++)
		{
			if (orderStrings[i].equals(cmd))
			{
				rtn = i;
				break;
			}
		}
		return rtn;
	}

	public int getcurBBSObject(Map<Integer, ConnectInstance> messageList)
	{

		int PreReturnConnId = -1;
		int priority = 10;

		long curTime = System.currentTimeMillis();
		for (int i = 0; i < messageList.size(); i++)
		{
			if (messageList.get(i).status == 0 && messageList.get(i).priority < priority)
			{
				if ( messageList.get(i).isExpired(curTime) == false )
				{
					PreReturnConnId = i;
					break;
				}
				else
				{
					messageList.get(i).status = 2;
				}
			}
		}
		return PreReturnConnId;
	}

	public ActionscriptObject getWorldMessage(Map<Integer, ConnectInstance> SendBBstMessageList)
	{
		ActionscriptObject worldMessage = new ActionscriptObject();
		if (SendBBstMessageList == null || SendBBstMessageList.size() == 0)
		{
			return worldMessage;
		}
		else
		{
			long curTime = System.currentTimeMillis();
			
			for (int i = 0; i < SendBBstMessageList.size(); i++)
			{
				String uid = (String) SendBBstMessageList.get(i).aObj.get("uid");
				
				if (uid != null && uid.equals("10000"))
				{
					if (SendBBstMessageList.get(i).isExpired(curTime) == false)
					{
						ActionscriptObject tmpObj = SendBBstMessageList.get(i).aObj;
						ActionscriptObject aObject = new ActionscriptObject();
						aObject.put("ct", tmpObj.get("ts"));
						aObject.put("msg", tmpObj.get("msg"));
						worldMessage.put(worldMessage.size(), aObject);
					}
				}
				else
				{
					continue;
				}
			}
			if (worldMessage.size() > 0)
			{
				return worldMessage;
			}
		}
		return worldMessage;
	}

	public int getcurBetaObject(Map<Integer, ConnectInstance> messageList,
			long preSendTime, String roomkey)
	{
		int PreReturnConnId = -1;
		expireNum = 0;
		normalNum = 0;
		ifSendRoomInfo = false;
		long curTimeStamp = System.currentTimeMillis();
		if (messageList.size() > 0)
		{
			ConnectInstance connectInstance = null;
			long createTime = -1;
			int expireTime = 0;
			byte priority = -1;
			byte status = -1;
			int order = 100;
			if (messageList.size() > 0)
			{
				expireTime = messageList.get(new Integer(0)).expireTime;
			}

			for (int i = 0; i < messageList.size(); i++)
			{
				connectInstance = messageList.get(i);

				if (connectInstance.status == 0
						&& (curTimeStamp - connectInstance.CreateTmeStampe) <= expireTime
						&& connectInstance.CreateTmeStampe >= preSendTime
						&& (connectInstance.roomKey.equals(roomkey) || roomkey
								.equals("")))
				{
					createTime = connectInstance.CreateTmeStampe;
					expireTime = connectInstance.expireTime;
					priority = connectInstance.priority;
					status = connectInstance.status;
					PreReturnConnId = i;
					order = getOrderofCmd(connectInstance._cmd);
					break;
				}
			}

			// 该线程已经过期而且还没有被调用过。
			for (int i = 0; i < messageList.size(); i++)
			{
				connectInstance = messageList.get(i);
				if (connectInstance == null
						|| (connectInstance != null
								&& connectInstance.status >= 1 || (connectInstance.status == 0 && ((curTimeStamp - connectInstance.CreateTmeStampe) > connectInstance.expireTime || connectInstance.CreateTmeStampe < preSendTime))))
				{
					if (connectInstance != null && connectInstance.status == 0)
					{
						expireNum++;
						if (connectInstance.isExpired(curTimeStamp))
						{
						}
						else if (connectInstance.CreateTmeStampe < preSendTime)
						{
						}
						ifSendRoomInfo = true;
					}
					continue;
				}
				else if (connectInstance.status == 0
						&& (curTimeStamp - connectInstance.CreateTmeStampe) <= connectInstance.expireTime
						&& (connectInstance.roomKey.equals(roomkey) || roomkey
								.equals("")))
				{
					// 现在比较没有到过期时间的线程，谁的优先级高，就调用谁。
					if (connectInstance.CreateTmeStampe >= preSendTime)
					{
						normalNum++;
					}

					if (connectInstance.CreateTmeStampe >= preSendTime
							&& ((Math.abs(createTime
									- connectInstance.CreateTmeStampe) <= 500 && getOrderofCmd(connectInstance._cmd) < order) || (createTime - connectInstance.CreateTmeStampe) > 500))
					{

						createTime = connectInstance.CreateTmeStampe;
						expireTime = connectInstance.expireTime;
						priority = connectInstance.priority;
						status = connectInstance.status;
						order = getOrderofCmd(connectInstance._cmd); // 2014-07-06
																		// by ai

						if (createTime == connectInstance.CreateTmeStampe)
						{
							ConstList.config.logger.info("有时间戳相等的情况出现~");
						}
						PreReturnConnId = i;
					}
				}
			}

			// 如果队列空，或全部是过期已执行的队列，则返回-1。
			return PreReturnConnId;
		}
		else
		{	
			return -1;
		}
	}

	public static int reSendMessage(Map<Integer, ConnectInstance> messageList,
			long linkid)
	{
		int rtn = -1;
		long curTimeStamp = System.currentTimeMillis();
		if (messageList.size() > 0)
		{
			for (int i = 0; i < messageList.size(); i++)
			{
				ConnectInstance cueMessage = messageList.get(new Integer(i));
				if (cueMessage.linkid == linkid
						&& cueMessage.isExpired(curTimeStamp) == false)
				{
					rtn = i;
					break;
				}
			}
		}
		return rtn;

	}

	public Map<Integer, ConnectInstance> InsertIntoConnectQuene(
			ConnectInstance NextConnect,
			Map<Integer, ConnectInstance> messageList)
	{
		int rtnSuffix = -1;
		
		messageList = gcMessageList(messageList);
		
		if (messageList.size() > 0)
		{
			for (int i = 0; i < messageList.size(); i++)
			{
				ConnectInstance oneConnectInstance = messageList.get(i);
				if (oneConnectInstance != null && oneConnectInstance.status == 2)
				{
					rtnSuffix = i;
					messageList.remove(i);
					messageList.put(i, NextConnect);
					break;
				}
			}

			// 全满的情况：要么是达到了队列上限，要么还没有不满5个。
			if (rtnSuffix == -1)
			{
				messageList.put(messageList.size(), NextConnect);
				if (NextConnect.expireTime == ConstList.level_3_ExpireTime)
				{
					
				}
				rtnSuffix = (byte) messageList.size();
			}

		}
		else
		{
			messageList.put(0, NextConnect);
			rtnSuffix = 0;
		}
		return messageList;
	}

	public Map<Integer, ConnectInstance> gcMessageList(Map<Integer, ConnectInstance> messageList)
	{
		
		ifSendRoomInfo = false;
		Map<Integer, ConnectInstance> tmpList = new HashMap<Integer, ConnectInstance>();
		long tmpcurTime = System.currentTimeMillis();
		int tmpi = 0;
		for (int j = 0; j < messageList.size(); j++)
		{
			ConnectInstance ci = messageList.get(j);
			if ( ci.isExpired(tmpcurTime) && ci.status != 0)
			{
				ci.status = 2;
				if (ci.status == 0 && (!ifSendRoomInfo))
				{
					ifSendRoomInfo = true;
					tmpi++;
				}
			}
			messageList.put(j, ci);
		}
		
		if (tmpi > 0)
		{
			ConstList.config.logger.warn("目前有" + tmpi + "个过期的队列数据。");
		}

		if (messageList != null && messageList.size() > 5)
		{
			for (int i = 0; i < messageList.size(); i++)
			{
				if (messageList.get(i).status != 2)
				{
					if ( messageList.get(i).isExpired(tmpcurTime) ==  false )
					{
						tmpList.put(tmpList.size(),messageList.get(i));
					}
				}

			}
			messageList.clear();
			messageList = tmpList;
		}
		return messageList;
	}

}
