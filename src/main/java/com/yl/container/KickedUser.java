package com.yl.container;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.logging.Logger;

import com.yl.Global.ConstList;

public class KickedUser
{

	public KickedUser(String ipAddr)
	{
		this.ipAddr = ipAddr;
		kicks = new LinkedList();
	}

	public String getIp()
	{
		return ipAddr;
	}

	public void addKick()
	{
		synchronized (kicks)
		{
			kicks.add(new Long(System.currentTimeMillis()));
		}
	}

	public void addBadWordKick()
	{
		synchronized (kicks)
		{
			kicks.add(new Long(-System.currentTimeMillis()));
		}
	}

	public long getLastKick()
	{
		return ((Long) kicks.getLast()).longValue();
	}

	public boolean isBannable()
	{
		boolean result;
		int floodCount;
		result = false;
		floodCount = 0;
		int badWordCount = 0;
		LinkedList linkedlist = kicks;
		synchronized (linkedlist)
		{
			for (Iterator i = kicks.iterator(); i.hasNext();)
			{
				long t = ((Long) i.next()).longValue();
				if (t > 0L && ConfigData.ANTIFLOOD_ACTIVE)
				{
					if (t < System.currentTimeMillis()
							- (long) ConfigData.ANTIFLOOD_BAN_AFTER_TIMESPAN)
					{
						i.remove();
						ConstList.config.logger.info((new StringBuilder(
								"Removed flood kick for ip-adr: ")).append(
								getIp()).toString());
					}
					else
					{
						floodCount++;
					}
				}
				else if (t < 0L && ConfigData.BADWORDS_ACTIVE)
				{
					t = -t;
					if (t < System.currentTimeMillis()
							- (long) ConfigData.BADWORDS_BAN_AFTER_TIMESPAN)
					{
						i.remove();
						ConstList.config.logger.info((new StringBuilder(
								"Removed badwords kick for ip-adr: ")).append(
								getIp()).toString());
					}
					else
					{
						badWordCount++;
					}
				}
			}

			if (floodCount >= ConfigData.ANTIFLOOD_BAN_AFTER_KICKS
					&& ConfigData.ANTIFLOOD_ACTIVE)
				result = true;
			if (badWordCount >= ConfigData.BADWORDS_BAN_AFTER_KICKS
					&& ConfigData.BADWORDS_ACTIVE)
				result = true;
			return result;
		}
	}

	private String ipAddr;
	private LinkedList kicks;
}
