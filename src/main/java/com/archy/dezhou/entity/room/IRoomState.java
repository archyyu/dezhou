package com.archy.dezhou.entity.room;

public interface IRoomState
{
	void beatHear(long now);
	
	boolean isGame();

	String getName();
}
