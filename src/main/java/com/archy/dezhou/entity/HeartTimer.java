package com.archy.dezhou.entity;

public class HeartTimer {
    
    private long DestTick = 0;
	
	private int Interval = 0;
	
	public HeartTimer(final int interval)
	{
		Interval = interval;
		DestTick += System.currentTimeMillis() + Interval;
	}
	
	public boolean Check(final long nowTick)
	{
		return nowTick >= DestTick;
	}
	
	public int secsFromNowToDest(final long now)
	{
		return (int)((DestTick - now)/1000);
	}
	
	public void setNextTick()
	{
		DestTick += Interval;
	}
	
	public void setNextTick(final int interval)
	{
		this.Interval = interval; 
		DestTick += Interval;
	}
	
	public void setInterval(int interval)
	{
		this.Interval = interval;
	}
}
