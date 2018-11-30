package com.archy.dezhou.room;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import com.archy.dezhou.Global.ConstList;
import com.archy.dezhou.Global.UserModule;
import com.archy.dezhou.container.ActionscriptObject;
import com.archy.dezhou.container.User;
import com.archy.dezhou.entity.Player;
import com.archy.dezhou.entity.Puke;
import com.archy.dezhou.entity.UserInfo;
import com.archy.dezhou.httpLogic.ConnectInstance;
import com.archy.dezhou.room.base.IRoom;
import org.apache.log4j.Logger;

import com.archy.dezhou.util.HeartTimer;
import com.archy.dezhou.Global.PropValues;
import com.archy.dezhou.Global.UserInfoMemoryCache;
import com.archy.dezhou.container.RoomVariable;
import com.archy.dezhou.container.TimerMessageQuene;
import com.archy.dezhou.room.base.IPukerGame;
import com.archy.dezhou.room.base.IRoomState;
import com.archy.dezhou.util.Utils;

public class Room implements IRoom
{

	private Logger log = Logger.getLogger(getClass());
	
	
	private Map<String,String> roomConfig = null;
 	
	/**
	 *
	 *
	 **/
	public Room(String name ,String zone,String Creater,String roomType)
	{
		setRoomID();
		this.name = name;
		this.zone = zone;
		this.creator = Creater;
		this.roomType = roomType;
		
		this.roomConfig = UserModule.getInstance().getRoomConfigByType(roomType);
		if(this.roomConfig == null)
		{
			log.fatal("create room error ,room type not exist : " + roomType);
			return ;
		}
		
		this.pokerGame = new PukerGame(this);
	}

	public void destroyExtensions()
	{

	}
	
	@Override
	public boolean isUserInRoom(User user)
	{
		return this.userMap.containsValue(user);
	}
	
	@Override
	public boolean isUserSitdown(User user)
	{
		return this.spectatorList.contains(user);
	}

	@Override
	public ActionscriptObject userSitDown(int seatId, User user, int cb)
	{
		ActionscriptObject response = new ActionscriptObject();
		ActionscriptObject userAobj = new ActionscriptObject();
		if(user == null)
		{
			return null;
		}
		
		if(this.userMap.containsKey(seatId))
		{
			response.put("_cmd", ConstList.CMD_SITDOWN);
			userAobj.put("uid", user.getUid());
			response.put("user", userAobj);
			response.put("issit", "yes");
			response.put("sid", seatId);
			response.put("info", "HaveAlreadySitDowned");
			log.info("roomName: " + this.getName() +   " 此位置已经有人 seat: " + seatId);
			return response;
		}
		
		if(this.userMap.values().contains(user))
		{
			response.put("_cmd", ConstList.CMD_SITDOWN);
			response.put("roomKey", this.getName());
			response.put("issit", "yes");
			response.put("info", "YouHaveSitedAtOtherPlace");
			log.info("roomName: " + this.getName() +   " 此玩家已经坐下  uid: " + user.getUid());
			return response;
		}
		
//		if(user.isInSitdownCd())
//		{
//			response.put("_cmd", ConstList.CMD_SITDOWN);
//			response.put("roomKey", this.getName());
//			response.put("issit", "yes");
//			response.put("info", "YouInSitdownCd");
//			log.info("roomName: " + this.getName() +   " 此玩家坐下cd中  uid: " + user.getUid());
//			return response;
//		}
		
		log.info("roomName: " + this.getName() + "  " + user.getUid() + " try to sitdown at seatId: " + seatId);
		this.spectatorList.remove(user);
		UserInfo uinfo = UserInfoMemoryCache.getUserInfo(user.getUid());
		
		uinfo.clearRoomMoney();
		uinfo.addRmoney(cb);
		uinfo.deductAmoney(cb);
		
		Player player = new Player();
		player.setUserId(user.getUid());
		player.setSeatId(seatId);
		this.addPlayer(seatId,player);
		user.updateSitdownTime();
		
		if(this.isGame())
		{
			player.setGameState(ConstList.PlayerGameState.PLAYER_STATE_WAIT);
			player.setPlayerState(ConstList.PlayerCareerState.PLAYER_STATE_WAIT);
		}
		else
		{
			player.setGameState(ConstList.PlayerGameState.PLAYER_STATE_PLAYER);
			player.setPlayerState(ConstList.PlayerCareerState.PLAYER_STATE_PLAYER);
		}
		
		HashMap<String, HashMap<String, Integer>> propmap = uinfo.getPropmap();
		HashMap<String, Integer> showmap = propmap.get(PropValues.Prop_MainType_A);
		HashMap<String, Integer> funcwmap = propmap.get(PropValues.Prop_MainType_B);

		String dj_show = "0";
		for (String key : showmap.keySet())
		{
			dj_show = key;
		}
		userAobj.put("dj_show", dj_show);
		
		ActionscriptObject dj_func = new ActionscriptObject();
		for (String key : funcwmap.keySet())
		{
			dj_func.put("type", key);
			dj_func.put("ucount", String.valueOf(funcwmap.get(key)));
		}
		
		userAobj.put("dj_func", dj_func);
		userAobj.put("uid",user.getUid());
		userAobj.putNumber("sid", seatId);
		userAobj.putNumber("cm", uinfo.getRmoney());
		userAobj.put("tm", String.valueOf(uinfo.getAMoney()));
		userAobj.put("un", uinfo.getName());
		userAobj.put("pic", uinfo.getPic());
		userAobj.putNumber("ps", player.getPlayerState().value());
		userAobj.putNumber("gs", player.getGameState().value());
		userAobj.putNumber("lev", Integer.parseInt(Utils.retLevel(uinfo.getExprience())));
		userAobj.putNumber("yt", 0);
		userAobj.put("big", "");
		userAobj.put("spr", "");
		
		response.put("_cmd",ConstList.CMD_SITDOWN);
		response.put("user", userAobj);
		response.put("issit", "no");
		
		this.addUser(seatId,user);
		this.notifyRoomPlayerButOne(response, ConstList.MessageType.MESSAGE_NINE,user.getUid());
		return response;
	}

