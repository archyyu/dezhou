package com.archy.dezhou.container;

public interface ExecutableAdminTask
{

	void execute();

	long getExecTime();

	void setExecTime(long l);

	boolean isLooping();

	long getDelay();
}
