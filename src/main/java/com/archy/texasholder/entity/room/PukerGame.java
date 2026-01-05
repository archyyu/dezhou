package com.archy.texasholder.entity.room;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.LinkedList;
import java.util.Map;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.archy.texasholder.global.ConstList;
import com.archy.texasholder.service.WebSocketService;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.archy.texasholder.GameCmdException;
import com.archy.texasholder.beans.GameState;
import com.archy.texasholder.entity.HeartTimer;
import com.archy.texasholder.entity.Player;
import com.archy.texasholder.entity.Puke;
import com.archy.texasholder.entity.RoomDB;
import com.archy.texasholder.entity.puker.PukerHelp;
import com.archy.texasholder.entity.puker.PukerKit;


public class PukerGame extends GameRoom
{
	@JsonIgnore
	private Logger log = LoggerFactory.getLogger(getClass());

	//当前的庄家座位号
	private int bankSeatId = 0;
	
	private int firstSeatIdOnRound = 0;
	
	//第几局
	private int roundNum = 0;
	
	//第几轮
	private volatile int round = 0;
	
	//最大筹码数
	private int maxBet = 0;
	
	private int maxBetSeatId = 0;
	
	//当前筹码数
	private int currentRoundBet = 0;
	
	private int publicPoolBet = 0;
	
	//当前出手玩家
	private volatile Player currentPlayer = null;
	
	private Map<Integer,Integer> winMap = new HashMap<Integer,Integer>();
	
	//玩家list
	private Queue<Player> playerList = new LinkedList<Player>();
	
	//扑克牌Map
	List<Puke> fiveSharePk = new ArrayList<Puke>();
	
	Map<Integer,Integer> roundPoolBet = new HashMap<Integer,Integer>();

	private volatile IRoomState roomState = new RoomStateReady();

	private PukerKit pukerKit;

	private PukerHelp pukerHelp;

	private WebSocketService webSocketService;
	
	public PukerGame(RoomDB roomDB, WebSocketService webSocketService, PukerHelp pukerHelp)
	{
		super(roomDB);
		this.webSocketService = webSocketService;
		this.pukerHelp = pukerHelp;
		this.pukerKit = new PukerKit();
	}

	public int getWinBySeat(int seatId) {
		if (this.winMap.containsKey(seatId) == false) {
			return 0;
		}
		return this.winMap.get(seatId);
	}

	public Map<Integer,Integer> getWinMap() {
		return this.winMap;
	}

	public void beatHeart(long now)
	{
		// log.info("roomName: " + this.getName() + " heartbeat at time: " + System.currentTimeMillis());
		now = System.currentTimeMillis();
		List<Player> users = this.getPlayers();
		for(Player user : users)
		{
			if(user.isStandUpExpired(now))
			{
				log.warn("roomName: " + this.getName() + " at time: " + System.currentTimeMillis() + " user " + user.getUid() + " standUp expired");
				this.playerStandUp(user);
			}
		}

		users.clear();
		users.addAll(this.getSpectatorList());
		for(Player user : users)
		{
			if(user.isLeaveExpired(now))
			{
				if(this.isPlayerSitDown(user.getUid()))
				{
					continue;
				}
				log.warn("roomName: " + this.getName() + " at time: " + System.currentTimeMillis() + " user " + user.getUid() + " leave room expired");
				this.playerLeave(user);
			}
		}

		this.checkTheCurrentPlayer(now);

		this.roomState.beatHear(now);
	}

	public IRoomState getRoomState () {
		return this.roomState;
	}

