package com.yl.httpLogic;

import com.yl.container.ActionscriptObject;

public class ConnectInstance
{

	public ConnectInstance()
	{
	}

	public long linkid = 0;
	/**
	 * 优先级,值越低表示优先级越低，值为0-9之间。
	 */
	public byte priority = 0;

	/**
	 * 优先级,值越低表示优先级越低，值为0-9之间。
	 */
	public String roomKey = "";

	/**
	 * 创建该连接实例的时间戳，用来与过期时间进行比对，来确定该线程是否到了必须执行的最晚时间。
	 */
	public long CreateTmeStampe;

	/**
	 * 设置当期连接的过期时间是2秒，过了2秒则必须执行它。
	 */
	public int expireTime = 6000;
	
	
	public boolean isExpired(long timeStr)
	{
		if(timeStr - this.CreateTmeStampe > this.expireTime)
		{
			return true;
		}
		return false;
	}
	

	/**
	 * 当前连接的postdata键值对。
	 * 
	 */
	public ActionscriptObject aObj = new ActionscriptObject();

	/**
	 * 当前连接的命令参数。
	 */
	public String _cmd;

	/**
	 * 该连接的状态。 定义如下： 0：该连接目前还没有调用。 1：该连接实例正在调用。 2：该连接已经被调用过了，可以被别的连接取代了。
	 */
	public byte status;
	
	
	public byte getStatus()
	{
		return this.status;
	}
	
	
	

}
