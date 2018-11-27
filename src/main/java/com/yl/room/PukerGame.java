package com.yl.room;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.yl.Global.ConstList;
import com.yl.Global.ConstList.MessageType;
import com.yl.Global.ConstList.PlayerCareerState;
import com.yl.Global.ConstList.PlayerGameState;
import com.yl.Global.PropValues;
import com.yl.Global.UserInfoMemoryCache;
import com.yl.Global.UserModule;
import com.yl.container.ActionscriptObject;
import com.yl.container.User;
import com.yl.puker.PukerKit;
import com.yl.room.base.IPukerGame;
import com.yl.room.base.IRoom;
import com.yl.util.Utils;
import com.yl.entity.Player;
import com.yl.entity.Puke;
import com.yl.entity.UserInfo;


public class PukerGame implements IPukerGame
{
	
	private Logger log = Logger.getLogger(getClass());
	
	private IRoom room = null;
	
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
	
	public PukerGame(IRoom room)
	{
		this.room = room;
	}
	
	
	@Override
	public void beatHeart( long now )
	{
//		if(this.isGameOver() && this.room.isGame())
//		{
//			this.gameOverHandle();
//		}
		
		if(this.currentPlayer == null)
		{
			
			return ;
		}
		if(this.currentPlayer.isDropCardExpired(now))
		{
			Player tempPlayer = this.currentPlayer;
			log.info("roomName: " + this.room.getName() + " seat: " + this.currentPlayer.getSeatId() + " Id: " + this.currentPlayer.getUserId() + " drop card time expired");
			this.currentPlayer.addDropCardNum();
			this.playerDropCard(this.currentPlayer);
			
			if(tempPlayer != null)
			{
				if(tempPlayer.getDropCardNum() >= 2)
				{
					User user = UserModule.getInstance().getUserByUserId(Integer.parseInt( tempPlayer.getUserId() ));
					if(user != null)
					{
						IRoom room = UserModule.getInstance().getRoom(user.getRoomId());
						if(room != null)
						{
							log.warn("roomName: " + this.room.getName() + " seat: " + tempPlayer.getSeatId() 
									+ " userId: " + user.getUserId() + " 两次弃牌，导致被站起 ");
							room.userStandUp(user,true);
						}
					}
				}
			}
			
		}
		
	}

	@Override
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
		this.maxBet = this.room.getBBet();
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
	
	@Override
	public void clearLessMoneyPlayer()
	{
		
		Map<Integer, Player> tempPlayerMap = new HashMap<Integer,Player>();
		tempPlayerMap.putAll(this.playerMap);
		
		for(Map.Entry<Integer, Player> entry : tempPlayerMap.entrySet())
		{
			User user = UserModule.getInstance().getUserByUserId(Integer.parseInt(entry.getValue().getUserId()));
			UserInfo userInfo = UserInfoMemoryCache.getUserInfo(entry.getValue().getUserId());
			
			if(userInfo.getRmoney() < this.room.getBBet())
			{
				this.room.userStandUp(user,true);
			}
		}
	}
	
	@Override
	public void resetAllPlayer()
	{
		this.playerMap.clear();
		this.playerMap.putAll( this.room.userListToPlayerMap() );
		for(Map.Entry<Integer, Player> entry : this.playerMap.entrySet())
		{
			UserInfo userInfo = UserInfoMemoryCache.getUserInfo(entry.getValue().getUserId());
			if(userInfo != null)
			{
				userInfo.setPlaying(true);
			}
			
			entry.getValue().clearTempBet();
			entry.getValue().clearTotalGambleBet();
			entry.getValue().setYourTurn(0);
			entry.getValue().clearPukeInfo();
			entry.getValue().setPlayerState(PlayerCareerState.PLAYER_STATE_PLAYER);
			entry.getValue().setGameState(PlayerGameState.PLAYER_STATE_PLAYER);
			log.info("roomName: " + this.room.getName() + " 参与玩家: seat: " + entry.getValue().getSeatId() + " Id: " + entry.getValue().getUserId());
		}
	}
	
	@Override
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
		