	public void checkTheCurrentPlayer( long now )
	{
		if(this.currentPlayer == null)
		{
			return ;
		}
		if(this.currentPlayer.isDropCardExpired(now))
		{
			Player tempPlayer = this.currentPlayer;
			log.info("roomName: " + this.getName() + " seat: " + this.currentPlayer.getSeatId() + " Id: " + this.currentPlayer.getUid() + " drop card time expired");
			this.currentPlayer.addDropCardNum();
			this.playerDropCard(this.currentPlayer);
			
			if(tempPlayer != null)
			{
				if(tempPlayer.getDropCardNum() >= 2)
				{
					this.playerStandUp(tempPlayer);
                    // GameRoom room = this.roomService.getRoom(tempPlayer.getRoomid());
                    // if(room != null)
                    // {
                    //     log.warn("roomName: " + this.getName() + " seat: " + tempPlayer.getSeatId()
                    //             + " userId: " + tempPlayer.getUid() + " 两次弃牌，导致被站起 ");
                    //     room.playerStandUp(tempPlayer);
                    // }
				}
			}
			
		}
		
	}

	
	public void gameStartHandle()
	{
		log.info("roomName: " + this.getName() + "第" + (this.roundNum + 1) + "轮扑克比赛开始");
		for(int i=1;i<5;i++) 
		{
			roundPoolBet.put(i,0);
		}
		
		this.maxBetSeatId = 0;
		this.currentRoundBet = 0;
		this.roundNum ++;
		this.round = 1;
		this.fiveSharePk.clear();
		this.winMap.clear();
		this.maxBet = this.getBbet();
		this.resetAllPlayer();
		this.autoSetNextBankerSeat();
		this.autoSetPlayerState();

		this.pukerKit.reSeed();
		this.flopPlayersPukers();
		
		log.info("roomName: " + this.getName() + " 第 " + this.round + " 回合开始");
		this.settleRoundPlayersOnStart();
		this.settleNextTurnPlayer();
		this.notifyRoomPlayerPokeGameStart();
		this.notifyRoomWhoTurn();
		this.clearPublicPoolBet();

		this.webSocketService.sendGameStateUpdate(this);

	}
	
	
	public void clearLessMoneyPlayer()
	{
		
		List<Player> tempPlayerMap = this.getPlayers();
		for(Player entry : tempPlayerMap)
		{
			if(entry.getRmoney() < this.getBbet())
			{
				this.playerStandUp(entry);
			}
		}

	}
	
	
	public void resetAllPlayer()
	{
		for(Player entry : this.getPlayers())
		{
		    entry.setPlaying(true);
			entry.clearTempBet();
			entry.clearTotalGambleBet();
			entry.setYourTurn(0);
			entry.clearPukeInfo();
			entry.setPlayerState(ConstList.PlayerCareerState.PLAYER_STATE_PLAYER);
			entry.setGameState(ConstList.PlayerGameState.PLAYER_STATE_PLAYER);
			log.info("roomName: " + this.getName() + " 参与玩家: seat: " + entry.getSeatId() + " Id: " + entry.getUid());
		}
	}
	
	
	public void autoSetPlayerState()
	{
		List<Player> playerList = getPlayers();
		
		Player player = playerList.get(0);
		while(player.getSeatId() != this.bankSeatId)
		{
			playerList.remove(0);
			playerList.add(player);
			player = playerList.get(0);
		}
		
		Player smallBlind = null;
		Player bigBlind = null;
		
		playerList.get(0).setPlayerState(ConstList.PlayerCareerState.PLAYER_STATE_BANKED);
		playerList.get(0 + 1).setPlayerState(ConstList.PlayerCareerState.PLAYER_STATE_SMALLBLIND);
		this.firstSeatIdOnRound = playerList.get(1).getSeatId();
		
		smallBlind = playerList.get(1);
		if(playerList.size() > 2)
		{
			playerList.get(0 + 2).setPlayerState(ConstList.PlayerCareerState.PLAYER_STATE_BIGBLIND);
			bigBlind = playerList.get(0 + 2);
		}
		else
		{
			playerList.get(0).setPlayerState(ConstList.PlayerCareerState.PLAYER_STATE_SMALLBLIND);
			playerList.get(1).setPlayerState(ConstList.PlayerCareerState.PLAYER_STATE_BIGBLIND);
			bigBlind = playerList.get(1);
			smallBlind = playerList.get(0);
			this.firstSeatIdOnRound = bigBlind.getSeatId();
		}

		bigBlind.addTempBet(this.maxBet);
		bigBlind.addTotalGambleBet(this.maxBet);
        bigBlind.deductRmoney(this.maxBet);
		
		smallBlind.addTempBet(this.maxBet/2);
		smallBlind.addTotalGambleBet(this.maxBet/2);
		smallBlind.deductRmoney(this.maxBet/2);
		
		log.info("roomName: " + this.getName() + " smallBlind seat : " + smallBlind.getSeatId() + " Id: " + smallBlind.getUid() + " deduct Bet " + this.maxBet/2);
		log.info("roomName: " + this.getName() + " big  Blind seat : " + bigBlind.getSeatId()   + " Id: " +   bigBlind.getUid() + " deduct Bet " + this.maxBet);
		
	}
	
