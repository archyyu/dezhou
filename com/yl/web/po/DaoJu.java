package com.yl.web.po;

public class DaoJu
{
	private String type;
	private int ctype = 0;
	private int id = 0;
	private int num;
	private String djName;
	private long buyTimecount; // 购买日的毫秒数
	private long expireTimecount; // 过期日的毫秒数

	private String IsUse;
	private int ExpireDays;
	private long useTimecount; // 购买日的毫秒数
	private int djDbId;

	public String getIsUse()
	{
		return IsUse;
	}

	public void setIsUse(String IsUse)
	{
		this.IsUse = IsUse;
	}

	public int getExpireDays()
	{
		return ExpireDays;
	}

	public void setExpireDays(int ExpireDays)
	{
		this.ExpireDays = ExpireDays;
	}

	public int getDjDbId()
	{
		return djDbId;
	}

	public void setDjDbId(int djDbId)
	{
		this.djDbId = djDbId;
	}

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public long getBuyTimecount()
	{
		return buyTimecount;
	}

	public void setBuyTimecount(long buyTimecount)
	{
		this.buyTimecount = buyTimecount;
	}

	public long getExpireTimecount()
	{
		return expireTimecount;
	}

	public void setExpireTimecount(long expireTimecount)
	{
		this.expireTimecount = expireTimecount;
	}

	public String getName()
	{
		return djName;
	}

	public void setName(String name)
	{
		this.djName = name;
	}

	public int getNum()
	{
		return num;
	}

	public void setNum(int num)
	{
		this.num = num;
	}

	public long getUseTimecount()
	{
		return useTimecount;
	}

	public void setUseTimecount(long useTimecount)
	{
		this.useTimecount = useTimecount;
	}

	public int getCtype()
	{
		return ctype;
	}
	
	public void setCtype(int ctype)
	{
		this.ctype = ctype;
	}
	public int getId()
	{
		return id;
	}
}