	@Override
	public ActionscriptObject userStandUp(User u,boolean notifyMySelf)
	{
		if(u == null)
		{
			return null;
		}

		log.info("roomName: " + this.getName() + " try standup users: "  + u.getUid());
		
		int seatId = 0;
		for(Map.Entry<Integer, User> entry : this.userMap.entrySet())
		{
			if(entry.getValue().getUid().equals(u.getUid()))
			{
				this.userMap.remove(entry.getKey());
				seatId = entry.getKey();
				log.info("roomName: " + this.getName() + "  standup users: "  + u.getUid() + " ok! ");
				break;
			}
		}
		
		Player player = this.findPlayerByUser(u);
		if(player == null)
		{
			return null;
		}
		
		this.spectatorList.add(u);

		log.warn("roomName: " + this.getName() + "  " + u.getUid() + " standup at seatId: " + seatId);
		
		player.setGameState(ConstList.PlayerGameState.GAME_STATE_STANDUP);

		this.playerMap.remove(seatId);
		boolean isp = this.pokerGame.isUserPlaying(u.getUid());
		this.pokerGame.playerStandup(player.getSeatId());
		UserInfo uInfo = UserInfoMemoryCache.getUserInfo(u.getUid());
		if(uInfo == null)
		{
			return null;
		}
		
		uInfo.addAmoney(uInfo.getRmoney());
		uInfo.clearRoomMoney();
		
		ActionscriptObject response = new ActionscriptObject();
		response.put("_cmd",ConstList.CMD_STANDUP);
		
		ActionscriptObject playerAs = new ActionscriptObject();
		playerAs.putNumber("sid",seatId);
		playerAs.put("un",u.getName());
		playerAs.putNumber("yt",player.getYourTurn());
		playerAs.put("uid",u.getUserId());
		playerAs.put("uid",u.getUid());
		playerAs.putNumber("ps",player.getPlayerState().value());
		playerAs.putNumber("gs",player.getGameState().value());
		playerAs.putBool("isp",isp);
		playerAs.putBool("ip",true);
		playerAs.put("tm","" + uInfo.getAMoney());
		playerAs.putNumber("tb",player.getTempBet());
		response.put("user",playerAs);
		
		if(notifyMySelf)
		{
			this.notifyRoomPlayer(response, ConstList.MessageType.MESSAGE_NINE);
		}
		else
		{
			this.notifyRoomPlayerButOne(response, ConstList.MessageType.MESSAGE_NINE, u.getUid());
		}
		return response;
	}

