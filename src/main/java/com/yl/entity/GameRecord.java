package com.yl.entity;

/**
 * 类GameRecord String userId 当前玩家的uid int sid 当前玩家的座位号 int roomId 当前玩家的房间号 int
 * action 表示当前玩家的行为 int round 表示当前玩家处于第几回合 int bet 当前玩家下注的筹码 int turn
 * 表示当前玩家打了多少局
 */
public class GameRecord
{
	/**
	 * 
	 */
	private String userId; // 用户id
	private int sid; // sid
	private int roomId; // 房间id
	private int action; // 玩家游戏动作
	private int round; // 第几回合
	private int bet; // 下注的筹码
	private int turn; // 第几局

	public String getUserId()
	{
		return userId;
	}

	public void setUserId(String userId)
	{
		this.userId = userId;
	}

	public int getRoomId()
	{
		return roomId;
	}

	public void setRoomId(int roomId)
	{
		this.roomId = roomId;
	}

	public int getAction()
	{
		return action;
	}

	public void setAction(int action)
	{
		this.action = action;
	}

	public int getRound()
	{
		return round;
	}

	public void setRound(int round)
	{
		this.round = round;
	}

	public int getBet()
	{
		return bet;
	}

	public void setBet(int bet)
	{
		this.bet = bet;
	}

	public int getTurn()
	{
		return turn;
	}

	public void setTurn(int turn)
	{
		this.turn = turn;
	}

	public int getSid()
	{
		return sid;
	}

	public void setSid(int sid)
	{
		this.sid = sid;
	}
}