	public void autoSetNextBankerSeat()
	{
		this.bankSeatId ++;
		while(this.isSeatTaken(this.bankSeatId) == false)
		{
			this.bankSeatId ++;
			if(this.bankSeatId > ConstList.MAXNUMPLAYEROFROOM)
			{
				this.bankSeatId = 1;
			}
		}
		
		log.info("roomName: " + this.getName() + " banker : seat: " + this.bankSeatId 
				+ " Id: " + this.getPlayerBySeat(this.bankSeatId).getUid());
	}
	
	public Player popNextPlayer()
	{
		this.currentPlayer = this.playerList.poll();
		if(this.currentPlayer == null)
		{
			return null;
		}
		this.currentPlayer.updateYourTurnTimeStramp();
		this.currentPlayer.setYourTurn(1);
		return this.currentPlayer;
	}

	
	public void flop3Pukers() {
		this.dispatchPublicPuker(3);
	}

	public void turnPuker() {
		this.dispatchPublicPuker(1);
	}

	public void riverPuker() {
		this.dispatchPublicPuker(1);
	}

	private void dispatchPublicPuker(int size) {
		List<Integer> pukeArray = this.pukerKit.generateRandomNumArray(size);
		IntStream.range(0, size).forEach( i -> {
			this.fiveSharePk.add(this.pukerHelp.getPuke(pukeArray.get(i)));
		});
	}

	public void flopPlayersPukers() {
		int size = 2 * this.getPlayerCount();
		List<Integer> pukeArray = this.pukerKit.generateRandomNumArray(size);
		List<Player> players = this.getPlayers();
		for(int i=0 ; i < players.size() ; i++) {
			Player player = players.get(i);	
			player.addPuke(this.pukerHelp.getPuke(pukeArray.get(2*i + 0)));
			player.addPuke(this.pukerHelp.getPuke(pukeArray.get(2*i + 1)));
		}
	}

	public int getSecsPassByTurn()
	{
		if(this.currentPlayer == null)
		{
			return 0;
		}
		return this.currentPlayer.secsPassWhenYourTurn();
	}
	
	//结束，结算
	public void balanceBet()
	{
		this.winMap.clear();
		List<Player> players = this.pukerHelp.sortPlayerByPukeMap(this.getPlayers());
		
		log.info("roomName: " + this.getName() + " 开始结算");
		
		for(Player player : players)
		{
			log.info("roomName: " + this.getName() + " seat: " + player.getSeatId() + " Id: " + player.getUid()
					+ " handleValue: " + player.getPkValue()
					+ " gambleValue: " + player.getTotalGambleBet());
		}
		
		for(Iterator<Player> player = players.iterator();player.hasNext();)
		{
			if(player.next().getGameState() == ConstList.PlayerGameState.GAME_STATE_DROP_CARD)
			{
				player.remove();
			}
		}
		
		for(Iterator<Player> player = players.iterator();player.hasNext();)
		{
			if(player.next().getGameState() == ConstList.PlayerGameState.GAME_STATE_STANDUP)
			{
				player.remove();
			}
		}
		
		for(int i=0;i<players.size();i++)
		{
			if(players.get(i).getTotalGambleBet() <= 0)
			{
				continue;
			}
			
			List<Player> sameCardPlayers = new ArrayList<Player>();
			sameCardPlayers.add(players.get(i));
			
			for(int j=i+1;j<players.size();j++)
			{
				if(players.get(i).getPkValue() == players.get(j).getPkValue())
				{
					sameCardPlayers.add(players.get(j));
				}
				else
				{
					break;
				}
			}
			
			int totalBet = 0;
			
			int pBet = players.get(i).getTotalGambleBet();
			for(int j = i; j < players.size();j++)
			{
				int bet = Math.min(pBet,players.get(j).getTotalGambleBet());
				totalBet += bet;
				players.get(j).deductGambleBet(bet);
			}
			
			if(i == 0)
			{
				totalBet += this.publicPoolBet;
			}
			
			for(Player player : sameCardPlayers)
			{
                player.addRmoney(totalBet/sameCardPlayers.size());
			
				log.info("roomName: " + this.getName() 
						+ " 奖励   seat : " + player.getSeatId() 
						+ " Id: " + player.getUid()
						+ " add : " + totalBet/sameCardPlayers.size() );
				
				if(this.winMap.containsKey(player.getSeatId()))
				{
					this.winMap.put(player.getSeatId(), this.winMap.get(player.getSeatId()) + totalBet/sameCardPlayers.size());
				}
				else
				{
					this.winMap.put(player.getSeatId(), totalBet/sameCardPlayers.size());

				}
			}
			
		}
		
		return ;
		
	}
	
	
	public void clearPublicPoolBet()
	{
		this.publicPoolBet = 0;
	}
	
