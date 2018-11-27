package com.yl.entity;

public class Prop
{
	private String mailType;
	private String secondaryType;
	private String type;
	private String name;
	private int amount;
	private String currency;
	private String level;
	private String forType;
	private int expireDay;
	private String consumeCode;

	public String getForType()
	{
		return forType;
	}

	public void setForType(String forType)
	{
		this.forType = forType;
	}

	public String getConsumeCode()
	{
		return consumeCode;
	}

	public void setConsumeCode(String consumeCode)
	{
		this.consumeCode = consumeCode;
	}

	public int getExpireDay()
	{
		return expireDay;
	}

	public void setExpireDay(int ExpireDay)
	{
		this.expireDay = ExpireDay;
	}

	public String getLevel()
	{
		return level;
	}

	public void setLevel(String level)
	{
		this.level = level;
	}

	public String getMailType()
	{
		return mailType;
	}

	public void setMailType(String mailType)
	{
		this.mailType = mailType;
	}

	public String getSecondaryType()
	{
		return secondaryType;
	}

	public void setSecondaryType(String secondaryType)
	{
		this.secondaryType = secondaryType;
	}

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public int getAmount()
	{
		return amount;
	}

	public void setAmount(int amount)
	{
		this.amount = amount;
	}

	public String getCurrency()
	{
		return currency;
	}

	public void setCurrency(String currency)
	{
		this.currency = currency;
	}
}