	public int getMaxSpectator()
	{
		return 100000;
	}

	@Override
	public int userJoin(User u)
	{
		u.updateOperateTime();
		if (!this.spectatorList.contains(u))
		{
			log.info("roomName: " + this.getName() + "  " + u.getUid() + " enter room as spectator");
			this.spectatorList.add(u);
			return 0;
		}
		else
		{
			log.info("roomName: " + this.getName() + "  " + u.getUid() + "已经进入过该房间");
			return -2;// 已经进入过房间
		}
	}
	
	@Override
	public int userLeave(User user)
	{
		user.clearBetMessageQuene();
		this.forceRemoveUser(user);
		return 0;
	}

	public int addUser(int seatId,User u)
	{
		this.userMap.put(seatId,u);
		return 0;
	}
	
	public void addPlayer(int seatId,Player player)
	{
		this.playerMap.put(seatId,player);
	}
	
	public void removePlayer(int seatId)
	{
		this.playerMap.remove(seatId);
	}
	
	private IPukerGame pokerGame = null;
	
	private class RoomStateReady implements IRoomState
	{
		
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
				if(userMap.size() >= 2 && playerMap.size() >= 2)
				{
					this.timer = null;
					roomState = new RoomStateFight();
					try
					{
						pokerGame.gameStartHandle();
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
		
		private HeartTimer timer = null;
		
	}
	
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
				pokerGame.beatHeart(now);
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
	
	private volatile IRoomState roomState = new RoomStateReady();
	
	@Override
	public void beatHeart(long now)
	{
		now = System.currentTimeMillis();
		List<User> users = new ArrayList<User>();
		users.addAll(this.userMap.values());
		for(User user : users)
		{
			if(user.isStandUpExpired(now))
			{
				log.warn("roomName: " + this.getName() + " at time: " + System.currentTimeMillis() + " user " + user.getUid() + " standUp expired");
				this.userStandUp(user,true);
			}
		}
		
		users.clear();
		users.addAll(this.spectatorList);
		for(User user : users)
		{
			if(user.isLeaveExpired(now))
			{
				if(this.isPlayerSitDown(user.getUid()))
				{
					continue;
				}
				log.warn("roomName: " + this.getName() + " at time: " + System.currentTimeMillis() + " user " + user.getUid() + " leave room expired");
				this.userLeave(user);
			}
		}
		
		this.roomState.beatHear(now);
	}
	
	@Override
	public boolean isPlayerSitDown(String uId)
	{
		
		for(Map.Entry<Integer, User> entry : this.userMap.entrySet())
		{
			if(entry.getValue().getUid().equals(uId))
			{
				return true;
			}
		}
		return false;
	}

	public User getUserByName(String userName)
	{
		for(User user : this.userMap.values())
		{
			if(user.getName().equals(userName))
			{
				return user;
			}
		}
		return null;
	}

	public User getUserByUid(String Uid)
	{
		for(Map.Entry<Integer, User>  entry : this.userMap.entrySet())
		{
			if(entry.getValue().getUid().equals(Uid))
			{
				return entry.getValue();
			}
		}
		return null;
	}

	public User[] getSpectatorList()
	{
		return this.spectatorList.toArray(new User[this.spectatorList.size()]);
	}

	@Override
	public User[] getAllUsers()
	{
		return this.userMap.values().toArray(new User[this.userMap.size()]);
	}

	public User[] getAllUsersButOne(User user)
	{
		List<User> tempUserList = new ArrayList<User>();
		tempUserList.addAll(this.userMap.values());
		tempUserList.remove(user);
		return tempUserList.toArray(new User[tempUserList.size()]);
	}

	public User[] getAllUsersButOne(Integer uid)
	{
		User user = UserModule.getInstance().getUserByUserId(uid);
		return this.getAllUsersButOne(user);
	}

	public String getXmlVariable(String varName)
	{
		StringBuffer xmlVar = new StringBuffer();
		RoomVariable rv = roomVariables.get(varName);
		if (rv != null)
		{
			xmlVar.append("<var n='").append(varName);
			xmlVar.append("' t='").append(rv.getType());
			xmlVar.append("'><![CDATA[").append(rv.getValue())
					.append("]]></var>");
		}
		return xmlVar.toString();
	}

	public List<String> getVariableNames()
	{
		List<String> ll = new LinkedList<String>();
		synchronized (roomVariables)
		{
			ll.addAll(roomVariables.keySet());
		}
		return ll;
	}

	public Map<String, RoomVariable> getVariables()
	{
		return roomVariables;
	}

	public RoomVariable getVariable(String varName)
	{
		return roomVariables.get(varName);
	}

	@Override
	public int getRoomId()
	{
		return roomid;
	}

	public String getZone()
	{
		return zone;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public boolean isPrivate()
	{
		return isPrivate;
	}

	public boolean isTemp()
	{
		return false;
	}
	
	public int getMaxUsers()
	{
		return ConstList.MAXNUMPLAYEROFROOM;
	}

	@Override
	public int getUserCount()
	{
		return this.userMap.size();
	}

	@Override
	public int getSpectatorCount()
	{
		return this.spectatorList.size();
	}
	
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

	public boolean forceRemoveUser(User u)
	{
		if(this.isPlayerSitDown(u.getUid()))
		{
			this.userStandUp(u,true);
		}
		
		this.spectatorList.remove(u);
		return true;
	}

	public boolean deleteVariable(String vName, User owner)
	{
		return true;
	}

	@Override
	public int howManyUsers()
	{
		return this.userMap.size();
	}
	
	@Override
	public int howManySpecators()
	{
		return this.spectatorList.size();
	}
	
	@Override
	public boolean isUserInSeat(User user)
	{
		return this.userMap.values().contains(user);
	}

	public String getCreator()
	{
		return creator;
	}
	
	public void notifyRoomPlayerButOne(ActionscriptObject aObj, ConstList.MessageType msgType, String uId)
	{
		long timeStamp = System.currentTimeMillis();
		Set<User> users = new HashSet<User>();
		
		synchronized (this.userMap)
		{
			users.addAll(this.userMap.values());
		}
		synchronized (this.spectatorList)
		{
			users.addAll(this.spectatorList);
		}
		
		for(User user : users)
		{
			if(user.getUid().equals(uId))
			{
				continue;
			}
			
			ConnectInstance connect = new ConnectInstance();
			connect.status = 0;
			connect.roomKey = this.getName();
			connect.priority = (byte) msgType.getMessageLevel();
			connect.CreateTmeStampe = timeStamp;
			aObj.put("ts", connect.CreateTmeStampe + "");
			connect.expireTime = msgType.getMessageExpireTime();
			connect.aObj = aObj;
			connect._cmd = aObj.getString("_cmd");
			++TimerMessageQuene.totalLinkid;
			connect.linkid = TimerMessageQuene.totalLinkid;
			user.sendBetMessage(connect);
		}
	}
	
	public void notifyRoomPlayer(ActionscriptObject aObj, ConstList.MessageType msgType)
	{
		long timeStamp = System.currentTimeMillis();
		Set<User> users = new HashSet<User>();
		
		synchronized (this.userMap)
		{
			users.addAll(this.userMap.values());
		}
		synchronized (this.spectatorList)
		{
			users.addAll(this.spectatorList);
		}
		
		for(User user : users)
		{
			ConnectInstance connect = new ConnectInstance();
			connect.status = 0;
			connect.roomKey = this.getName();
			connect.priority = (byte) msgType.getMessageLevel();
			connect.CreateTmeStampe = timeStamp;
			aObj.put("ts", connect.CreateTmeStampe + "");
			connect.expireTime = msgType.getMessageExpireTime();
			connect.aObj = aObj;
			connect._cmd = aObj.getString("_cmd");
			++TimerMessageQuene.totalLinkid;
			connect.linkid = TimerMessageQuene.totalLinkid;
			user.sendBetMessage(connect);
		}
	}

	private void setRoomID()
	{
		roomid = autoId.getAndIncrement();
	}

	public static void resetRoomStaticData()
	{
		autoId = new AtomicInteger(0);
	}

	public boolean isLimbo()
	{
		return isLimbo;
	}

	public void setLimbo(boolean isLimbo)
	{
		this.isLimbo = isLimbo;
	}
	
	public String getRoomType()
	{
		return this.roomType;
	}
	
	public String getChineseName()
	{
		return this.roomConfig.get("showname");
	}
	
	@Override
	public int getMinBuy()
	{
		String str = this.roomConfig.get("mixbuy");
		if(str != null)
		{
			return Integer.parseInt(str);
		}
		return 0;
	}
	
	@Override
	public int getSBet()
	{
		String str = this.roomConfig.get("sbet");
		if(str != null)
		{
			return Integer.parseInt(str);
		}
		return 0;
	}
	
	@Override
	public int getBBet()
	{
		String str = this.roomConfig.get("bbet");
		if(str != null)
		{
			return Integer.parseInt(str);
		}
		return 0;
	}
	
	@Override
	public int getMaxBuy()
	{
		String str = this.roomConfig.get("maxbuy");
		if(str != null)
		{
			return Integer.parseInt(str);
		}
		return 0;
	}
	
	@Override
	public boolean isGame()
	{
		return this.roomState.isGame();
	}
	
	@Override
	public IPukerGame getPokerGame()
	{
		return this.pokerGame;
	}
	
	@Override
	public void gameOverHandle()
	{
		this.roomState = new RoomStateReady(7);
	}
	
	@Override
	public int getSecsPassByTurn()
	{
		if(this.pokerGame == null)
		{
			return 0;
		}
		return this.pokerGame.getSecsPassByTurn();
	}
	
	@Override
	public String getValueByKey(String key)
	{
		return this.roomConfig.get(key);
	}
	
	@Override
	public ActionscriptObject toAsObj()
	{
		ActionscriptObject response = new ActionscriptObject();
		
		response.put("_cmd",ConstList.CMD_ROOMINFO);
		response.put("ig", this.isGame()?"yes":"no");
		response.putNumber("turn",this.pokerGame.getTurn());
		response.putNumber("round",this.pokerGame.getRound());
		
		response.putNumber("mbet",this.getBBet());
		response.putNumber("sbet",this.getSBet());
		response.putNumber("msid",this.pokerGame.maxSeatId());
		response.putNumber("bsid",this.pokerGame.getBankerSeatId());
		response.putNumber("wt",this.pokerGame.getNextSeatId());
		
		response.putNumber("minbuy",this.getMinBuy());
		response.putNumber("maxbuy",this.getMaxBuy());
		
		response.put("chname",this.getValueByKey("showname"));
		response.put("roomName",this.getName());
		response.putNumber("fpb",this.pokerGame.getPoolBet(1));
		response.putNumber("spb",this.pokerGame.getPoolBet(2));
		response.putNumber("tpb",this.pokerGame.getPoolBet(3));
		response.putNumber("fopb",this.pokerGame.getPoolBet(4));
		
		if(this.isGame())
		{
			response.put("fpk",this.pokerGame.fiveSharePkToAsob());
		}
		
		ActionscriptObject as_plist = new ActionscriptObject();
		for(Map.Entry<Integer, Player> entry : this.playerMap.entrySet())
		{
			ActionscriptObject as_player = new ActionscriptObject();
			Player player = entry.getValue();
			UserInfo uinfo = UserInfoMemoryCache.getUserInfo(player.getUserId());
			
			if(uinfo == null)
			{
				continue;
			}
			
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
			as_player.putBool("isp",uinfo.isPlaying());
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
			
			if(this.isGame())
			{
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
				
			}
			as_plist.put("sid" + entry.getKey(),as_player);
		}
		
		response.putNumber("bsid",this.pokerGame.getBankerSeatId());
		response.put("plist",as_plist);
		
		return response;
	}
	
	public Map<Integer,Player> userListToPlayerMap()
	{
		Map<Integer,Player> map = new HashMap<Integer,Player>();
		map.putAll(this.playerMap);
		return map;
	}
	
	private static AtomicInteger autoId = new AtomicInteger(0);
	
	private int roomid;
	
	private String name;
	
	private String roomType = "";
	
	private Map<String, RoomVariable> roomVariables = new HashMap<String, RoomVariable>();
	
	//进入并且坐下的玩家
	private Map<Integer,User> userMap = new Hashtable<Integer,User>();
	
	private Map<Integer,Player> playerMap = new Hashtable<Integer,Player>();
	
	//进入房间，但是尚未坐下的玩家
	private Set<User> spectatorList = Collections.synchronizedSet( new HashSet<User>() );
	
	private boolean isPrivate;
	
	private boolean isLimbo;
	
	private String zone;
	
	public String creator;
	
}