		playerList.get(0).setPlayerState(PlayerCareerState.PLAYER_STATE_BANKED);
		playerList.get(0 + 1).setPlayerState(PlayerCareerState.PLAYER_STATE_SMALLBLIND);
		this.firstSeatIdOnRound = playerList.get(1).getSeatId();
		
		smallBlind = playerList.get(1);
		if(playerList.size() > 2)
		{
			playerList.get(0 + 2).setPlayerState(PlayerCareerState.PLAYER_STATE_BIGBLIND);
			bigBlind = playerList.get(0 + 2);
		}
		else
		{
			playerList.get(0).setPlayerState(PlayerCareerState.PLAYER_STATE_SMALLBLIND);
			playerList.get(1).setPlayerState(PlayerCareerState.PLAYER_STATE_BIGBLIND);
			bigBlind = playerList.get(1);
			smallBlind = playerList.get(0);
			this.firstSeatIdOnRound = bigBlind.getSeatId();
		}
		bigBlind.addTempBet(this.maxBet);
		bigBlind.addTotalGambleBet(this.maxBet);
		
		smallBlind.addTempBet(this.maxBet/2);
		smallBlind.addTotalGambleBet(this.maxBet/2);
		
		log.info("roomName: " + this.room.getName() + " big  Blind seat : " + bigBlind.getSeatId()   + " Id: " +   bigBlind.getUserId() + " deduct Bet " + this.maxBet);
		log.info("roomName: " + this.room.getName() + " smallBlind seat : " + smallBlind.getSeatId() + " Id: " + smallBlind.getUserId() + " deduct Bet " + this.maxBet/2);
		
