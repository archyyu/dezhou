
package com.yl.room.base;


import com.yl.container.ActionscriptObject;
import com.yl.container.User;
import com.yl.vo.Player;

public interface IPukerGame
{
	public void beatHeart(long now);
	
	public void gameOverHandle();
	
	public void roundOverHandle();
	
	public void turnOverHandle();
	
	public boolean isRoundOver();
	
	public boolean isGameOver();
	
	public boolean isGameOverWhenDropCard();
	
	public void gameStartHandle() throws Exception;
	
	public void clearLessMoneyPlayer();
	
	public void resetAllPlayer();
	
	public void dispatchPukers();
	
	public int getSecsPassByTurn();
	
	public void autoSetPlayerState();
	
	public void settleRoundPlayerOnRoundOver();
	
	public void settleRoundPlayersOnStart();
	
	public void settleRoundPlayersOnAddBet(Player player);
	
	public Player findPlayerByUser(User u);
	
	public void notifyRoomPlayerPokeGameStart();
	
	public void notifyRoomPlayerPokeGameOver();
	
	public ActionscriptObject toAsObj();
	
	public ActionscriptObject playerLookCard(Player player);
	
	public ActionscriptObject playerAddBet(Player player,int bet);
	
	public ActionscriptObject playerFollowBet(Player player,int bet);
	
	public ActionscriptObject playerDropCard(Player player);
	
	public ActionscriptObject playerAllIn(Player player,int bet);
	
	public ActionscriptObject playerStandUp(Player player);
	
	public ActionscriptObject playerLeave(Player player);
	
	public boolean isYouTurn(Player player);
	
	public boolean isUserPlaying(String uid);
	
	public void playerStandup(int seatId);
	
	public int getTurn();
	
	public int getRound();
	
	public int maxBet();
	
	public int maxSeatId();
	
	public int getPoolBet(int round);
	
	public void addPoolBet(int bet);
	
	public ActionscriptObject fiveSharePkToAsob();
	
	public void clearPublicPoolBet();
	
	public int getBankerSeatId();
	
	public int getNextSeatId();
	
	public void settleNextTurnPlayer();
	
	public void notifyRoomPlayerRoundOver();
	
}