	public void settleRoundPlayersOnStart()
	{
		this.playerList.clear();
		for(Player entry : this.getPlayers())
		{
			this.playerList.add(entry);
		}
		
		Player player = this.playerList.peek();
		while(player.getPlayerState() != ConstList.PlayerCareerState.PLAYER_STATE_BIGBLIND)
		{
			this.playerList.offer(this.playerList.poll());
			player = this.playerList.peek();
		}
		
		this.playerList.offer(this.playerList.poll());
	}
	
	public void settleRoundPlayersOnAddBet(Player player)
	{
		this.playerList.clear();
		for(Player entry : this.getPlayers())
		{
			this.playerList.add(entry);
		}
		
		Player tempPlayer = this.playerList.peek();
		while(!tempPlayer.getUid().equals(player.getUid()))
		{
			this.playerList.offer(this.playerList.poll());
			tempPlayer = this.playerList.peek();
		}
		this.playerList.poll();
	}

	
	public void settleRoundPlayerOnRoundOver()
	{
		this.playerList.clear();
		for(Player entry : this.getPlayers())
		{
			if(entry == null)
			{
				continue;
			}
			
			this.playerList.add(entry);
		}
		int seatId = 1;
		int i = this.firstSeatIdOnRound;
		int k = 0;
		while(k < 8)
		{
			if(this.isSeatTaken(i))
			{
				seatId = i ;
				break;
			}
			
			i ++;
			k ++;
			if(i > 8)
			{
				i = 1;
			}
		}
		
		Player player = this.playerList.peek();
		while(player.getSeatId() != seatId)
		{
			this.playerList.offer(this.playerList.poll());
			player = this.playerList.peek();
		}
	}

	
	public void gameOverHandle()
	{
		for(Player entry : this.getPlayers())
		{
			entry.setPlaying(false);
			entry.setPkLevelByPkType(this.pukerHelp);
		}
		
		this.currentPlayer = null;
		this.getPlayerMaxHandPuke();
		this.round = 0;
		this.balanceBet();
		// this.gameOverHandle();
		this.notifyRoomPlayerRoundOver();
		this.notifyRoomPlayerPokeGameOver();

		this.clearLessMoneyPlayer();
		this.webSocketService.sendGameStateUpdate(this);
		this.roomState = new RoomStateReady(7);
	}
	
	public void getPlayerMaxHandPuke() 
	{
		List<Player> players = this.getPlayers();
		int j = players.size();
		for(int i = 0; i < j; i++)
		{

		    Player player =  players.get(i);

			Map<Integer, Puke> maxHand = new HashMap<Integer, Puke>();

			List<Puke> fivePk = player.getFivePk();
			long fivepkvalue = player.getPkValue();


			int l = fivePk.size();
			for(int k = 0; k < l; k++)
			{
				maxHand.put(k, fivePk.get(k));
			}


			//设置玩家最大手牌
		}

	}
	
	
	public void notifyRoomPlayerPokeGameOver()
	{
		this.notifyRoom();
	}
	
	
	public void notifyRoomPlayerPokeGameStart()
	{
		this.notifyRoom();
	}
	
