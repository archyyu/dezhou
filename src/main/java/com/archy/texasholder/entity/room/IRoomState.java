package com.archy.texasholder.entity.room;

public interface IRoomState
{
	void beatHear(long now);
	
	boolean isGame();

	String getName();
}
