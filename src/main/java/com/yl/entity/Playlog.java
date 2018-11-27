package com.yl.entity;

public class Playlog
{
	private String uid;
	private int seatId; // 用户Id
	private int money; // 赢取筹码数量

	public int getSeatId()
	{
		return seatId;
	}

	public void setSeatId(int seatId)
	{
		this.seatId = seatId;
	}

	public int getMoney()
	{
		return money;
	}

	public void setMoney(int money)
	{
		this.money = money;
	}

	public String getUid()
	{
		return uid;
	}

	public void setUid(String uid)
	{
		this.uid = uid;
	}

}