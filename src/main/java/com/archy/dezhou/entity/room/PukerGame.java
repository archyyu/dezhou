package com.archy.dezhou.entity.room;

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

import com.archy.dezhou.global.ConstList;

import jakarta.annotation.Resource;

import com.archy.dezhou.GameCmdException;
import com.archy.dezhou.container.JsonObjectWrapper;
import com.archy.dezhou.entity.HeartTimer;
import com.archy.dezhou.entity.Player;
import com.archy.dezhou.entity.Puke;
import com.archy.dezhou.entity.RoomDB;
import com.archy.dezhou.entity.puker.PukerKit;


public class PukerGame extends GameRoom
{

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
	
	List<Puke> allPukes = new ArrayList<>();

	//扑克牌Map
	List<Puke> fiveSharePk = new ArrayList<Puke>();
	
	Map<Integer,Integer> roundPoolBet = new HashMap<Integer,Integer>();

	private volatile IRoomState roomState = new RoomStateReady();
	
	public PukerGame(RoomDB roomDB)
	{
		super(roomDB);
	}

	public void initGame() {

	}

	public int getWinBySeat(int seatId) {
		if (this.winMap.containsKey(seatId) == false) {
			return 0;
		}
		return this.winMap.get(seatId);
	}
	
	public String getCurrentTurnPlayerId()
	{
		if(this.currentPlayer == null)
		{
			return "0";
		}
		return this.currentPlayer.getUid() + "";
	}

