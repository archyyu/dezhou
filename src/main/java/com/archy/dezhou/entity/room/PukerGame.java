package com.archy.dezhou.entity.room;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.archy.dezhou.entity.User;
import com.archy.dezhou.global.ConstList;
import com.archy.dezhou.service.RoomService;

import jakarta.annotation.Resource;

import com.archy.dezhou.container.ActionscriptObject;
import com.archy.dezhou.entity.Player;
import com.archy.dezhou.entity.Puke;
import com.archy.dezhou.entity.puker.PukerKit;


public class PukerGame
{
	
	@Resource
	private RoomService	roomService;

	private Logger log = LoggerFactory.getLogger(getClass());

	private GameRoom room = null;

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
	
	//玩家map key:seatid value:Player
	private Map<Integer,Player> playerMap = new TreeMap<Integer, Player>();
	
	private Map<Integer,Integer> winMap = new HashMap<Integer,Integer>();
	
	//玩家list
	private Queue<Player> playerList = new LinkedList<Player>();
	
	//扑克牌Map
	List<Puke> fiveSharePk = new ArrayList<Puke>();
	
	Map<Integer,Integer> roundPoolBet = new HashMap<Integer,Integer>();
	
	public PukerGame(GameRoom room)
	{
		this.room = room;
	}
	
	public String getCurrentTurnPlayerId()
	{
		if(this.currentPlayer == null)
		{
			return "0";
		}
		return this.currentPlayer.getUid() + "";
	}

	public void beatHeart( long now )
	{
		
		if(this.currentPlayer == null)
		{
			
			return ;
		}
		if(this.currentPlayer.isDropCardExpired(now))
		{
			Player tempPlayer = this.currentPlayer;
			log.info("roomName: " + this.room.getName() + " seat: " + this.currentPlayer.getSeatId() + " Id: " + this.currentPlayer.getUid() + " drop card time expired");
			this.currentPlayer.addDropCardNum();
			this.playerDropCard(this.currentPlayer);
			
			if(tempPlayer != null)
			{
				if(tempPlayer.getDropCardNum() >= 2)
				{

                    GameRoom room = this.roomService.getRoom(tempPlayer.getRoomId());
                    if(room != null)
                    {
                        log.warn("roomName: " + this.room.getName() + " seat: " + tempPlayer.getSeatId()
                                + " userId: " + tempPlayer.getUid() + " 两次弃牌，导致被站起 ");
                        room.playerStandUp(tempPlayer.getUid(),true);
                    }
				}
			}
			
		}
		
	}

	
	public void gameStartHandle()
	{
		log.info("roomName: " + this.room.getName() + " 新一轮扑克比赛开始");
		for(int i=1;i<5;i++)
		{
			roundPoolBet.put(i,0);
		}
		
		this.maxBetSeatId = 0;
		this.currentRoundBet = 0;
		this.roundNum ++;
		this.round = 1;
		this.fiveSharePk.clear();
		this.maxBet = this.room.getBbet();
		this.resetAllPlayer();
		this.autoSetNextBankerSeat();
		this.autoSetPlayerState();
		this.dispatchPukers();
		
		for(Map.Entry<Integer,Player> entry : this.playerMap.entrySet())
		{
			entry.getValue().setPkLevelByPkType();
		}
		
		log.info("roomName: " + this.room.getName() + " 第 " + this.round + " 回合开始");
		this.settleRoundPlayersOnStart();
		this.settleNextTurnPlayer();
		this.notifyRoomPlayerPokeGameStart();
		this.notifyRoomWhoTurn();
		this.clearPublicPoolBet();
	}
	
	
	public void clearLessMoneyPlayer()
	{
		
		Map<Integer, Player> tempPlayerMap = new HashMap<Integer,Player>();
		tempPlayerMap.putAll(this.playerMap);
		
		for(Map.Entry<Integer, Player> entry : tempPlayerMap.entrySet())
		{
			if(entry.getValue().getRmoney() < this.room.getBbet())
			{
				this.room.playerStandUp(entry.getValue().getUid(),true);
			}
		}
	}
	
	
	public void resetAllPlayer()
	{
		this.playerMap.clear();
		this.playerMap.putAll( this.room.userListToPlayerMap() );
		for(Map.Entry<Integer, Player> entry : this.playerMap.entrySet())
		{
		    entry.getValue().setPlaying(true);
			entry.getValue().clearTempBet();
			entry.getValue().clearTotalGambleBet();
			entry.getValue().setYourTurn(0);
			entry.getValue().clearPukeInfo();
			entry.getValue().setPlayerState(ConstList.PlayerCareerState.PLAYER_STATE_PLAYER);
			entry.getValue().setGameState(ConstList.PlayerGameState.PLAYER_STATE_PLAYER);
			log.info("roomName: " + this.room.getName() + " 参与玩家: seat: " + entry.getValue().getSeatId() + " Id: " + entry.getValue().getUid());
		}
	}
	
	
	public void autoSetPlayerState()
	{
		List<Player> playerList = new ArrayList<Player>();
		for(Map.Entry<Integer, Player> entry : this.playerMap.entrySet())
		{
			playerList.add(entry.getValue());
		}
		
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
		
		smallBlind.addTempBet(this.maxBet/2);
		smallBlind.addTotalGambleBet(this.maxBet/2);
		
		log.info("roomName: " + this.room.getName() + " big  Blind seat : " + bigBlind.getSeatId()   + " Id: " +   bigBlind.getUid() + " deduct Bet " + this.maxBet);
		log.info("roomName: " + this.room.getName() + " smallBlind seat : " + smallBlind.getSeatId() + " Id: " + smallBlind.getUid() + " deduct Bet " + this.maxBet/2);

        bigBlind.deductRmoney(this.maxBet);
        smallBlind.deductRmoney(this.maxBet/2);
		
	}
	
