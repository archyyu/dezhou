package com.archy.dezhou.entity;

public class Puke
{
	private String tag; // 图片扑克花色的表示（代表的是红桃、黑桃,梅花、方块)
	private int num; // 表示扑克牌面的大

	public Puke(String tag, int num)
	{
		this.num = num;
		this.tag = tag;
	}

	public int getNum()
	{
		return num;
	}

	public void setNum(int num)
	{
		this.num = num;
	}

	public void setTag(String tag)
	{
		this.tag = tag;
	}

	public String getTag()
	{
		return tag;
	}
	
	public String toString()
	{
		return " tag: " + this.tag +" num: " + this.num;
	}
	
	
}