package com.yl.httplink;


import java.io.IOException;
import org.apache.log4j.Logger;
import com.yl.Global.UserInfoMemoryCache;
import com.yl.httpLogic.*;

public class WebService
{
	
	private static Logger log = Logger.getLogger(WebService.class);
	
	public static void init() throws IOException
	{
		log.info("WebService Init ........");
		UserInfoMemoryCache.RankList = PlayerManager.RankList();
		
//		if (UserInfoMemoryCache.RankList.size() == 0 || (UserInfoMemoryCache.RankList.get("ts") != null && (System
//						.currentTimeMillis() - Long.parseLong((String) UserInfoMemoryCache.RankList .get("ts")) < 1800)))
//		{
			
//			System.out.println(" RankList=" + UserInfoMemoryCache.RankList );
//		}
		

	}

	public static int endOftheDay(int year, int month)
	{
		if (year % 4 == 0 && month == 2)
		{
			if (year % 400 != 0 && year % 100 == 0)
				return 28;
			else
				return 29;
		}
		else if (month == 2)
			return 28;

		else if (month == 1 || month == 3 || month == 5 || month == 7
				|| month == 8 || month == 10 || month == 12)
			return 31;
		else
			return 30;
	}

	private static boolean isDebug = false;
	
	public static void setDebugMode()
	{
		isDebug = true;
	}

	public static boolean getDebugMode()
	{
		return isDebug;
	}
	
}