	public void autoSetNextBankerSeat()
	{
		this.bankSeatId ++;
		while(this.playerMap.containsKey(this.bankSeatId) == false)
		{
			this.bankSeatId ++;
			if(this.bankSeatId > ConstList.MAXNUMPLAYEROFROOM)
			{
				this.bankSeatId = 1;
			}
		}
		
		log.info("roomName: " + this.room.getName() + " banker : seat: " + this.bankSeatId 
				+ " Id: " + this.playerMap.get(this.bankSeatId).getUid());
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
		int totalPuke = 2 * playerMap.size() + 5;
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
		log.info("roomName: " + this.room.getName() + " 公共五张牌: " + sb.toString());
		
		int n = 1;
		for(Map.Entry<Integer, Player> entry : this.playerMap.entrySet())
		{
			entry.getValue().addPukes(fiveSharePk);
			entry.getValue().addPuke(PukerKit.getPuke(pukeArray[n-1]));
			entry.getValue().addPuke(PukerKit.getPuke(pukeArray[n-1 + playerMap.size()]));
			
			log.info("roomName: " + this.room.getName() +
					" seat: " + entry.getValue().getSeatId() +
					" Id: " + entry.getValue().getUid() +
					" puke1: " + PukerKit.getPuke(pukeArray[n-1]).toString() +
					" puke2: " + PukerKit.getPuke(pukeArray[n-1 + playerMap.size()]).toString());
			
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
		List<Player> players = PukerKit.sortPlayerByPukeMap(new ArrayList<Player>(this.playerMap.values()));
		
		log.info("roomName: " + this.room.getName() + " 开始结算");
		
		for(Player player : players)
		{
			log.info("roomName: " + this.room.getName() + " seat: " + player.getSeatId() + " Id: " + player.getUid()
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
			
				log.info("roomName: " + this.room.getName() 
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
	
	
	public ActionscriptObject fiveSharePkToAsob()
	{
		ActionscriptObject aso = new ActionscriptObject();
		for(int i = 0;i<this.fiveSharePk.size();i++)
		{
			aso.put(i,this.fiveSharePk.get(i).toAobj());
		}
		return aso;
	}
	
	
	public void settleRoundPlayersOnStart()
	{
		this.playerList.clear();
		for(Map.Entry<Integer, Player> entry : this.playerMap.entrySet())
		{
			this.playerList.add(entry.getValue());
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
		for(Map.Entry<Integer, Player> entry : this.playerMap.entrySet())
		{
			this.playerList.add(entry.getValue());
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
		for(Map.Entry<Integer, Player> entry : this.playerMap.entrySet())
		{
			if(entry.getValue() == null)
			{
				continue;
			}
			
			this.playerList.add(entry.getValue());
		}
		int seatId = 1;
		int i = this.firstSeatIdOnRound;
		int k = 0;
		while(k < 8)
		{
			if(this.playerMap.containsKey(i))
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
		for(Map.Entry<Integer, Player> entry : this.playerMap.entrySet())
		{
			entry.getValue().setPlaying(false);
			//entry.getValue().addCompletePkNum();
		}
		
		this.currentPlayer = null;
//		this.balanceBet();	
		this.getPlayerMaxHandPuke();
		this.round = 0;
		this.balanceBet();
		this.room.gameOverHandle();
		this.notifyRoomPlayerRoundOver();
		this.notifyRoomPlayerPokeGameOver();

		this.clearLessMoneyPlayer();
	}
	
	public void getPlayerMaxHandPuke() 
	{
		List<Player> players = new ArrayList<Player>(this.playerMap.values());
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
	
	
	public ActionscriptObject toAsObj()
	{
		ActionscriptObject response = new ActionscriptObject();
		
		response.put("ig","yes");
		response.putNumber("turn",this.roundNum);
		response.putNumber("round",this.round);
		
		response.putNumber("fpb",this.roundPoolBet.get(1));
		response.putNumber("spb",this.roundPoolBet.get(2));
		response.putNumber("tpb",this.roundPoolBet.get(3));
		response.putNumber("fopb",this.roundPoolBet.get(4));
		
		response.put("fpk",this.fiveSharePkToAsob());
		
		response.putNumber("mbet",this.maxBet());
		response.putNumber("wt_sid",this.currentPlayer.getSeatId());
		response.put("wt_uid",this.currentPlayer.getUid());
		
		ActionscriptObject as_plist = new ActionscriptObject();
		for(Map.Entry<Integer, Player> entry : this.playerMap.entrySet())
		{
			ActionscriptObject as_player = new ActionscriptObject();
			Player player = entry.getValue();



			
			as_player.put("un",player.getAccount());
			as_player.putNumber("sid",entry.getKey());
			as_player.put("uid",player.getUid());
			
			as_player.putNumber("pkl",player.getPkLevel());
			as_player.putBool("isp",true);
			as_player.putNumber("tb",player.getTempBet());
			as_player.putNumber("yt",player.getYourTurn());
			
			as_player.putNumber("cm",player.getRmoney());
			as_player.putNumber("tm",player.getAMoney());
			as_player.putNumber("gs",player.getGameState().value());
			as_player.putNumber("ps",player.getPlayerState().value());

			
			Puke p = player.getPuke(5);
			if(p != null)
			{
				as_player.put("pk1",p.toAobj());
			}
			
			Puke p2 = player.getPuke(6);
			if(p2 != null)
			{
				as_player.put("pk2",p2.toAobj());
			}
			
			as_plist.put("sid" + entry.getKey(),as_player);
		}
		
		response.putNumber("bsid",this.bankSeatId);
		response.put("plist",as_plist);
		return response;
	}
	
	
	public void notifyRoomPlayerPokeGameOver()
	{
		ActionscriptObject asObj = new ActionscriptObject();
		
		ActionscriptObject winList = new ActionscriptObject();
		for(Map.Entry<Integer, Integer> entry : this.winMap.entrySet())
		{
			Player player = this.playerMap.get(entry.getKey());
			if(player == null)
			{
				continue;
			}
			ActionscriptObject winPlayer = new ActionscriptObject();
			
			winPlayer.put("rlist",new ActionscriptObject());
			winPlayer.putNumber("sid",entry.getKey());
			winPlayer.putNumber("tbet",entry.getValue());
			winPlayer.putNumber("gs",player.getPkLevel());
			winPlayer.put("pkwz",PukerKit.pkPosition(player.getFivePk(), player.getPkType()));
			
			winList.put(entry.getKey(),winPlayer);
		}
		
		asObj.put("wlist",winList);
		asObj.put("blist",new ActionscriptObject());
		
		ActionscriptObject cmList = new ActionscriptObject();
		for(Map.Entry<Integer, Player> entry : this.playerMap.entrySet())
		{
			if(entry.getValue().getPlayerState() == ConstList.PlayerCareerState.PLAYER_STATE_LEAVE)
			{
				continue;
			}
			cmList.put(entry.getValue().getSeatId(),entry.getValue().getRmoney() + "");
		}
		asObj.put("cmList",cmList);
		asObj.put("_cmd",ConstList.CMD_DBT); 
		asObj.putBool("normal", isGameNormalOver());
		this.room.notifyRoomPlayer(asObj, ConstList.MessageType.MESSAGE_NINE);
	}
	
	
	public void notifyRoomPlayerPokeGameStart()
	{
		ActionscriptObject response = this.toAsObj();
		response.put("_cmd",ConstList.CMD_SBOT);
		this.room.notifyRoomPlayer(response, ConstList.MessageType.MESSAGE_NINE);
	}
	
	public void notifyRoomWhoTurn()
	{
		ActionscriptObject response = new ActionscriptObject();
		
		response.put("_cmd",ConstList.CMD_WHO_TURN);
		response.putNumber("mbet",this.maxBet);
		response.putNumber("wt_sid",currentPlayer.getSeatId());
		response.put("wt_uid",currentPlayer.getUid());
		
		this.room.notifyRoomPlayer(response, ConstList.MessageType.MESSAGE_NINE);
	}
	
	
	public void turnOverHandle()
	{
		if(this.room.isGame() == false )
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
		for(Map.Entry<Integer,Player> entry : this.playerMap.entrySet())
		{
			if(entry.getValue().getGameState() == ConstList.PlayerGameState.GAME_STATE_DROP_CARD)
			{
				continue;
			}
			if(entry.getValue().getGameState() == ConstList.PlayerGameState.GAME_STATE_STANDUP)
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
		for(Map.Entry<Integer,Player> entry : this.playerMap.entrySet())
		{
			if(entry.getValue().getGameState() == ConstList.PlayerGameState.GAME_STATE_DROP_CARD)
			{
				continue;
			}
			else if(entry.getValue().getGameState() == ConstList.PlayerGameState.GAME_STATE_STANDUP)
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
		for(Map.Entry<Integer,Player> entry : this.playerMap.entrySet())
		{
			if(entry.getValue().getGameState() == ConstList.PlayerGameState.GAME_STATE_DROP_CARD)
			{
				continue;
			}
			else if(entry.getValue().getGameState() == ConstList.PlayerGameState.GAME_STATE_ALL_END)
			{
				continue;
			}
			else if(entry.getValue().getGameState() == ConstList.PlayerGameState.GAME_STATE_STANDUP)
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
        return this.playerMap.containsKey(player.getSeatId()) != false;
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
		
		for(Map.Entry<Integer, Player> entry : this.playerMap.entrySet())
		{
			entry.getValue().clearTempBet();
		}
		
		this.round ++;
		this.currentRoundBet = 0;
		this.maxBet = 0;
		log.info("roomName: " + this.room.getName() + " 第  " + this.round + " 回合开始");
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
		ActionscriptObject response = new ActionscriptObject();
		ActionscriptObject uList = new ActionscriptObject();
		
		for(Map.Entry<Integer, Player> entry : this.playerMap.entrySet())
		{
			ActionscriptObject pl = new ActionscriptObject();
			pl.putNumber("sid",entry.getKey());
			pl.putNumber("pkl",entry.getValue().getPkLevel());
			uList.put("sid" + entry.getKey(),pl);
		}
		
		response.put("ulist",uList);
		response.put("_cmd",ConstList.CMD_ROUND_OVER);
		response.putNumber("round",this.round);
		response.putNumber("rbet",this.currentRoundBet);
		response.put("gover", this.room.isGame() ? "no" : "yes");
		
		this.room.notifyRoomPlayer(response, ConstList.MessageType.MESSAGE_NINE);
	}
	
	
	public ActionscriptObject playerLookCard(Player player)
	{
		ActionscriptObject response = new ActionscriptObject();
		if(this.isYouTurn(player) == false)
		{
			response.put("error","notYourTurn");
			log.error("roomName: " + this.room.getName() 
					+ " seat : " + player.getSeatId() + " Id: "
					+ player.getUid() + " not your turn ");
			
			return response;
		}
		
		player.setGameState(ConstList.PlayerGameState.GAME_STATE_LOOK_CARD);
		player.setYourTurn(0);
		
		ActionscriptObject as_player = player.toAsObj();
		
		response.put("_cmd", ConstList.CMD_LOOK_CARD);
		response.put("user", as_player);
		
		log.info("roomName: " + this.room.getName() + " seat: " + player.getSeatId() + " Id: " + player.getUid() + " look card " );
		this.room.notifyRoomPlayerButOne(response, ConstList.MessageType.MESSAGE_NINE,player.getUid());
		this.turnOverHandle();
		return response;
	}
	
	
	public void addPoolBet(int bet)
	{
		this.currentRoundBet += bet;
	}
	
	
	public ActionscriptObject playerAddBet(Player player,int bet)
	{
		ActionscriptObject response = new ActionscriptObject();
		if(this.isYouTurn(player) == false)
		{
			response.put("error","notYourTurn");
			
			log.error("roomName: " + this.room.getName() 
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
			
			log.error("roomName: " + this.room.getName() 
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
		
		ActionscriptObject as_player = player.toAsObj();
		response.put("_cmd",ConstList.CMD_ADD_BET);
		response.put("user",as_player);
		
		log.info("roomName: " + this.room.getName() + " seat : " + player.getSeatId() + " Id: " + player.getUid() + " add " + bet  );
		this.room.notifyRoomPlayerButOne(response, ConstList.MessageType.MESSAGE_NINE,player.getUid());
		this.settleRoundPlayersOnAddBet(player);
		this.turnOverHandle();
		return response;
	}
	
	
	public ActionscriptObject playerFollowBet(Player player,int bet)
	{
		ActionscriptObject response = new ActionscriptObject();
		if(this.isYouTurn(player) == false)
		{
			response.put("error","notYourTurn");
			
			log.error("roomName: " + this.room.getName() 
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
			log.error("roomName: " + this.room.getName() 
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
		ActionscriptObject as_player = player.toAsObj();
		response.put("user",as_player);
		
		log.info("roomName: " + this.room.getName() + " seat: " + player.getSeatId() + " Id: " + player.getUid() + " call " + bet );
		this.room.notifyRoomPlayerButOne(response, ConstList.MessageType.MESSAGE_NINE,player.getUid());
		this.turnOverHandle();
		return response;
	}
	
	
	public ActionscriptObject playerDropCard(Player player)
	{
		
		ActionscriptObject response = new ActionscriptObject();
		if(this.isYouTurn(player) == false)
		{
			response.put("error","notYourTurn");
			
			log.error("roomName: " + this.room.getName() 
					+ " seat : " + player.getSeatId() + " Id: "
					+ player.getUid() + " not your turn ");
			
			return response;
		}
		
		player.setGameState(ConstList.PlayerGameState.GAME_STATE_DROP_CARD);
		player.setYourTurn(0);
		
		response.put("_cmd",ConstList.CMD_DROP_CARD);
		response.put("user",player.toAsObj());
		
		this.publicPoolBet += player.getTotalGambleBet();
		
		log.info("roomName: " + this.room.getName() + " seat: " + player.getSeatId() + " Id: " + player.getUid() + " drop card " );
		this.room.notifyRoomPlayer(response, ConstList.MessageType.MESSAGE_NINE);
		this.turnOverHandle();
		if(this.room.isGame() && this.isGameOverWhenDropCard())
		{
			this.gameOverHandle();
		}
		
		return response;
	}
	
	
	public ActionscriptObject playerAllIn(Player player,int bet)
	{
		ActionscriptObject response = new ActionscriptObject();
		if(this.isYouTurn(player) == false)
		{
			response.put("error","notYourTurn");
			
			log.error("roomName: " + this.room.getName() 
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
		
		log.info("roomName: " + this.room.getName() + " seat : " + player.getSeatId() + " Id: " + player.getUid() + " all in " + bet );
		this.room.notifyRoomPlayerButOne(response, ConstList.MessageType.MESSAGE_NINE,player.getUid());
		this.turnOverHandle();
		return response;
	}
	
	
	public ActionscriptObject playerStandUp(Player player)
	{
		if(player == null)
		{
			return null;
		}
		
		if(this.playerMap.containsKey(player.getSeatId()) == false)
		{
			return null;
		}
		
		ActionscriptObject response = new ActionscriptObject();

		player.setGameState(ConstList.PlayerGameState.GAME_STATE_STANDUP);
		player.setPlayerState(ConstList.PlayerCareerState.PLAYER_STATE_LEAVE);

		player.addAmoney(player.getRmoney());
		player.clearRoomMoney();
		
		ActionscriptObject as_player = player.toAsObj();
		as_player.putBool("isp",false);
		as_player.putBool("ip",true);
		
		response.put("_cmd",ConstList.CMD_STANDUP);
		response.put("user",as_player);
		
		this.room.notifyRoomPlayer(response, ConstList.MessageType.MESSAGE_NINE);
		
		if(this.isGameOver())
		{
			this.gameOverHandle();
		}
		
		return response;
	}
	
	
	public boolean isYouTurn(Player player)
	{
		if(this.currentPlayer == null || player == null)
		{
			return false;
		}

        return player.getUid().equals(this.currentPlayer.getUid());

    }
	
	
	public ActionscriptObject playerLeave(Player player)
	{
		this.playerMap.remove(player.getSeatId());
		ActionscriptObject response = new ActionscriptObject();

		player.setGameState(ConstList.PlayerGameState.GAME_STATE_LEAVE);
		player.setPlayerState(ConstList.PlayerCareerState.PLAYER_STATE_LEAVE);

		player.addAmoney(player.getRmoney());

		player.setRoomId(-1);

		ActionscriptObject as_player = player.toAsObj();
		
		response.put("_cmd",ConstList.CMD_LEAVE);
		response.put("_user",as_player);

		if(this.room.isPlayerSitDown(player.getUid()))
		{
			response.put("flag","sit");
			response.put("user",as_player);
			this.room.notifyRoomPlayerButOne(response, ConstList.MessageType.MESSAGE_NINE,player.getUid());
		}
		else
		{
			response.put("flag","nosit");
		}
		
		if(this.isGameOver())
		{
//			this.gameOverHandle();
		}
		
		return response;
	}
	
	public boolean isUserPlaying(Integer uid)
	{	
		if(this.room.isGame() == false)
		{
			return false;
		}
		
		for(Map.Entry<Integer, Player> entry : this.playerMap.entrySet())
		{
			if(entry.getValue().getUid().equals(uid))
			{
				return true;
			}
		}
		
		return false;
	}
	
	
	public void playerStandup(int seatId)
	{
		Player player = playerMap.get(seatId);
		if(player != null)
		{
			this.publicPoolBet += player.getTotalGambleBet();
			if(this.currentPlayer != null && player.getUid().equals(this.currentPlayer.getUid()))
			{
				this.turnOverHandle();
			}
		}
		
		this.playerMap.remove(seatId);
		return ;
	}
	
	
	public Player findPlayerByUser(User u)
	{
		for(Map.Entry<Integer, Player> entry : this.playerMap.entrySet())
		{
			if(entry.getValue().getUid().equals(u.getUid()))
			{
				return entry.getValue();
			}
		}
		return null;
	}
	
	
	public int getTurn()
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
	
}