	public void notifyRoomWhoTurn()
	{
		this.notifyRoom();
	}
	
	
	public void turnOverHandle()
	{
		
		
		if(this.isRoundOver())
		{
			this.roundOverHandle();
			return ;
		}
		
		this.settleNextTurnPlayer();
		if(this.currentPlayer == null)
		{
			this.roundOverHandle();
			return ;
		}
		
		this.notifyRoomWhoTurn();
	}
	
	public boolean isRoundOver()
	{
		return this.playerList.isEmpty();
	}
	
	
	public boolean isGameOverWhenDropCard()
	{
		int size = 0;
		for(Player entry : this.getPlayers())
		{
			if(entry.getGameState() == ConstList.PlayerGameState.GAME_STATE_DROP_CARD)
			{
				continue;
			}
			if(entry.getGameState() == ConstList.PlayerGameState.GAME_STATE_STANDUP)
			{
				continue;
			}
			size ++;
		}

        return size < 2;

    }
	
	public boolean isGameNormalOver()
	{
		int size = 0;
		for(Player entry : this.getPlayers())
		{
			if(entry.getGameState() == ConstList.PlayerGameState.GAME_STATE_DROP_CARD)
			{
				continue;
			}
			else if(entry.getGameState() == ConstList.PlayerGameState.GAME_STATE_STANDUP)
			{
				continue;
			}
			size ++;
		}

        return size >= 2;
    }
	
	
	public boolean isGameOver()
	{
		if(this.round >= 4)
		{
			return true;
		}
		
		int size = 0;
		for(Player entry : this.getPlayers())
		{
			if(entry.getGameState() == ConstList.PlayerGameState.GAME_STATE_DROP_CARD)
			{
				continue;
			}
			else if(entry.getGameState() == ConstList.PlayerGameState.GAME_STATE_ALL_END)
			{
				continue;
			}
			else if(entry.getGameState() == ConstList.PlayerGameState.GAME_STATE_STANDUP)
			{
				continue;
			}
			size ++;
		}

        return size < 2;

    }
	
	public boolean isPlayerTurnValid(Player player)
	{
		if(player == null)
		{
			return false;
		}
		if(player.getGameState() == ConstList.PlayerGameState.GAME_STATE_DROP_CARD)
		{
			return false;
		}
		if(player.getGameState() == ConstList.PlayerGameState.GAME_STATE_ALL_END)
		{
			return false;
		}
		if(player.getGameState() == ConstList.PlayerGameState.GAME_STATE_STANDUP)
		{
			return false;
		}
        return this.isSeatTaken(player.getSeatId()) != false;
    }

	public List<Puke> getPublicPukes() {
		return this.fiveSharePk;
	}
	
	
	public void roundOverHandle()
	{
		if(this.isGameOver())
		{
			this.gameOverHandle();
			return ;
		}
		this.notifyRoomPlayerRoundOver();
		this.roundPoolBet.put(this.round,this.currentRoundBet);
		this.currentRoundBet = 0;
		
		for(Player entry : this.getPlayers())
		{
			entry.clearTempBet();
		}
		
		this.round ++;
		this.currentRoundBet = 0;
		this.maxBet = 0;//this.getBbet();
		log.info("roomName: " + this.getName() + " round " + this.round + " started");

		if (this.round == ConstList.RoundState.Flop.value()) {
			this.flop3Pukers();
			IntStream.range(0, 3).forEach(i -> {

				log.info("public cards: " + fiveSharePk.get(i).toString());
				this.getPlayers().forEach(player -> {
					player.addPuke(fiveSharePk.get(i));
				});
			});
			
		}
		else if (this.round == ConstList.RoundState.Turn.value()) {
			this.turnPuker();
			log.info("public cards: " + fiveSharePk.get(3).toString());
			this.getPlayers().forEach(player -> {
					player.addPuke(fiveSharePk.get(3));
				});
		}
		else if (this.round == ConstList.RoundState.River.value()) {
			this.riverPuker();
			log.info("public cards: " + fiveSharePk.get(4).toString());
			this.getPlayers().forEach(player -> {
					player.addPuke(fiveSharePk.get(4));
				});
		}

		this.settleRoundPlayerOnRoundOver();
		this.settleNextTurnPlayer();
		if(this.currentPlayer == null)
		{
			this.gameOverHandle();
			return ;
		}
		this.notifyRoomWhoTurn();
		return ;
	}
	
	
	public void notifyRoomPlayerRoundOver()
	{
		this.notifyRoom();
	}

