package com.archy.dezhou.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


import com.archy.dezhou.container.JsonObjectWrapper;
import com.archy.dezhou.global.ConstList;
import com.archy.dezhou.global.ConstList.PlayerCareerState;
import com.archy.dezhou.global.ConstList.PlayerGameState;

import lombok.Data;

import com.archy.dezhou.entity.puker.FivePukeItem;
import com.archy.dezhou.entity.puker.PukerKit;
import com.archy.dezhou.entity.response.GameStateResponse.Card;

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
	
	private List<Card> cards;

	private PlayerCareerState playerState = PlayerCareerState.PLAYER_STATE_PLAYER;

	private PlayerGameState gameState = PlayerGameState.PLAYER_STATE_PLAYER;

	protected Logger log = Logger.getLogger(Player.class.getName());

	
	private volatile int dropCardNum = 0;
	
	public Player()
	{
		
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
	
	private long yourTurnTimeStramp = 0;
	
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
	
	public void setPkLevelByPkType()
	{
		this.maxFivePukeList = PukerKit.getMaxFive(this.pkType);
		this.fivePk = this.maxFivePukeList.getList();
		this.pkValue = this.maxFivePukeList.getPkValue();
		this.pkLevel = this.maxFivePukeList.getLevel();
	}
	
	public JsonObjectWrapper toAsObj()
	{
		JsonObjectWrapper asObj = new JsonObjectWrapper();
		
		asObj.putNumber("sid",this.getSeatId());
		asObj.put("un",this.getAccount());
		asObj.put("uid",this.getUid());
		asObj.putNumber("tb",this.getTempBet());
		asObj.putNumber("yt",this.getYourTurn());
		asObj.putNumber("gs",this.getGameState().value());
		asObj.putNumber("cm",this.getRmoney());
		
		return asObj;
	}

	/**
	 * Convert player to modern JSON object
	 * @return JsonObjectWrapper representation of player
	 */
	public JsonObjectWrapper toJsonObj()
	{
		JsonObjectWrapper jsonObj = new JsonObjectWrapper();
		
		jsonObj.putNumber("sid", this.getSeatId());
		jsonObj.put("un", this.getAccount());
		jsonObj.put("uid", this.getUid());
		jsonObj.putNumber("tb", this.getTempBet());
		jsonObj.putNumber("yt", this.getYourTurn());
		jsonObj.putNumber("gs", this.getGameState().value());
		jsonObj.putNumber("cm", this.getRmoney());
		jsonObj.putNumber("tm", this.getAMoney());
		jsonObj.putNumber("lev", this.getLevel());
		jsonObj.putBool("isp", this.isPlaying());
		
		return jsonObj;
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
	
}