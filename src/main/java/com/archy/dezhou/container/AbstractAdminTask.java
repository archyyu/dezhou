package com.archy.dezhou.container;

public abstract class AbstractAdminTask implements ExecutableAdminTask
{

	public AbstractAdminTask(long execTime, boolean looping)
	{
		this.execTime = execTime;
		this.looping = looping;
		delay = execTime - System.currentTimeMillis();
	}

	public AbstractAdminTask(long execTime, boolean looping, long delay)
	{
		long now = System.currentTimeMillis();
		this.execTime = now + execTime;
		this.looping = looping;
		this.delay = delay;
	}

	public void execute()
	{
	}

	public long getExecTime()
	{
		return execTime;
	}

	public void setExecTime(long t)
	{
		execTime = t;
	}

	public boolean isLooping()
	{
		return looping;
	}

	public long getDelay()
	{
		return delay;
	}

	protected long execTime;
	protected boolean looping;
	protected long delay;
}