	public void beatHeart(long now)
	{
		log.info("roomName: " + this.getName() + " heartbeat at time: " + System.currentTimeMillis());
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
		this.maxBet = this.getBbet();
		this.resetAllPlayer();
		this.autoSetNextBankerSeat();
		this.autoSetPlayerState();
		this.dispatchPukers();
		
		log.info("roomName: " + this.getName() + " 第 " + this.round + " 回合开始");
		this.settleRoundPlayersOnStart();
		this.settleNextTurnPlayer();
		this.notifyRoomPlayerPokeGameStart();
		this.notifyRoomWhoTurn();
		this.clearPublicPoolBet();
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
	
	
	public void dispatchPukers()
	{
		int totalPuke = 2 * this.getPlayerCount() + 5;
		Integer[] pukeArray = PukerKit.generateRandomNumArray(totalPuke);
		
		for (int j = pukeArray.length - 5; j < pukeArray.length; j++)
		{
			fiveSharePk.add(PukerKit.getPuke(pukeArray[j]));
		}
		
		StringBuilder sb = new StringBuilder();
		for(Puke puke : fiveSharePk)
		{
			sb.append(puke.toString());
		}
		// log.info("roomName: " + this.getName() + " 公共五张牌: " + sb.toString());
		
		int playerCount = this.getPlayerCount();
		int n = 1;
		for(Player entry : this.getPlayers())
		{
			// entry.getValue().addPukes(fiveSharePk);
			entry.addPuke(PukerKit.getPuke(pukeArray[n-1]));
			entry.addPuke(PukerKit.getPuke(pukeArray[n-1 + playerCount]));
			
			log.info("roomName: " + this.getName() +
					" seat: " + entry.getSeatId() +
					" Id: " + entry.getUid() +
					" puke1: " + PukerKit.getPuke(pukeArray[n-1]).toString() +
					" puke2: " + PukerKit.getPuke(pukeArray[n-1 + playerCount]).toString());
			
			n++;
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
		List<Player> players = PukerKit.sortPlayerByPukeMap(this.getPlayers());
		
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
	
	
	public JsonObjectWrapper fiveSharePkToAsob()
	{
		JsonObjectWrapper aso = new JsonObjectWrapper();
		for(int i = 0;i<this.fiveSharePk.size();i++)
		{
			aso.put(i,this.fiveSharePk.get(i).toAobj());
		}
		return aso;
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

	public JsonObjectWrapper toAsObj()
	{
		JsonObjectWrapper response = new JsonObjectWrapper();

		response.put("_cmd",ConstList.CMD_ROOMINFO);
		response.put("ig", this.isGame()?"yes":"no");
		response.putNumber("turn",this.getRoundNum());
		response.putNumber("round",this.getRound());

		response.putNumber("mbet",this.getBbet());
		response.putNumber("sbet",this.getSbet());
		response.putNumber("msid",this.maxSeatId());
		response.putNumber("bsid",this.getBankerSeatId());
		response.putNumber("wt",this.getNextSeatId());

		response.putNumber("minbuy",this.getMinbuy());
		response.putNumber("maxbuy",this.getMaxbuy());

		response.put("chname",this.getShowname());
		response.put("roomName",this.getName());
		response.putNumber("fpb",this.getPoolBet(1));
		response.putNumber("spb",this.getPoolBet(2));
		response.putNumber("tpb",this.getPoolBet(3));
		response.putNumber("fopb",this.getPoolBet(4));

		if(this.isGame())
		{
			response.put("fpk",this.fiveSharePkToAsob());
		}

		JsonObjectWrapper as_plist = this.playersToList();
		

		response.putNumber("bsid",this.getBankerSeatId());
		response.put("plist",as_plist);

		return response;
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
			entry.setPkLevelByPkType();
		}
		
		this.currentPlayer = null;
		this.getPlayerMaxHandPuke();
		this.round = 0;
		this.balanceBet();
		// this.gameOverHandle();
		this.notifyRoomPlayerRoundOver();
		this.notifyRoomPlayerPokeGameOver();

		this.clearLessMoneyPlayer();

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
		JsonObjectWrapper asObj = new JsonObjectWrapper();
		
		JsonObjectWrapper winList = new JsonObjectWrapper();
		for(Map.Entry<Integer, Integer> entry : this.winMap.entrySet())
		{
			Player player = this.getPlayerBySeat(entry.getKey());
			if(player == null)
			{
				continue;
			}
			JsonObjectWrapper winPlayer = new JsonObjectWrapper();
			
			winPlayer.put("rlist",new JsonObjectWrapper());
			winPlayer.putNumber("sid",entry.getKey());
			winPlayer.putNumber("tbet",entry.getValue());
			winPlayer.putNumber("gs",player.getPkLevel());
			winPlayer.put("pkwz",PukerKit.pkPosition(player.getFivePk(), player.getPkType()));
			
			winList.put(entry.getKey(),winPlayer);
		}
		
		asObj.put("wlist",winList);
		asObj.put("blist",new JsonObjectWrapper());
		
		JsonObjectWrapper cmList = new JsonObjectWrapper();
		for(Player entry : this.getPlayers())
		{
			if(entry.getPlayerState() == ConstList.PlayerCareerState.PLAYER_STATE_LEAVE)
			{
				continue;
			}
			cmList.put(entry.getSeatId(),entry.getRmoney() + "");
		}
		asObj.put("cmList",cmList);
		asObj.put("_cmd",ConstList.CMD_DBT); 
		asObj.putBool("normal", isGameNormalOver());
		this.notifyRoomPlayer(asObj, ConstList.MessageType.MESSAGE_NINE);
	}
	
	
	public void notifyRoomPlayerPokeGameStart()
	{
		JsonObjectWrapper response = this.toAsObj();
		response.put("_cmd",ConstList.CMD_SBOT);
		this.notifyRoomPlayer(response, ConstList.MessageType.MESSAGE_NINE);
	}
	
	public void notifyRoomWhoTurn()
	{
		JsonObjectWrapper response = new JsonObjectWrapper();
		
		response.put("_cmd",ConstList.CMD_WHO_TURN);
		response.putNumber("mbet",this.maxBet);
		response.putNumber("wt_sid",currentPlayer.getSeatId());
		response.put("wt_uid",currentPlayer.getUid());
		
		this.notifyRoomPlayer(response, ConstList.MessageType.MESSAGE_NINE);
	}
	
	
	public void turnOverHandle()
	{
		if(this.isGame() == false )
		{
			return ;
		}
		
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

		if (this.round == 2) {
			IntStream.range(0, 3).forEach(i -> {
				log.info("public cards: " + fiveSharePk.get(i).toString());
				this.getPlayers().forEach(player -> {
					player.addPuke(fiveSharePk.get(i));
			});
			});
			
		}
		else if (this.round == 3) {
			log.info("public cards: " + fiveSharePk.get(3).toString());
			this.getPlayers().forEach(player -> {
					player.addPuke(fiveSharePk.get(3));
				});
		}
		else if (this.round == 4) {
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
		JsonObjectWrapper response = new JsonObjectWrapper();
		JsonObjectWrapper uList = new JsonObjectWrapper();
		
		for(Player entry : this.getPlayers())
		{
			JsonObjectWrapper pl = new JsonObjectWrapper();
			pl.putNumber("sid",entry.getSeatId());
			pl.putNumber("pkl",entry.getPkLevel());
			uList.put("sid" + entry.getSeatId(),pl);
		}
		
		response.put("ulist",uList);
		response.put("_cmd",ConstList.CMD_ROUND_OVER);
		response.putNumber("round",this.round);
		response.putNumber("rbet",this.currentRoundBet);
		response.put("gover", this.isGame() ? "no" : "yes");
		
		this.notifyRoomPlayer(response, ConstList.MessageType.MESSAGE_NINE);
	}
	
	
	public JsonObjectWrapper playerLookCard(Player player)
	{
		JsonObjectWrapper response = new JsonObjectWrapper();
		if(this.isYouTurn(player) == false)
		{
			response.put("error","notYourTurn");
			log.error("roomName: " + this.getName() 
					+ " seat : " + player.getSeatId() + " Id: "
					+ player.getUid() + " not your turn ");
			
			return response;
		}
		
		player.setGameState(ConstList.PlayerGameState.GAME_STATE_LOOK_CARD);
		player.setYourTurn(0);
		
		JsonObjectWrapper as_player = player.toAsObj();
		
		response.put("_cmd", ConstList.CMD_LOOK_CARD);
		response.put("user", as_player);
		
		log.info("roomName: " + this.getName() + " seat: " + player.getSeatId() + " Id: " + player.getUid() + " look card " );
		this.notifyRoomPlayerButOne(response, ConstList.MessageType.MESSAGE_NINE,player.getUid());
		this.turnOverHandle();
		return response;
	}
	
	
	public void addPoolBet(int bet)
	{
		this.currentRoundBet += bet;
	}
	
	
	public JsonObjectWrapper playerAddBet(Player player,int bet)
	{
		JsonObjectWrapper response = new JsonObjectWrapper();
		if(this.isYouTurn(player) == false)
		{
			response.put("error","notYourTurn");
			
			log.error("roomName: " + this.getName() 
					+ " seat : " + player.getSeatId() + " Id: "
					+ player.getUid() + " not your turn ");
			
			return response;
		}
		
		player.setGameState(ConstList.PlayerGameState.GAME_STATE_ADD_BET);

		if(bet > player.getRmoney())
		{
			bet = player.getRmoney();
		}
		
		if(bet < 2 * (this.maxBet - player.getTempBet()))
		{
			response.put("error","YouParmsisNotValid");
			
			log.error("roomName: " + this.getName() 
					+ " seat : " + player.getSeatId() + " Id: "
					+ player.getUid() + " add bet error bet: " + bet);
			
			return response;
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
		
		JsonObjectWrapper as_player = player.toAsObj();
		response.put("_cmd",ConstList.CMD_ADD_BET);
		response.put("user",as_player);
		
		log.info("roomName: " + this.getName() + " seat : " + player.getSeatId() + " Id: " + player.getUid() + " add " + bet  );
		this.notifyRoomPlayerButOne(response, ConstList.MessageType.MESSAGE_NINE,player.getUid());
		this.settleRoundPlayersOnAddBet(player);
		this.turnOverHandle();
		return response;
	}

	public JsonObjectWrapper playerFollow(Player player) {
		int bet = this.maxBet - player.getTempBet();
		return this.playerFollowBet(player, bet);
	}

	public JsonObjectWrapper playerCheck(Player player) {
		return this.playerFollowBet(player, 0);
	}

	public JsonObjectWrapper playerRaise(Player player, int bet) {
		return this.playerFollowBet(player, bet);
	}
	
	public JsonObjectWrapper playerFollowBet(Player player,int bet)
	{
		JsonObjectWrapper response = new JsonObjectWrapper();
		if(this.isYouTurn(player) == false)
		{
			response.put("error","notYourTurn");
			
			log.error("roomName: " + this.getName() 
					+ " seat : " + player.getSeatId() + " Id: "
					+ player.getUid() + " not your turn ");
			
			return response;
		}
		
		player.setGameState(ConstList.PlayerGameState.GAME_STATE_FOLLOW_BET);

		if(bet > player.getRmoney())
		{
			bet = player.getRmoney();
		}
		
		if(player.getTempBet() + bet < this.maxBet)
		{
			response.put("error","YouParmsisNotValid");
			log.error("roomName: " + this.getName() 
					+ " seat : " + player.getSeatId() + " Id: "
					+ player.getUid() + " call error,bet: " + bet);
			
			return response;
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
		
		response.put("_cmd",ConstList.CMD_FOLLOW_BET);
		JsonObjectWrapper as_player = player.toAsObj();
		response.put("user",as_player);
		
		log.info("roomName: " + this.getName() + " seat: " + player.getSeatId() + " Id: " + player.getUid() + " call " + bet + " on round:" + this.round);
		this.notifyRoomPlayerButOne(response, ConstList.MessageType.MESSAGE_NINE,player.getUid());
		this.turnOverHandle();
		return response;
	}
	
	
	public JsonObjectWrapper playerDropCard(Player player)
	{
		
		JsonObjectWrapper response = new JsonObjectWrapper();
		if(this.isYouTurn(player) == false)
		{
			response.put("error","notYourTurn");
			
			log.error("roomName: " + this.getName() 
					+ " seat : " + player.getSeatId() + " Id: "
					+ player.getUid() + " not your turn ");
			
			return response;
		}
		
		player.setGameState(ConstList.PlayerGameState.GAME_STATE_DROP_CARD);
		player.setYourTurn(0);
		
		response.put("_cmd",ConstList.CMD_DROP_CARD);
		response.put("user",player.toAsObj());
		
		this.publicPoolBet += player.getTotalGambleBet();
		
		log.info("roomName: " + this.getName() + " seat: " + player.getSeatId() + " Id: " + player.getUid() + " drop card " );
		this.notifyRoomPlayer(response, ConstList.MessageType.MESSAGE_NINE);
		this.turnOverHandle();
		if(this.isGame() && this.isGameOverWhenDropCard())
		{
			this.gameOverHandle();
		}
		
		return response;
	}
	
	
	public JsonObjectWrapper playerAllIn(Player player,int bet)
	{
		JsonObjectWrapper response = new JsonObjectWrapper();
		if(this.isYouTurn(player) == false)
		{
			response.put("error","notYourTurn");
			
			log.error("roomName: " + this.getName() 
					+ " seat : " + player.getSeatId() + " Id: "
					+ player.getUid() + " not your turn ");
			
			return response;
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
		
		response.put("_cmd",ConstList.CMD_ALL_IN);
		response.put("user",player.toAsObj());
		
		log.info("roomName: " + this.getName() + " seat : " + player.getSeatId() + " Id: " + player.getUid() + " all in " + bet );
		this.notifyRoomPlayerButOne(response, ConstList.MessageType.MESSAGE_NINE,player.getUid());
		this.turnOverHandle();
		return response;
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
	public JsonObjectWrapper playerSitDown(int seatId, Player player, int cb) throws GameCmdException {
		return super.playerSitDown(seatId, player, cb);
	}
	
	public boolean playerLeave(Player player)
	{

		if (this.isPlayerSitDown(player.getUid())) {
			super.playerStandUp(player);
		} else {
			this.removePlayerFromSpectaclors(player);
		}

		player.setGameState(ConstList.PlayerGameState.GAME_STATE_LEAVE);
		player.setPlayerState(ConstList.PlayerCareerState.PLAYER_STATE_LEAVE);

		player.addAmoney(player.getRmoney());

		player.setRoomId(-1);
		player.setSeatId(-1);
		
		if(this.isGameOver())
		{
			// this.gameOverHandle();
		}
		
		return true;
	}
	
	public boolean isUserPlaying(Integer uid)
	{	
		if(this.isGame() == false)
		{
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
				beatHeart(now);
				this.timer.setNextTick();
			}
		}

		@Override
		public boolean isGame()
		{
			return true;
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

	}
	
}
