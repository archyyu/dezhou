package com.archy.dezhou.container;

import java.util.HashMap;
import java.util.Map;

import com.archy.dezhou.Global.ConstList;
import com.archy.dezhou.httpLogic.ConnectInstance;

// test the prosider to getting a msg from a queque 
public class Test
{

	public Map<Integer, ConnectInstance> SendBetMessageList = new HashMap<Integer, ConnectInstance>();
	int expireNum;
	int normalNum;
	boolean ifSendRoomInfo;

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

	public static void main(String[] args)
	{

		Test test = new Test();

		// System.out.println(getValiddate("20120101",0));
		// System.out.println(getValiddate("20120228",1));
		ConnectInstance connect = null;

		long curTimeStamp = System.currentTimeMillis();

		System.out.println("Time:" + curTimeStamp);

		connect = new ConnectInstance();
		connect.status = 0;
		connect.roomKey = "admin";
		connect.priority = (byte) 9;
		connect.CreateTmeStampe = 1404547948711L;// curTimeStamp;
		// aObj.put("ts", connect.CreateTmeStampe+"");
		connect.expireTime = ConstList.level_9_ExpireTime;
		// connect.aObj = aObj;
		connect._cmd = ConstList.CMD_STANDUP;

		test.SendBetMessageList.put(new Integer(0), connect);

		connect = new ConnectInstance();
		connect.status = 0;
		connect.roomKey = "admin";
		connect.priority = (byte) 8;
		connect.CreateTmeStampe = 1404547948726L;// curTimeStamp + 3000;
		// connect.CreateTmeStampe = 1404547948758L + 300;//curTimeStamp + 3000;
		// aObj.put("ts", connect.CreateTmeStampe+"");
		connect.expireTime = ConstList.level_9_ExpireTime;
		// connect.aObj = aObj;
		connect._cmd = "9";

		test.SendBetMessageList.put(new Integer(2), connect);

		connect = new ConnectInstance();
		connect.status = 0;
		connect.roomKey = "admin";
		connect.priority = (byte) 9;
		connect.CreateTmeStampe = 1404547948758L;// curTimeStamp + 3000*2;
		// aObj.put("ts", connect.CreateTmeStampe+"");
		connect.expireTime = ConstList.level_9_ExpireTime;
		// connect.aObj = aObj;
		connect._cmd = ConstList.CMD_SBOT;

		test.SendBetMessageList.put(new Integer(1), connect);

		test.getcurBetaObject(test.SendBetMessageList, 1404547948711L - 500,
				"admin");
	}

	public int getcurBetaObject(Map<Integer, ConnectInstance> messageList,
			long preSendTime, String roomkey)
	{
		int PreReturnConnId = -1;
		expireNum = 0;
		normalNum = 0;
		// ifSendRoomInfo = false;
		long curTimeStamp = 1404547948758L + 1000;// System.currentTimeMillis();
		if (messageList.size() > 0)
		{
			ConnectInstance connectInstance = null;
			long createTime = -1;
			int expireTime = 0;
			byte priority = -1;
			byte status = -1;
			int order = 100;
			if (messageList.size() > 0)
				expireTime = messageList.get(new Integer(0)).expireTime;

			for (int i = 0; i < messageList.size(); i++)
			{
				connectInstance = messageList
						.get(new Integer(i));

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
					if (preSendTime == -1)
						ConstList.config.logger.info("首次获得的顺序号为：" + order);
					break;
				}

			}

			// 该线程已经过期而且还没有被调用过。

			for (int i = 0; i < messageList.size(); i++)
			{
				connectInstance = messageList
						.get(new Integer(i));
				if (connectInstance == null
						|| (connectInstance != null
								&& connectInstance.status >= 1 || (connectInstance.status == 0 && ((curTimeStamp - connectInstance.CreateTmeStampe) > connectInstance.expireTime || connectInstance.CreateTmeStampe < preSendTime))))
				{
					if (connectInstance != null && connectInstance.status == 0)
					{
						expireNum++;
						if ((curTimeStamp - connectInstance.CreateTmeStampe) > connectInstance.expireTime)
							ConstList.config.logger
									.warn("因为过了系统过期时间，设置过期标记为true,expireNum="
											+ expireNum);
						else if (connectInstance.CreateTmeStampe < preSendTime)
							ConstList.config.logger
									.warn("因为ts标记绕过了未抓取的队列数据，使牌局紊乱，因此设置过期标记为true,expireNum="
											+ expireNum);
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

					long ltemp = createTime - connectInstance.CreateTmeStampe;
					if (Math.abs(createTime - connectInstance.CreateTmeStampe) <= 500
							&& getOrderofCmd(connectInstance._cmd) < order)
					{
						int temp = 0;
						temp = 1;
					}
					if (connectInstance.CreateTmeStampe >= preSendTime
							&& ((Math.abs(createTime
									- connectInstance.CreateTmeStampe) <= 500 && getOrderofCmd(connectInstance._cmd) < order) || (createTime - connectInstance.CreateTmeStampe) > 500))
					{

						order = getOrderofCmd(connectInstance._cmd);
						createTime = connectInstance.CreateTmeStampe;
						expireTime = connectInstance.expireTime;
						priority = connectInstance.priority;
						status = connectInstance.status;
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
			return -1;
	}
}
