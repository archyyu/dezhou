package com.archy.dezhou.entity;

public class Count
{
	private int playercount;
	private int regcount;
	private int totalcount;
	private int nologincount;
	private String time;
	private int counttime;
	private String activeDegree;
	private String lostDegree;

	private int fiveCount = 0; // 注册玩家5分钟内的在线人数
	private int fifteenCount = 0; // 注册玩家15分钟内的在线人数
	private int thirtyFiveCount = 0; // 注册玩家35分钟内的在线人数
	private int sixtyFiveCount = 0; // 注册玩家65分钟内的在线人数
	private int overCount = 0; // 注册玩家完成新手引导的在线人数

	public int getFiveCount()
	{
		return fiveCount;
	}

	public void setFiveCount(int fiveCount)
	{
		this.fiveCount = fiveCount;
	}

	public int getFifteenCount()
	{
		return fifteenCount;
	}

	public void setFifteenCount(int fifteenCount)
	{
		this.fifteenCount = fifteenCount;
	}

	public int getThirtyFiveCount()
	{
		return thirtyFiveCount;
	}

	public void setThirtyFiveCount(int thirtyFiveCount)
	{
		this.thirtyFiveCount = thirtyFiveCount;
	}

	public int getSixtyFiveCount()
	{
		return sixtyFiveCount;
	}

	public void setSixtyFiveCount(int sixtyFiveCount)
	{
		this.sixtyFiveCount = sixtyFiveCount;
	}

	public int getOverCount()
	{
		return overCount;
	}

	public void setOverCount(int overCount)
	{
		this.overCount = overCount;
	}

	public int getPlayercount()
	{
		return playercount;
	}

	public void setPlayercount(int playercount)
	{
		this.playercount = playercount;
	}

	public int getRegcount()
	{
		return regcount;
	}

	public void setRegcount(int regcount)
	{
		this.regcount = regcount;
	}

	public int getTotalcount()
	{
		return totalcount;
	}

	public void setTotalcount(int totalcount)
	{
		this.totalcount = totalcount;
	}

	public int getNologincount()
	{
		return nologincount;
	}

	public void setNologincount(int nologincount)
	{
		this.nologincount = nologincount;
	}

	public String getTime()
	{
		return time;
	}

	public void setTime(String time)
	{
		this.time = time;
	}

	public String getActiveDegree()
	{
		return totalcount == 0 ? (0 + "%") : 100 * playercount / totalcount
				+ "%";

	}

	public String getLostDegree()
	{
		return totalcount == 0 ? (0 + "%") : 100 * nologincount / totalcount
				+ "%";
	}

	public int getCounttime()
	{
		return counttime;
	}

	public void setCounttime(int counttime)
	{
		this.counttime = counttime;
	}

	public void setActiveDegree(String activeDegree)
	{
		this.activeDegree = activeDegree;
	}

	public void setLostDegree(String lostDegree)
	{
		this.lostDegree = lostDegree;
	}
}
