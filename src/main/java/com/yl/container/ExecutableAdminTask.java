package com.yl.container;

public interface ExecutableAdminTask
{

	public abstract void execute();

	public abstract long getExecTime();

	public abstract boolean isLooping();

	public abstract void setExecTime(long l);

	public abstract long getDelay();
}
