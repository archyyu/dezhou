package com.archy.dezhou.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;



import com.archy.dezhou.global.ConstList;
import com.archy.dezhou.global.ConstList.PlayerCareerState;
import com.archy.dezhou.global.ConstList.PlayerGameState;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

import com.archy.dezhou.beans.GameState;
import com.archy.dezhou.beans.PlayerState;
import com.archy.dezhou.beans.PukerState;
import com.archy.dezhou.entity.puker.FivePukeItem;
import com.archy.dezhou.entity.puker.PukerHelp;
import com.archy.dezhou.entity.puker.PukerKit;

@Data
public class Player extends User
{

	private int seatId = -1; // 座位号

	private int yourTurn = 0; // 下注标识 1 代表 yes 0 no

	private int tempBet = 0; // 存放用户面前的临时筹码
	
	private int tempGold = 0;
	
	private int totalGambleBet = 0;

	private int chips;
	private int currentBet;
	private boolean hasLooked;
	private boolean isActive;
	private boolean isReady;
	private boolean isAllIn;
	private boolean isDealer;
	private boolean isCurrentTurn;

	// Explicit getters for boolean fields
	public boolean isHasLooked() {
		return hasLooked;
	}

	public boolean isActive() {
		return isActive;
	}

	public boolean isAllIn() {
		return isAllIn;
	}

	public boolean isDealer() {
		return isDealer;
	}

	public boolean isCurrentTurn() {
		return isCurrentTurn;
	}
	private boolean isOnline;
	private String avatarUrl;

	// Explicit getters for fields that might have Lombok issues
	public boolean isOnline() {
		return isOnline;
	}

	public String getAvatarUrl() {
		return avatarUrl;
	}

	private int totalGames;
	private int gamesWon;
	private int gamesLost;
	private int handsPlayed;
	private int handsWon;
	private double winRate;
	private int biggestWin;
	private int biggestLoss;
	private int totalBets;
	private int totalWinnings;

	// Explicit getters for statistics fields
	public int getTotalGames() {
		return totalGames;
	}

	public int getGamesWon() {
		return gamesWon;
	}

	public int getGamesLost() {
		return gamesLost;
	}

	public int getHandsPlayed() {
		return handsPlayed;
	}

	public int getHandsWon() {
		return handsWon;
	}

	public double getWinRate() {
		return winRate;
	}

	public int getBiggestWin() {
		return biggestWin;
	}

	public int getBiggestLoss() {
		return biggestLoss;
	}

	public int getTotalBets() {
		return totalBets;
	}

	public int getTotalWinnings() {
		return totalWinnings;
	}

	private PlayerCareerState playerState = PlayerCareerState.PLAYER_STATE_PLAYER;

	private PlayerGameState gameState = PlayerGameState.PLAYER_STATE_PLAYER;

	@JsonIgnore
	protected Logger log = Logger.getLogger(Player.class.getName());

	private volatile int dropCardNum = 0;
	
	public Player(User user)
	{
		super(user);
	}

	public int getTempBet()
	{
		return tempBet;
	}
	
	public void addTempBet(int bet)
	{
		this.tempBet += bet;
	}
	
	public void clearTempBet()
	{
		this.tempBet = 0;
	}
	
	public void deductTempBet(int bet)
	{
		if(bet <= this.tempBet)
		{
			this.tempBet -= bet;
		}
	}
	
	
	public int getTotalGambleBet()
	{
		return this.totalGambleBet;
	}
	
	public void addTotalGambleBet(int bet)
	{
		if(bet > 0)
		{
			this.totalGambleBet += bet;
		}
	}
	
	public void deductGambleBet(int bet)
	{
		this.totalGambleBet -= bet;
		if(this.totalGambleBet < 0)
		{
			this.totalGambleBet = 0;
		}
	}	
	
	
	public void clearTotalGambleBet()
	{
		this.totalGambleBet = 0;
	}

	public int getYourTurn()
	{
		return yourTurn;
	}

	public void setYourTurn(int yourTurn)
	{
		this.yourTurn = yourTurn;
	}

	public int getSeatId()
	{
		return seatId;
	}

	public void setSeatId(int seatId)
	{
		this.seatId = seatId;
	}

	public PlayerCareerState getPlayerState()
	{
		return playerState;
	}

	public void setPlayerState(PlayerCareerState playerState)
	{
		this.playerState = playerState;
	}

	public PlayerGameState getGameState()
	{
		return gameState;
	}

	public void setGameState(PlayerGameState gameState)
	{
		this.gameState = gameState;
	}

	public int getTempGold()
	{
		return tempGold;
	}

	public void setTempGold(int tempGold)
	{
		this.tempGold = tempGold;
	}
	
	private volatile long yourTurnTimeStramp = 0;
	
	public long getYourTurnTimeStramp()
	{
		return this.yourTurnTimeStramp;
	}
	
	public void updateYourTurnTimeStramp()
	{
		this.yourTurnTimeStramp = System.currentTimeMillis();
	}
	
	public boolean isDropCardExpired(long now)
	{
        return now - this.yourTurnTimeStramp > ConstList.DROPCARDTIMEEXPIRED;
    }
	
	public int secsPassWhenYourTurn()
	{
		return (int)(ConstList.DROPCARDTIMEEXPIRED - (System.currentTimeMillis() - this.yourTurnTimeStramp) )/1000;
	}
	
	
	public void clearPukeInfo()
	{
		this.pkType.clear();
		this.fivePk.clear();
		this.pkLevel = 0;
	}
	
	private List<Puke> pkType = new ArrayList<Puke>();//玩家牌的集合
	
	private List<Puke> fivePk = new ArrayList<Puke>();//最大五张牌
	
	private int pkLevel = 0; //扑克牌的等级
	
	private long pkValue = 0L;
	
	private FivePukeItem maxFivePukeList = null;
	
	public void addPukes(List<Puke> map)
	{
		this.pkType.addAll(map);
	}
	
	public void addPuke(Puke puke)
	{
		this.pkType.add(puke);
	}
	
	public List<Puke> getPkType()
	{
		return this.pkType;
	}
	
	public long getPkValue()
	{
		return this.pkValue;
	}
	
	public List<Puke> getFivePk()
	{
		return this.fivePk;
	}
	
	public int getPkLevel()
	{
		return this.pkLevel;
	}
	
	public Puke getPuke(int index)
	{
		if(index < 0 || index >= this.pkType.size())
		{
			return null;
		}
		return this.pkType.get(index);
	}

	public List<Puke> getOwnPukers() {
		if (this.pkType.size() < 2) {
			return new ArrayList<>();
		}
		return this.pkType.subList(0, 2);
	}
	
	public void setPkLevelByPkType(PukerHelp pukerHelp)
	{
		this.maxFivePukeList = pukerHelp.getMaxFive(this.pkType);
		this.fivePk = this.maxFivePukeList.getList();
		this.pkValue = this.maxFivePukeList.getPkValue();
		this.pkLevel = this.maxFivePukeList.getLevel();
		
	}

	public int getDropCardNum()
	{
		return this.dropCardNum;
	}
	
	public void clearDropCardNum()
	{
		this.dropCardNum = 0;
	}
	
	public void addDropCardNum()
	{
		this.dropCardNum ++;
	}

	public PlayerState toPlayerState() {
		return new PlayerState(this);
	}

	public void notifyClient(PlayerState playerState) {
		//send player client the updated player state
	}

	public void notifyClient(GameState gameState) {
		//send player client the update game state
	}
	
}