package com.archy.texasholder.container;

public interface ExecutableAdminTask
{

	void execute();

	long getExecTime();

	void setExecTime(long l);

	boolean isLooping();

	long getDelay();
}
