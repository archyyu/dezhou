
package com.archy.dezhou.room.base;


import com.archy.dezhou.container.ActionscriptObject;
import com.archy.dezhou.container.User;
import com.archy.dezhou.entity.Player;

public interface IPukerGame
{
	void beatHeart(long now);
	
	void gameOverHandle();
	
	void roundOverHandle();
	
	void turnOverHandle();
	
	boolean isRoundOver();
	
	boolean isGameOver();
	
	boolean isGameOverWhenDropCard();
	
	void gameStartHandle() throws Exception;
	
	void clearLessMoneyPlayer();
	
	void resetAllPlayer();
	
	void dispatchPukers();
	
	int getSecsPassByTurn();
	
	void autoSetPlayerState();
	
	void settleRoundPlayerOnRoundOver();
	
	void settleRoundPlayersOnStart();
	
	void settleRoundPlayersOnAddBet(Player player);
	
	Player findPlayerByUser(User u);
	
	void notifyRoomPlayerPokeGameStart();
	
	void notifyRoomPlayerPokeGameOver();
	
	ActionscriptObject toAsObj();
	
	ActionscriptObject playerLookCard(Player player);
	
	ActionscriptObject playerAddBet(Player player, int bet);
	
	ActionscriptObject playerFollowBet(Player player, int bet);
	
	ActionscriptObject playerDropCard(Player player);
	
	ActionscriptObject playerAllIn(Player player, int bet);
	
	ActionscriptObject playerStandUp(Player player);
	
	ActionscriptObject playerLeave(Player player);
	
	boolean isYouTurn(Player player);
	
	boolean isUserPlaying(String uid);
	
	void playerStandup(int seatId);
	
	int getTurn();
	
	int getRound();
	
	int maxBet();
	
	int maxSeatId();
	
	int getPoolBet(int round);
	
	void addPoolBet(int bet);
	
	ActionscriptObject fiveSharePkToAsob();
	
	void clearPublicPoolBet();
	
	int getBankerSeatId();
	
	int getNextSeatId();
	
	void settleNextTurnPlayer();
	
	void notifyRoomPlayerRoundOver();
	
}
