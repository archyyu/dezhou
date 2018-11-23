package com.yl.thread;

import org.apache.log4j.Logger;

public class TimeCountDown implements Runnable
{
	
	private Logger log = Logger.getLogger(getClass());
	private int second;
	public Thread t;

	public TimeCountDown(String type, int second)
	{
		t = new Thread(this);
		t.setName(type);
		this.second = second;
	}

	public void run()
	{
		while (second > 0)
		{
			try
			{
				Thread.sleep(1000);
				second--;
			}
			catch (InterruptedException e)
			{
				log.error("error", e);
			}
		}

	}
}
