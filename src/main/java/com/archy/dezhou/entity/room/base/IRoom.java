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
	
	int getSBet();
	
	void notifyRoomPlayerButOne(ActionscriptObject aObj, ConstList.MessageType msgType, String uId);
	
	void notifyRoomPlayer(ActionscriptObject aObj, ConstList.MessageType msgType);
	
	int getBBet();
	
	int getRoomId();
	
	String getName();
	
	String getCreator();
	
	int getMaxUsers();
	
	Map<String, RoomVariable> getVariables();
	
	User[] getAllUsers();
	
	int getMinBuy();
	
	int getMaxBuy();
	
	int getUserCount();
	
	int getSpectatorCount();
	
	boolean isGame();
	
	IPukerGame getPokerGame();
	
	void gameOverHandle();
	
	int getSecsPassByTurn();
	
	String getValueByKey(String key);
	
	int howManyUsers();
	
	int howManySpecators();
	
	boolean isUserInSeat(User user);
	
	Player findPlayerByUser(User u);
	
}