	@Override
	public void notifyRoom()
	{
		this.webSocketService.sendGameStateUpdate(this);
	}
	
	
	public boolean playerLookCard(Player player)
	{
		if(this.isYouTurn(player) == false)
		{
			log.error("roomName: " + this.getName() 
					+ " seat : " + player.getSeatId() + " Id: "
					+ player.getUid() + " not your turn ");
			
			return false;
		}
		
		player.setGameState(ConstList.PlayerGameState.GAME_STATE_LOOK_CARD);
		player.setYourTurn(0);
		
		this.turnOverHandle();
		return true;
	}
	
	
	public void addPoolBet(int bet)
	{
		this.currentRoundBet += bet;
	}

	public int getCurrentRoundBet() {
		return this.currentRoundBet;
	}
	
	public int getTotalBet() {
		int result = 0;
		for(Map.Entry<Integer, Integer> entry : this.roundPoolBet.entrySet()) {
			result += entry.getValue();
		}
		return result;
	}
	
	public boolean playerAddBet(Player player,int bet)
	{
		if(this.isYouTurn(player) == false)
		{
			
			log.error("roomName: " + this.getName() 
					+ " seat : " + player.getSeatId() + " Id: "
					+ player.getUid() + " not your turn ");
			
			return false;
		}
		
		player.setGameState(ConstList.PlayerGameState.GAME_STATE_ADD_BET);

		if(bet > player.getRmoney())
		{
			bet = player.getRmoney();
		}
		
		if(bet < 2 * (this.maxBet - player.getTempBet()))
		{
			
			log.error("roomName: " + this.getName() 
					+ " seat : " + player.getSeatId() + " Id: "
					+ player.getUid() + " add bet error bet: " + bet);
			
			return false;
		}
		
		this.addPoolBet(bet);
		player.addTempBet(bet);
		player.addTotalGambleBet(bet);
		player.deductRmoney(bet);
		player.setYourTurn(0);
		
		if(player.getTempBet() > this.maxBet)
		{
			this.maxBet = player.getTempBet();
			this.maxBetSeatId = player.getSeatId();
		}
		
		
		log.info("roomName: " + this.getName() + " seat : " + player.getSeatId() + " Id: " + player.getUid() + " add " + bet  );
		this.settleRoundPlayersOnAddBet(player);
		this.turnOverHandle();
		return true;
	}

	public boolean playerFollow(Player player) {
		int bet = this.maxBet - player.getTempBet();
		return this.playerFollowBet(player, bet);
	}

	public boolean playerCheck(Player player) {
		return this.playerFollowBet(player, 0);
	}

	public boolean playerRaise(Player player, int bet) {
		return this.playerFollowBet(player, bet);
	}
	
	public boolean playerFollowBet(Player player,int bet)
	{
		if(this.isYouTurn(player) == false)
		{			
			log.error("roomName: " + this.getName() 
					+ " seat : " + player.getSeatId() + " Id: "
					+ player.getUid() + " not your turn ");
			
			return false;
		}
		
		player.setGameState(ConstList.PlayerGameState.GAME_STATE_FOLLOW_BET);

		if(bet > player.getRmoney())
		{
			bet = player.getRmoney();
		}
		
		if(player.getTempBet() + bet < this.maxBet)
		{
			log.error("roomName: " + this.getName() 
					+ " seat : " + player.getSeatId() + " Id: "
					+ player.getUid() + " call error,bet: " + bet);
			
			return false;
		}
		
		this.addPoolBet(bet);
		player.addTempBet(bet);
		player.addTotalGambleBet(bet);
		player.deductRmoney(bet);
		player.setYourTurn(0);
		player.setGameState(ConstList.PlayerGameState.GAME_STATE_FOLLOW_BET);
		
		if(player.getTempBet() > this.maxBet)
		{
			this.maxBet = player.getTempBet();
			this.maxBetSeatId = player.getSeatId();
		}
		
		
		log.info("roomName: " + this.getName() + " seat: " + player.getSeatId() + " Id: " + player.getUid() + " call " + bet + " on round:" + this.round);
		// this.notifyRoom(response, ConstList.MessageType.MESSAGE_NINE,player.getUid());
		this.notifyPlayer(player.toPlayerState());
		this.turnOverHandle();
		return true;
	}
	
