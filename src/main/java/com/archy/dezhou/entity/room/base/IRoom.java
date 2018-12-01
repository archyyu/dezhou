package com.archy.dezhou.entity.room.base;

import com.archy.dezhou.Global.ConstList;
import com.archy.dezhou.container.ActionscriptObject;
import com.archy.dezhou.container.User;
import com.archy.dezhou.entity.Player;
import com.archy.dezhou.container.RoomVariable;

import java.util.Map;


public interface IRoom
{
	
	int userJoin(User user);
	
	ActionscriptObject userStandUp(User u, boolean notifyMySelf);
	
	int userLeave(User user);
	
	void beatHeart(long now);
	
	boolean isPlayerSitDown(String uId);
	
	ActionscriptObject userSitDown(int seatId, User u, int cb);
	
	boolean isUserSitdown(User user);
	
	boolean isUserInRoom(User user);
	
	Map<Integer, Player> userListToPlayerMap();
	
	ActionscriptObject toAsObj();
	
	Integer getSbet();
	
	void notifyRoomPlayerButOne(ActionscriptObject aObj, ConstList.MessageType msgType, String uId);
	
	void notifyRoomPlayer(ActionscriptObject aObj, ConstList.MessageType msgType);
	
	Integer getBbet();
	
	int getRoomId();
	
	String getName();

	String getShowname();
	
	String getCreator();
	
	int getMaxUsers();

	User[] getAllUsers();
	
	Integer getMinbuy();
	
	Integer getMaxbuy();
	
	int getUserCount();
	
	int getSpectatorCount();
	
	boolean isGame();
	
	IPukerGame getPokerGame();
	
	void gameOverHandle();
	
	int getSecsPassByTurn();

	int howManyUsers();
	
	int howManySpecators();
	
	boolean isUserInSeat(User user);
	
	Player findPlayerByUser(User u);
	
}
