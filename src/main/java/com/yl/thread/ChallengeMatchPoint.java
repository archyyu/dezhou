package com.yl.thread;

import com.yl.db.*;

import java.util.ArrayList;
import java.util.Calendar;

import com.yl.Global.UserInfoMemoryCache;
import com.yl.util.PayFinalValue;
import com.yl.util.Utils;
import com.yl.entity.UserInfo;

public class ChallengeMatchPoint extends java.util.TimerTask
{
	private DbManager db;

	public ChallengeMatchPoint(DbManager db)
	{
		this.db = db;
	}

	@Override
	public void run()
	{
		Calendar cal = Calendar.getInstance();
		int d = cal.get(Calendar.DATE);
		int h = cal.get(Calendar.HOUR_OF_DAY);
		int m = cal.get(Calendar.MINUTE);
		int s = cal.get(Calendar.SECOND);
		if ((11 == h || 19 == h) && m == 30 && s == 0)
		{
			// 返赛点
			UserInfo[] users = UserInfoMemoryCache.toUserInfos();
			for (UserInfo uinfo : users)
			{
				uinfo.setTzMatchPoint(uinfo.getTzMatchPoint()
						+ PayFinalValue.GIVEN_Point_TZ > PayFinalValue.Point_TTZ ? PayFinalValue.Point_TTZ
						: uinfo.getTzMatchPoint()
								+ PayFinalValue.GIVEN_Point_TZ);
				uinfo.setZfMatchPoint(uinfo.getZfMatchPoint()
						+ PayFinalValue.GIVEN_Point_ZF > PayFinalValue.Point_TZF ? PayFinalValue.Point_TZF
						: uinfo.getZfMatchPoint()
								+ PayFinalValue.GIVEN_Point_ZF);
			}
		}
		if (h == 0 && m == 0 && s == 59)
		{
			countMsg();
		}
	}

	/**
	 * 统计
	 */
	public void countMsg()
	{
		String str = Utils.getDateToStr(-1); // 昨天
		int playerCount = 0; // 昨天的玩家人数
		int regCount = 0; // 昨天注册的玩家人数
		int totalCount = 0; // 昨天总的玩家人数 3 天以上没再次登陆
		int noLoginCount = 0; // 3 天以上没再次登陆
		long averTimeCount = 0; // 玩家平均每天的在线时长

		int fiveCount = 0; // 注册玩家5分钟内的在线人数
		int fifteenCount = 0; // 注册玩家15分钟内的在线人数
		int thirtyFiveCount = 0; // 注册玩家35分钟内的在线人数
		int sixtyFiveCount = 0; // 注册玩家65分钟内的在线人数
		int overCount = 0; // 注册玩家完成新手引导的在线人数

		String pcsql = "select count(*) as pc from userinfo where logintime between '"
				+ str + " 00:00:00' and '" + str + " 23:59:59'";
		String rcsql = "select count(*) as rc from userinfo where regtime between '"
				+ str + " 00:00:00' and '" + str + " 23:59:59'";
		String tcsql = "select count(*) as tc,sum(timestampdiff(second,'1970-01-01 00:00:01',counttime))as tt from userinfo";
		String nlcsql = "select count(*) as nlc from userinfo where timestampdiff(hour,logintime,now())>=72";

		String fivesql = "select count(*) as fvc from userinfo where (timestampdiff(second,'1970-01-01 00:00:01',counttime) between 0 and 300) and (regtime between '"
				+ str + " 00:00:00' and '" + str + " 23:59:59')";
		String tensql = "select count(*) as tec from userinfo where (timestampdiff(second,'1970-01-01 00:00:01',counttime) between 301 and 900) and (regtime between '"
				+ str + " 00:00:00' and '" + str + " 23:59:59')";
		String tweentysql = "select count(*) as twc from userinfo where (timestampdiff(second,'1970-01-01 00:00:01',counttime) between 901 and 2100) and (regtime between '"
				+ str + " 00:00:00' and '" + str + " 23:59:59')";
		String thirtysql = "select count(*) as thc from userinfo where (timestampdiff(second,'1970-01-01 00:00:01',counttime) between 2101 and 3900) and (regtime between '"
				+ str + " 00:00:00' and '" + str + " 23:59:59')";
		String oversql = "select count(*) as ovc from userinfo where timestampdiff(second,'1970-01-01 00:00:01',counttime)>3900 and (regtime between '"
				+ str + " 00:00:00' and '" + str + " 23:59:59')";

		ArrayList pclist = db.executeQuery(pcsql);
		ArrayList rclist = db.executeQuery(rcsql);
		ArrayList tclist = db.executeQuery(tcsql);
		ArrayList nlclist = db.executeQuery(nlcsql);

		ArrayList fiveclist = db.executeQuery(fivesql);
		ArrayList tenclist = db.executeQuery(tensql);
		ArrayList twtyclist = db.executeQuery(tweentysql);
		ArrayList thtyclist = db.executeQuery(thirtysql);
		ArrayList overclist = db.executeQuery(oversql);
		if (fiveclist != null && fiveclist.size() > 0)
		{
			fiveCount = Integer.parseInt(((DataRow) fiveclist.get(0))
					.getItem("fvc"));
		}
		if (tenclist != null && tenclist.size() > 0)
		{
			fifteenCount = Integer.parseInt(((DataRow) tenclist.get(0))
					.getItem("tec"));
		}
		if (twtyclist != null && twtyclist.size() > 0)
		{
			thirtyFiveCount = Integer.parseInt(((DataRow) twtyclist.get(0))
					.getItem("twc"));
		}
		if (thtyclist != null && thtyclist.size() > 0)
		{
			sixtyFiveCount = Integer.parseInt(((DataRow) thtyclist.get(0))
					.getItem("thc"));
		}
		if (overclist != null && overclist.size() > 0)
		{
			overCount = Integer.parseInt(((DataRow) overclist.get(0))
					.getItem("ovc"));
		}

		if (pclist != null && pclist.size() > 0)
		{
			playerCount = Integer.parseInt(((DataRow) pclist.get(0))
					.getItem("pc"));
			System.out.println("playerCount==" + playerCount);
		}
		if (rclist != null && rclist.size() > 0)
		{
			regCount = Integer
					.parseInt(((DataRow) rclist.get(0)).getItem("rc"));
			System.out.println("regCount==" + regCount);
		}
		if (tclist != null && tclist.size() > 0)
		{
			totalCount = Integer.parseInt(((DataRow) tclist.get(0))
					.getItem("tc"));
			long totalTime = Long.parseLong(((DataRow) tclist.get(0))
					.getItem("tt"));
			averTimeCount = totalTime / (60 * totalCount);
			System.out.println("totalCount==" + totalCount + "averTimeCount=="
					+ averTimeCount);
		}
		if (nlclist != null && nlclist.size() > 0)
		{
			noLoginCount = Integer.parseInt(((DataRow) nlclist.get(0))
					.getItem("nlc"));
			System.out.println("noLoginCount==" + noLoginCount);
		}
		String sql = "insert into count (time,playercount,regcount,totalcount,nologincount,counttime,fivecount,tencount,tweentycount,thirtycount,overcount) values('"
				+ str
				+ "',"
				+ playerCount
				+ ","
				+ regCount
				+ ","
				+ totalCount
				+ ","
				+ noLoginCount
				+ ","
				+ averTimeCount
				+ ","
				+ fiveCount
				+ ","
				+ fifteenCount
				+ ","
				+ thirtyFiveCount
				+ ","
				+ sixtyFiveCount + "," + overCount + ")";
		boolean b = db.executeCommand(sql);
		System.out.println("count====over" + b);
	}

}