	public GameState toGameState() {
		return new GameState(this);
	}
	
	public boolean playerDropCard(Player player)
	{
		
		if(this.isYouTurn(player) == false)
		{
			log.error("roomName: " + this.getName() 
					+ " seat : " + player.getSeatId() + " Id: "
					+ player.getUid() + " not your turn ");
			
			return false;
		}
		
		player.setGameState(ConstList.PlayerGameState.GAME_STATE_DROP_CARD);
		player.setYourTurn(0);
		
		this.publicPoolBet += player.getTotalGambleBet();
		
		log.info("roomName: " + this.getName() + " seat: " + player.getSeatId() + " Id: " + player.getUid() + " drop card " );
		// this.notifyRoomPlayer(response, ConstList.MessageType.MESSAGE_NINE);
		this.turnOverHandle();
		if(this.roomState.isGame() && this.isGameOverWhenDropCard())
		{
			this.gameOverHandle();
		}
		
		return true;
	}
	
	
	public boolean playerAllIn(Player player,int bet)
	{
		if(this.isYouTurn(player) == false)
		{
			log.error("roomName: " + this.getName() 
					+ " seat : " + player.getSeatId() + " Id: "
					+ player.getUid() + " not your turn ");
			
			return false;
		}

		if(bet > player.getRmoney())
		{
			bet = player.getRmoney();
		}
		
		this.addPoolBet(bet);
		player.addTempBet(bet);
		player.addTotalGambleBet(bet);
		player.deductRmoney(bet);
		
		player.setGameState(ConstList.PlayerGameState.GAME_STATE_ALL_END);
		player.setYourTurn(0);
		
		if(player.getTempBet() > this.maxBet)
		{
			this.maxBet = player.getTempBet();
			this.maxBetSeatId = player.getSeatId();
			this.settleRoundPlayersOnAddBet(player);
		}
		
		
		log.info("roomName: " + this.getName() + " seat : " + player.getSeatId() + " Id: " + player.getUid() + " all in " + bet );
		// this.notifyRoomPlayerButOne(response, ConstList.MessageType.MESSAGE_NINE,player.getUid());
		this.turnOverHandle();
		return true;
	}
	
	@Override
	public boolean playerStandUp(Player player)
	{
		if(player == null)
		{
			return false;
		}
		
		if (this.isPlayerSeatOn(player.getUid(), player.getSeatId()) == false) {
			return false;
		}

		player.setGameState(ConstList.PlayerGameState.GAME_STATE_STANDUP);
		player.setPlayerState(ConstList.PlayerCareerState.PLAYER_STATE_LEAVE);

		player.addAmoney(player.getRmoney());
		player.clearRoomMoney();

		super.playerStandUp(player);
		player.setSeatId(-1);

		if(this.isGameOver())
		{
			this.gameOverHandle();
		}
		
		return true;
	}
	
	
	public boolean isYouTurn(Player player)
	{
		if(this.currentPlayer == null || player == null)
		{
			return false;
		}

        return player.getUid().equals(this.currentPlayer.getUid());

    }

	@Override
	public boolean playerSitDown(int seatId, Player player, int cb) throws GameCmdException {
		boolean result = super.playerSitDown(seatId, player, cb);
		if(this.roomState.isGame())
		{
			player.setGameState(ConstList.PlayerGameState.PLAYER_STATE_WAIT);
			player.setPlayerState(ConstList.PlayerCareerState.PLAYER_STATE_WAIT);
		}
		else
		{
			player.setGameState(ConstList.PlayerGameState.PLAYER_STATE_PLAYER);
			player.setPlayerState(ConstList.PlayerCareerState.PLAYER_STATE_PLAYER);
		}
		return result;
	}
	
