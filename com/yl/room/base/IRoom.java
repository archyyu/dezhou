package com.yl.room.base;

import com.yl.Global.ConstList.MessageType;
import com.yl.container.ActionscriptObject;
import com.yl.container.RoomVariable;
import com.yl.container.User;
import com.yl.vo.Player;

import java.util.Map;


public interface IRoom
{
	
	public int userJoin(User user);
	
	public ActionscriptObject userStandUp(User u,boolean notifyMySelf);
	
	public int userLeave(User user);
	
	public void beatHeart(long now);
	
	public boolean isPlayerSitDown(String uId);
	
	public ActionscriptObject userSitDown(int seatId,User u,int cb);
	
	public boolean isUserSitdown(User user);
	
	public boolean isUserInRoom(User user);
	
	public Map<Integer,Player> userListToPlayerMap();
	
	public ActionscriptObject toAsObj();
	
	public int getSBet();
	
	public void notifyRoomPlayerButOne(ActionscriptObject aObj,MessageType msgType,String uId);
	
	public void notifyRoomPlayer(ActionscriptObject aObj, MessageType msgType);
	
	public int getBBet();
	
	public int getRoomId();
	
	public String getName();
	
	public String getCreator();
	
	public int getMaxUsers();
	
	public Map<String, RoomVariable> getVariables();
	
	public User[] getAllUsers();
	
	public int getMinBuy();
	
	public int getMaxBuy();
	
	public int getUserCount();
	
	public int getSpectatorCount();
	
	public boolean isGame();
	
	public IPukerGame getPokerGame();
	
	public void gameOverHandle();
	
	public int getSecsPassByTurn();
	
	public String getValueByKey(String key);
	
	public int howManyUsers();
	
	public int howManySpecators();
	
	public boolean isUserInSeat(User user);
	
	public Player findPlayerByUser(User u);
	
}