		UserInfo uInfo = UserInfoMemoryCache.getUserInfo(bigBlind.getUserId());
		uInfo.deductRmoney(this.maxBet);
		uInfo = UserInfoMemoryCache.getUserInfo(smallBlind.getUserId());
		uInfo.deductRmoney(this.maxBet/2);
		
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
				+ " Id: " + this.playerMap.get(this.bankSeatId).getUserId());
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
	
	@Override
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
					" Id: " + entry.getValue().getUserId() + 
					" puke1: " + PukerKit.getPuke(pukeArray[n-1]).toString() +
					" puke2: " + PukerKit.getPuke(pukeArray[n-1 + playerMap.size()]).toString());
			
			n++;
		}
	}
	
	@Override
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
			log.info("roomName: " + this.room.getName() + " seat: " + player.getSeatId() + " Id: " + player.getUserId()
					+ " handleValue: " + player.getPkValue()
					+ " gambleValue: " + player.getTotalGambleBet());
		}
		
		for(Iterator<Player> player = players.iterator();player.hasNext();)
		{
			if(player.next().getGameState() == PlayerGameState.GAME_STATE_DROP_CARD)
			{
				player.remove();
			}
		}
		
		for(Iterator<Player> player = players.iterator();player.hasNext();)
		{
			if(player.next().getGameState() == PlayerGameState.GAME_STATE_STANDUP)
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
				UserInfo uInfo = UserInfoMemoryCache.getUserInfo(player.getUserId());
				uInfo.addRmoney(totalBet/sameCardPlayers.size());
			
				log.info("roomName: " + this.room.getName() 
						+ " 奖励   seat : " + player.getSeatId() 
						+ " Id: " + player.getUserId() 
						+ " add : " + totalBet/sameCardPlayers.size() );
				
				if(this.winMap.containsKey(player.getSeatId()))
				{
					this.winMap.put(player.getSeatId(), this.winMap.get(player.getSeatId()) + totalBet/sameCardPlayers.size());
				}
				else
				{
					this.winMap.put(player.getSeatId(), totalBet/sameCardPlayers.size());
					uInfo.addWinNum();
				}
			}
			
		}
		
		return ;
		
	}
	
	@Override
	public void clearPublicPoolBet()
	{
		this.publicPoolBet = 0;
	}
	
	@Override
	public ActionscriptObject fiveSharePkToAsob()
	{
		ActionscriptObject aso = new ActionscriptObject();
		for(int i = 0;i<this.fiveSharePk.size();i++)
		{
			aso.put(i,this.fiveSharePk.get(i).toAobj());
		}
		return aso;
	}
	
	@Override
	public void settleRoundPlayersOnStart()
	{
		this.playerList.clear();
		for(Map.Entry<Integer, Player> entry : this.playerMap.entrySet())
		{
			this.playerList.add(entry.getValue());
		}
		
		Player player = this.playerList.peek();
		while(player.getPlayerState() != PlayerCareerState.PLAYER_STATE_BIGBLIND)
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
		while(!tempPlayer.getUserId().equals(player.getUserId()))
		{
			this.playerList.offer(this.playerList.poll());
			tempPlayer = this.playerList.peek();
		}
		this.playerList.poll();
	}
	
	@Override
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

	@Override
	public void gameOverHandle()
	{
		for(Map.Entry<Integer, Player> entry : this.playerMap.entrySet())
		{
			UserInfo userInfo = UserInfoMemoryCache.getUserInfo(entry.getValue().getUserId());
			if(userInfo != null)
			{
				userInfo.setPlaying(false);
				userInfo.addCompletePkNum();
			}
		}
		
		this.currentPlayer = null;
//		this.balanceBet();	
		this.getPlayerMaxHandPuke();
		this.round = 0;
		this.balanceBet();
		this.room.gameOverHandle();
		this.notifyRoomPlayerRoundOver();
		this.notifyRoomPlayerPokeGameOver();
		
		for(Map.Entry<Integer, Player> entry : this.playerMap.entrySet())
		{
			UserInfo userInfo = UserInfoMemoryCache.getUserInfo(entry.getValue().getUserId());
			if(userInfo != null)
			{
				userInfo.setSaveUpdate(true);
			}
		}
		
		this.clearLessMoneyPlayer();
	}
	
	public void getPlayerMaxHandPuke() 
	{
		List<Player> players = new ArrayList<Player>(this.playerMap.values());
		int j = players.size();
		for(int i = 0; i < j; i++)
		{
			
			Map<Integer, Puke> maxHand = new HashMap<Integer, Puke>();

			List<Puke> fivePk = players.get(i).getFivePk();
			long fivepkvalue = players.get(i).getPkValue();
			UserInfo usInfo = UserInfoMemoryCache.getUserInfo(players.get(i).getUserId());
			int l = fivePk.size();
			for(int k = 0; k < l; k++)
			{
				maxHand.put(k, fivePk.get(k));
			}

			
			if(usInfo.getMaxHandStr().equals(""))
			{
				usInfo.setMaxHand(maxHand);
				usInfo.setMaxHandValue(fivepkvalue);
				usInfo.setMaxHandStr(usInfo.GetMaxhandStringFromObj(usInfo.getMaxHand()));
			}
			else 
			{
				if(players.get(i).getGameState() != PlayerGameState.GAME_STATE_STANDUP
					 && players.get(i).getGameState() != PlayerGameState.GAME_STATE_DROP_CARD )
				{
					if(fivepkvalue > usInfo.getMaxHandValue())
					{
						usInfo.setMaxHand(maxHand);
						usInfo.setMaxHandValue(fivepkvalue);
						usInfo.setMaxHandStr(usInfo.GetMaxhandStringFromObj(usInfo.getMaxHand()));
					}
				}	
			}
		}

	}
	
	@Override
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
		response.put("wt_uid",this.currentPlayer.getUserId());
		
		ActionscriptObject as_plist = new ActionscriptObject();
		for(Map.Entry<Integer, Player> entry : this.playerMap.entrySet())
		{
			ActionscriptObject as_player = new ActionscriptObject();
			Player player = entry.getValue();
			UserInfo uinfo = UserInfoMemoryCache.getUserInfo(player.getUserId());
			
			HashMap<String, HashMap<String, Integer>> propmap = uinfo.getPropmap();
			HashMap<String, Integer> showmap = propmap.get(PropValues.Prop_MainType_A);
			HashMap<String, Integer> funcwmap = propmap.get(PropValues.Prop_MainType_B);
			
			String dj_show = "0";
			for (String key : showmap.keySet())
			{
				dj_show = key;
			}
			as_player.put("dj_show", dj_show);
			
			ActionscriptObject dj_func = new ActionscriptObject();
			for (String key : funcwmap.keySet())
			{
				dj_func.put("type", key);
				dj_func.put("ucount", String.valueOf(funcwmap.get(key)));
			}
			as_player.put("dj_func", dj_func);
			
			as_player.put("un",uinfo.getName());
			as_player.putNumber("lev",Utils.retLevelAndExp(uinfo.getExprience())[0]);
			as_player.putNumber("sid",entry.getKey());
			as_player.put("uid",player.getUserId());
			
			as_player.putNumber("pkl",player.getPkLevel());
			as_player.put("pic",uinfo.getPic());
			as_player.putBool("isp",true);
			as_player.putNumber("tb",player.getTempBet());
			as_player.putNumber("yt",player.getYourTurn());
			
			as_player.putNumber("cm",uinfo.getRmoney());
			as_player.putNumber("tm",uinfo.getAMoney());
			as_player.putNumber("gs",player.getGameState().value());
			as_player.putNumber("ps",player.getPlayerState().value());
			
			as_player.putNumber("frb", uinfo.getFirstRoundBet());
			as_player.putNumber("srb", uinfo.getSecondRoundBet());
			as_player.putNumber("trb", uinfo.getThirdRoundBet());
			as_player.putNumber("ftrb", uinfo.getFourthRoundBet());
			
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
	
	@Override
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
			if(entry.getValue().getPlayerState() == PlayerCareerState.PLAYER_STATE_LEAVE)
			{
				continue;
			}
			UserInfo uInfo = UserInfoMemoryCache.getUserInfo(entry.getValue().getUserId());
			cmList.put(entry.getValue().getSeatId(),uInfo.getRmoney() + "");
		}
		asObj.put("cmList",cmList);
		asObj.put("_cmd",ConstList.CMD_DBT); 
		asObj.putBool("normal", isGameNormalOver());
		this.room.notifyRoomPlayer(asObj,MessageType.MESSAGE_NINE);
	}
	
	@Override
	public void notifyRoomPlayerPokeGameStart()
	{
		ActionscriptObject response = this.toAsObj();
		response.put("_cmd",ConstList.CMD_SBOT);
		this.room.notifyRoomPlayer(response,MessageType.MESSAGE_NINE);
	}
	
	public void notifyRoomWhoTurn()
	{
		ActionscriptObject response = new ActionscriptObject();
		
		response.put("_cmd",ConstList.CMD_WHO_TURN);
		response.putNumber("mbet",this.maxBet);
		response.putNumber("wt_sid",currentPlayer.getSeatId());
		response.put("wt_uid",currentPlayer.getUserId());
		
		this.room.notifyRoomPlayer(response,MessageType.MESSAGE_NINE);
	}
	
	@Override
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
	
	@Override
	public boolean isGameOverWhenDropCard()
	{
		int size = 0;
		for(Map.Entry<Integer,Player> entry : this.playerMap.entrySet())
		{
			if(entry.getValue().getGameState() == PlayerGameState.GAME_STATE_DROP_CARD)
			{
				continue;
			}
			if(entry.getValue().getGameState() == PlayerGameState.GAME_STATE_STANDUP)
			{
				continue;
			}
			size ++;
		}
		
		if(size < 2)
		{
			return true;
		}
		
		return false;
	}
	
	public boolean isGameNormalOver()
	{
		int size = 0;
		for(Map.Entry<Integer,Player> entry : this.playerMap.entrySet())
		{
			if(entry.getValue().getGameState() == PlayerGameState.GAME_STATE_DROP_CARD)
			{
				continue;
			}
			else if(entry.getValue().getGameState() == PlayerGameState.GAME_STATE_STANDUP)
			{
				continue;
			}
			size ++;
		}
		
		if(size < 2)
		{
			return false;
		}
		
		return true;
	}
	
	@Override
	public boolean isGameOver()
	{
		if(this.round >= 4)
		{
			return true;
		}
		
		int size = 0;
		for(Map.Entry<Integer,Player> entry : this.playerMap.entrySet())
		{
			if(entry.getValue().getGameState() == PlayerGameState.GAME_STATE_DROP_CARD)
			{
				continue;
			}
			else if(entry.getValue().getGameState() == PlayerGameState.GAME_STATE_ALL_END)
			{
				continue;
			}
			else if(entry.getValue().getGameState() == PlayerGameState.GAME_STATE_STANDUP)
			{
				continue;
			}
			size ++;
		}
		
		if(size < 2)
		{
			return true;
		}
		
		return false;
	}
	
	public boolean isPlayerTurnValid(Player player)
	{
		if(player == null)
		{
			return false;
		}
		if(player.getGameState() == PlayerGameState.GAME_STATE_DROP_CARD)
		{
			return false;
		}
		if(player.getGameState() == PlayerGameState.GAME_STATE_ALL_END)
		{
			return false;
		}
		if(player.getGameState() == PlayerGameState.GAME_STATE_STANDUP)
		{
			return false;
		}
		if(this.playerMap.containsKey(player.getSeatId()) == false)
		{
			return false;
		}
		
		return true;
	}
	
	@Override
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
	
	@Override
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
		
		this.room.notifyRoomPlayer(response,MessageType.MESSAGE_NINE);
	}
	
	@Override
	public ActionscriptObject playerLookCard(Player player)
	{
		ActionscriptObject response = new ActionscriptObject();
		if(this.isYouTurn(player) == false)
		{
			response.put("error","notYourTurn");
			log.error("roomName: " + this.room.getName() 
					+ " seat : " + player.getSeatId() + " Id: "
					+ player.getUserId() + " not your turn ");
			
			return response;
		}
		
		player.setGameState(PlayerGameState.GAME_STATE_LOOK_CARD);
		player.setYourTurn(0);
		
		ActionscriptObject as_player = player.toAsObj();
		
		response.put("_cmd", ConstList.CMD_LOOK_CARD);
		response.put("user", as_player);
		
		log.info("roomName: " + this.room.getName() + " seat: " + player.getSeatId() + " Id: " + player.getUserId() + " look card " );
		this.room.notifyRoomPlayerButOne(response,MessageType.MESSAGE_NINE,player.getUserId());
		this.turnOverHandle();
		return response;
	}
	
	@Override
	public void addPoolBet(int bet)
	{
		this.currentRoundBet += bet;
	}
	
	@Override
	public ActionscriptObject playerAddBet(Player player,int bet)
	{
		ActionscriptObject response = new ActionscriptObject();
		if(this.isYouTurn(player) == false)
		{
			response.put("error","notYourTurn");
			
			log.error("roomName: " + this.room.getName() 
					+ " seat : " + player.getSeatId() + " Id: "
					+ player.getUserId() + " not your turn ");
			
			return response;
		}
		
		player.setGameState(PlayerGameState.GAME_STATE_ADD_BET);
		UserInfo uInfo = UserInfoMemoryCache.getUserInfo(player.getUserId());
		
		if(bet > uInfo.getRmoney())
		{
			bet = uInfo.getRmoney();
		}
		
		if(bet < 2 * (this.maxBet - player.getTempBet()))
		{
			response.put("error","YouParmsisNotValid");
			
			log.error("roomName: " + this.room.getName() 
					+ " seat : " + player.getSeatId() + " Id: "
					+ player.getUserId() + " add bet error bet: " + bet);
			
			return response;
		}
		
		this.addPoolBet(bet);
		player.addTempBet(bet);
		player.addTotalGambleBet(bet);
		uInfo.deductRmoney(bet);
		player.setYourTurn(0);
		
		if(player.getTempBet() > this.maxBet)
		{
			this.maxBet = player.getTempBet();
			this.maxBetSeatId = player.getSeatId();
		}
		
		ActionscriptObject as_player = player.toAsObj();
		response.put("_cmd",ConstList.CMD_ADD_BET);
		response.put("user",as_player);
		
		log.info("roomName: " + this.room.getName() + " seat : " + player.getSeatId() + " Id: " + player.getUserId() + " add " + bet  );
		this.room.notifyRoomPlayerButOne(response,MessageType.MESSAGE_NINE,player.getUserId());
		this.settleRoundPlayersOnAddBet(player);
		this.turnOverHandle();
		return response;
	}
	
	@Override
	public ActionscriptObject playerFollowBet(Player player,int bet)
	{
		ActionscriptObject response = new ActionscriptObject();
		if(this.isYouTurn(player) == false)
		{
			response.put("error","notYourTurn");
			
			log.error("roomName: " + this.room.getName() 
					+ " seat : " + player.getSeatId() + " Id: "
					+ player.getUserId() + " not your turn ");
			
			return response;
		}
		
		player.setGameState(PlayerGameState.GAME_STATE_FOLLOW_BET);
		UserInfo uInfo = UserInfoMemoryCache.getUserInfo(player.getUserId());
		
		if(bet > uInfo.getRmoney())
		{
			bet = uInfo.getRmoney();
		}
		
		if(player.getTempBet() + bet < this.maxBet)
		{
			response.put("error","YouParmsisNotValid");
			log.error("roomName: " + this.room.getName() 
					+ " seat : " + player.getSeatId() + " Id: "
					+ player.getUserId() + " call error,bet: " + bet);
			
			return response;
		}
		
		this.addPoolBet(bet);
		player.addTempBet(bet);
		player.addTotalGambleBet(bet);
		uInfo.deductRmoney(bet);
		player.setYourTurn(0);
		player.setGameState(PlayerGameState.GAME_STATE_FOLLOW_BET);
		
		if(player.getTempBet() > this.maxBet)
		{
			this.maxBet = player.getTempBet();
			this.maxBetSeatId = player.getSeatId();
		}
		
		response.put("_cmd",ConstList.CMD_FOLLOW_BET);
		ActionscriptObject as_player = player.toAsObj();
		response.put("user",as_player);
		
		log.info("roomName: " + this.room.getName() + " seat: " + player.getSeatId() + " Id: " + player.getUserId() + " call " + bet );
		this.room.notifyRoomPlayerButOne(response,MessageType.MESSAGE_NINE,player.getUserId());
		this.turnOverHandle();
		return response;
	}
	
	@Override
	public ActionscriptObject playerDropCard(Player player)
	{
		
		ActionscriptObject response = new ActionscriptObject();
		if(this.isYouTurn(player) == false)
		{
			response.put("error","notYourTurn");
			
			log.error("roomName: " + this.room.getName() 
					+ " seat : " + player.getSeatId() + " Id: "
					+ player.getUserId() + " not your turn ");
			
			return response;
		}
		
		player.setGameState(PlayerGameState.GAME_STATE_DROP_CARD);
		player.setYourTurn(0);
		
		response.put("_cmd",ConstList.CMD_DROP_CARD);
		response.put("user",player.toAsObj());
		
		this.publicPoolBet += player.getTotalGambleBet();
		
		log.info("roomName: " + this.room.getName() + " seat: " + player.getSeatId() + " Id: " + player.getUserId() + " drop card " );
		this.room.notifyRoomPlayer(response,MessageType.MESSAGE_NINE);
		this.turnOverHandle();
		if(this.room.isGame() && this.isGameOverWhenDropCard())
		{
			this.gameOverHandle();
		}
		
		return response;
	}
	
	@Override
	public ActionscriptObject playerAllIn(Player player,int bet)
	{
		ActionscriptObject response = new ActionscriptObject();
		if(this.isYouTurn(player) == false)
		{
			response.put("error","notYourTurn");
			
			log.error("roomName: " + this.room.getName() 
					+ " seat : " + player.getSeatId() + " Id: "
					+ player.getUserId() + " not your turn ");
			
			return response;
		}
		
		UserInfo uInfo = UserInfoMemoryCache.getUserInfo(player.getUserId());
		if(bet > uInfo.getRmoney())
		{
			bet = uInfo.getRmoney();
		}
		
		this.addPoolBet(bet);
		player.addTempBet(bet);
		player.addTotalGambleBet(bet);
		uInfo.deductRmoney(bet);
		
		player.setGameState(PlayerGameState.GAME_STATE_ALL_END);
		player.setYourTurn(0);
		
		if(player.getTempBet() > this.maxBet)
		{
			this.maxBet = player.getTempBet();
			this.maxBetSeatId = player.getSeatId();
			this.settleRoundPlayersOnAddBet(player);
		}
		
		response.put("_cmd",ConstList.CMD_ALL_IN);
		response.put("user",player.toAsObj());
		
		log.info("roomName: " + this.room.getName() + " seat : " + player.getSeatId() + " Id: " + player.getUserId() + " all in " + bet );
		this.room.notifyRoomPlayerButOne(response,MessageType.MESSAGE_NINE,player.getUserId());
		this.turnOverHandle();
		return response;
	}
	
	@Override
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
		UserInfo uInfo = UserInfoMemoryCache.getUserInfo(player.getUserId());
		
		player.setGameState(PlayerGameState.GAME_STATE_STANDUP);
		player.setPlayerState(PlayerCareerState.PLAYER_STATE_LEAVE);
		
		uInfo.addAmoney(uInfo.getRmoney());
		uInfo.clearRoomMoney();
		
		ActionscriptObject as_player = player.toAsObj();
		as_player.putBool("isp",false);
		as_player.putBool("ip",true);
		
		response.put("_cmd",ConstList.CMD_STANDUP);
		response.put("user",as_player);
		
		this.room.notifyRoomPlayer(response,MessageType.MESSAGE_NINE);
		
		if(this.isGameOver())
		{
			this.gameOverHandle();
		}
		
		return response;
	}
	
	@Override
	public boolean isYouTurn(Player player)
	{
		if(this.currentPlayer == null || player == null)
		{
			return false;
		}
		
		if(player.getUserId().equals(this.currentPlayer.getUserId()))
		{
			return true;
		}
		
		return false;
		
	}
	
	@Override
	public ActionscriptObject playerLeave(Player player)
	{
		this.playerMap.remove(player.getSeatId());
		ActionscriptObject response = new ActionscriptObject();
		UserInfo uInfo = UserInfoMemoryCache.getUserInfo(player.getUserId());
		User user = UserModule.getInstance().getUserByUserId(Integer.parseInt( player.getUserId() ));
		
		player.setGameState(PlayerGameState.GAME_STATE_LEAVE);
		player.setPlayerState(PlayerCareerState.PLAYER_STATE_LEAVE);
		
		uInfo.addAmoney(uInfo.getRmoney());
		uInfo.clearRoomMoney();
		
		user.setRoomId(-1);
		ActionscriptObject as_player = player.toAsObj();
		
		response.put("_cmd",ConstList.CMD_LEAVE);
		response.put("_user",as_player);

		if(this.room.isPlayerSitDown(player.getUserId()))
		{
			response.put("flag","sit");
			response.put("user",as_player);
			this.room.notifyRoomPlayerButOne(response,MessageType.MESSAGE_NINE,player.getUserId());
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
	
	public boolean isUserPlaying(String uid)
	{	
		if(this.room.isGame() == false)
		{
			return false;
		}
		
		for(Map.Entry<Integer, Player> entry : this.playerMap.entrySet())
		{
			if(entry.getValue().getUserId().equals(uid))
			{
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public void playerStandup(int seatId)
	{
		Player player = playerMap.get(seatId);
		if(player != null)
		{
			this.publicPoolBet += player.getTotalGambleBet();
			if(this.currentPlayer != null && player.getUserId().equals(this.currentPlayer.getUserId()))
			{
				this.turnOverHandle();
			}
		}
		
		this.playerMap.remove(seatId);
		return ;
	}
	
	@Override
	public Player findPlayerByUser(User u)
	{
		for(Map.Entry<Integer, Player> entry : this.playerMap.entrySet())
		{
			if(entry.getValue().getUserId().equals(u.getUid()))
			{
				return entry.getValue();
			}
		}
		return null;
	}
	
	@Override
	public int getTurn()
	{
		return this.roundNum;
	}
	
	@Override
	public int getRound()
	{
		return this.round;
	}
	
	@Override
	public int maxBet()
	{
		return this.maxBet;
	}
	
	@Override
	public int maxSeatId()
	{
		return this.maxBetSeatId;
	}
	
	@Override
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
	
	@Override
	public int getBankerSeatId()
	{
		return this.bankSeatId;
	}
	
	@Override
	public int getNextSeatId()
	{
		if(currentPlayer == null)
		{
			return 0;
		}
		return this.currentPlayer.getSeatId();
	}
	
	@Override
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
