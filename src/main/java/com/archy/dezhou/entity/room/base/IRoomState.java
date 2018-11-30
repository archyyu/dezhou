package com.archy.dezhou.entity.room.base;

public interface IRoomState
{
	void beatHear(long now);
	
	boolean isGame();
}
