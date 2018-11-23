package com.yl.container;

import java.io.Serializable;

public class BannedUser implements Serializable
{

	public BannedUser()
	{
		banDate = System.currentTimeMillis();
	}

	public BannedUser(String name, String ip)
	{
		this();
		this.name = name;
		this.ip = ip;
	}

	public String getName()
	{
		return name;
	}

	public String getIp()
	{
		return ip;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public void setIp(String ip)
	{
		this.ip = ip;
	}

	public long getBanDate()
	{
		return banDate;
	}

	public boolean equals(BannedUser bu)
	{
		if (ip != null)
		{
			if (ip.equals(bu.getIp()))
				return true;
			if (name != null && name.equals(bu.getName()))
				return true;
		}
		return false;
	}

	private static final long serialVersionUID = 1L;
	private String name;
	private String ip;
	private long banDate;
}