	public boolean playerLeave(Player player)
	{

		log.info("player:" + player.getAccount() + ", uid:" + player.getUid() + ", try to leave the room");

		if (this.isPlayerSitDown(player.getUid())) {
			super.playerStandUp(player);
		} 
		
		this.removePlayerFromSpectaclors(player);
		
		player.setGameState(ConstList.PlayerGameState.GAME_STATE_LEAVE);
		player.setPlayerState(ConstList.PlayerCareerState.PLAYER_STATE_LEAVE);

		player.addAmoney(player.getRmoney());

		player.setRoomId(-1);
		player.setSeatId(-1);
		
		if(this.isGameOver())
		{
			//this.gameOverHandle();
		}

		this.notifyRoom();
		
		return true;
	}
	
	public boolean isUserPlaying(Integer uid)
	{	
		if (this.roomState.isGame() == false) {
			return false;
		}
		
		return super.isUserPlaying(uid);
	}
	
	
	public void playerStandup(Player player)
	{
		super.playerStandUp(player);
		{
			this.publicPoolBet += player.getTotalGambleBet();
			if(this.currentPlayer != null && player.getUid().equals(this.currentPlayer.getUid()))
			{
				this.turnOverHandle();
			}
		}
		
		return ;
	}
	
	public int getRoundNum()
	{
		return this.roundNum;
	}
	
	
	public int getRound()
	{
		return this.round;
	}
	
	
	public int maxBet()
	{
		return this.maxBet;
	}
	
	
	public int maxSeatId()
	{
		return this.maxBetSeatId;
	}
	
	
	public int getPoolBet(int round)
	{
		if(this.roundPoolBet.containsKey(round))
		{
			return this.roundPoolBet.get(round);
		}
		else
		{
			return 0;
		}
	}
	
	
	public int getBankerSeatId()
	{
		return this.bankSeatId;
	}
	
	
	public int getNextSeatId()
	{
		if(currentPlayer == null)
		{
			return 0;
		}
		return this.currentPlayer.getSeatId();
	}
	
	public Player getCurrentPlayer() {
		return this.currentPlayer;
	}
	

	public void settleNextTurnPlayer()
	{
		this.popNextPlayer();
		while(this.isPlayerTurnValid(this.currentPlayer) == false)
		{
			if(this.currentPlayer == null)
			{
				return ;
			}
			this.popNextPlayer();
		}
	}

		// two game states
	private class RoomStateFight implements IRoomState
	{
		
		public RoomStateFight()
		{
			timer = new HeartTimer(1*1000);
		}
		
		@Override
		public void beatHear(long now)
		{
			if( this.timer != null && this.timer.Check(now))
			{
				// beatHeart(now);
				this.timer.setNextTick();
			}
		}

		@Override
		public boolean isGame()
		{
			return true;
		}

		@Override
		public String getName() {
			return "gaming";
		}
		
		private HeartTimer timer = null;
		
	}

	private class RoomStateReady implements IRoomState
	{

		private HeartTimer timer = null;

		public RoomStateReady()
		{
			timer = new HeartTimer(5*1000);
		}

		public RoomStateReady(int secs)
		{
			timer = new HeartTimer( secs * 1000 );
		}

		@Override
		public void beatHear(long now)
		{
			if( this.timer != null && this.timer.Check(now))
			{
				if(getPlayerCount() >= 2)
				{
					this.timer = null;
					roomState = new RoomStateFight();
					try
					{
						gameStartHandle();
					}
					catch (Exception e)
					{
						timer = new HeartTimer(3*1000);
						this.timer.setNextTick();
						log.error("roomName: " +  getName() + " start game error",e);
					}
				}
				else
				{
					this.timer.setNextTick();
				}
			}
		}

		@Override
		public boolean isGame()
		{
			return false;
		}

		@Override
		public String getName() {
			return "waiting";
		}

	}
	
}
