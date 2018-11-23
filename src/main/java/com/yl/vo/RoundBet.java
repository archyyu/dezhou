package com.yl.vo;

import java.util.HashMap;

public class RoundBet
{
	private int money = 0; // 记录分堆的
	private HashMap<Integer, String> sidMap = new HashMap<Integer, String>();

	public int getMoney()
	{
		return money;
	}

	public void setMoney(int money)
	{
		this.money = money;
	}

	public HashMap<Integer, String> getSidMap()
	{
		return sidMap;
	}

	public void setSidMap(HashMap<Integer, String> sidMap)
	{
		this.sidMap = sidMap;
	}
}