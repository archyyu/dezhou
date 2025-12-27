package com.archy.dezhou.entity.room;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.archy.dezhou.entity.User;
import com.archy.dezhou.global.ConstList;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

import com.archy.dezhou.GameCmdException;
import com.archy.dezhou.container.JsonObjectWrapper;
import com.archy.dezhou.entity.Player;
import com.archy.dezhou.entity.Puke;
import com.archy.dezhou.entity.RoomDB;

@Data
public class GameRoom
{

	@JsonIgnore
	private Logger log = LoggerFactory.getLogger(getClass());

	private int roomid;
	private String name;

	// Explicit getters for fields
	public int getRoomid() {
		return roomid;
	}

	public String getName() {
		return name;
	}

	public String creator;

	//进入房间，但是尚未坐下的玩家
	@JsonIgnore
	private Set<Player> spectatorList = Collections.synchronizedSet( new HashSet<Player>() );
	private boolean isLimbo;
	private String zone;
	private Integer minbuy;
	private Integer maxbuy;

	private Integer roomTypeId;

	private Integer bbet;
	private Integer sbet;
	private String showname;
	private String roomtype;

	private Integer maxPlayers;
	private Integer minPlayers;

	private String gameType;

	private static AtomicInteger autoId = new AtomicInteger(0);

	@JsonIgnore
	private Map<Integer,Player> playerMap = new Hashtable<Integer,Player>();

	public boolean isPlayerInRoom(Player user)
	{
		return this.playerMap.containsValue(user);
	}

	public Player getPlayerBySeat(int seatId) {
		return this.playerMap.get(seatId);
	}

	public GameRoom() {
		setRoomID();
	}

	public GameRoom(RoomDB roomDB)
	{
		setRoomID();

		
		this.roomTypeId = roomDB.getId();
		this.name = roomDB.getName();
		
		// this.zone = roomDB.getZone();
		// this.creator = roomDB.getCreator();
		this.bbet = roomDB.getBbet();
		this.sbet = roomDB.getSbet();
		this.minbuy = roomDB.getMinbuy();
		this.maxbuy = roomDB.getMaxbuy();
		this.showname = roomDB.getShowname();

	}

	public int getMaxSpectator()
	{
		return 100000;
	}
	
	private void addPlayer(int seatId,Player player)
	{
		this.playerMap.put(seatId,player);
	}
	
	public void removePlayer(int seatId)
	{
		this.playerMap.remove(seatId);
	}

	public boolean isSeatTaken(int seatId) {
		return this.playerMap.containsKey(seatId);
	}

	public List<Player> getPlayers()
	{
		return this.playerMap.values().stream().collect(Collectors.toList());
	}
	
	public JsonObjectWrapper playerSitDown(int seatId, Player player, int cb) throws GameCmdException
	{
		JsonObjectWrapper response = new JsonObjectWrapper();
		if(player == null)
		{
			throw new GameCmdException("playerNotExist");
		}

		if(this.playerMap.containsKey(seatId))
		{
			throw new GameCmdException("HaveAlreadySitDowned");
		}

		if(this.playerMap.values().contains(player))
		{
			throw new GameCmdException("YouHaveSitedAtOtherPlace");
		}


		log.info("roomName: " + this.getName() + "  " + player.getUid() + " try to sitdown at seatId: " + seatId + ", with cb:" + cb);
		this.spectatorList.remove(player);

		player.clearRoomMoney();
		player.addRmoney(cb);
		player.deductAmoney(cb);
		player.setSeatId(seatId);

		// this.addPlayer(seatId,player);

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

		this.addPlayer(seatId,player);
		this.notifyRoomPlayerButOne(response, ConstList.MessageType.MESSAGE_NINE,player.getUid());
		return response;
	}
	
	public boolean playerStandUp(Player player)
	{

		if (this.playerMap.containsKey(player.getSeatId()) == false) {
			return false;
		}

		log.warn("roomName: " + this.getName() + "  " + player.getUid() + " standup at seatId: " + player.getSeatId());
		this.removePlayer(player.getSeatId());
		this.spectatorList.add(player);

		return true;

	}
	

	public void removePlayerBySeat(int seatId) {
		this.playerMap.remove(seatId);
	}
	

	public int getPlayerCount() {
		return this.playerMap.size();
	}

	public int userJoin(Player u)
	{
		if (!this.spectatorList.contains(u))
		{
			u.setRoomId(getRoomid());
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

	public void removePlayerFromSpectaclors(Player player) {
		this.spectatorList.remove(player);
	}

	public boolean isTemp()
	{
		return false;
	}
	
	public int getMaxUsers()
	{
		return ConstList.MAXNUMPLAYEROFROOM;
	}

	
	public int getSpectatorCount()
	{
		return this.spectatorList.size();
	}

	public boolean isPlayerSeatOn(int seatId, int uid) {
		Player player = this.playerMap.get(seatId);
		if (player == null) {
			return false;
		}
		if (player.getUid() == uid) {
			return true;
		}
		return false;
	}
	
	public boolean isPlayerSitDown(Integer uId)
	{
		for(Map.Entry<Integer,Player> entry : this.playerMap.entrySet())
		{
			if(entry.getValue().getUid().equals(uId))
			{
				return true;
			}
		}
		return false;
	}

	public Player[] getAllUsers()
	{
		return this.playerMap.values().toArray(new Player[this.playerMap.size()]);
	}

	public int getUserCount()
	{
		return this.playerMap.size();
	}

	public int howManySpecators()
	{
		return this.spectatorList.size();
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

	public String getCreator()
	{
		return creator;
	}

	private void setRoomID()
	{
		roomid = autoId.getAndIncrement();
		this.minPlayers = 2;
		this.maxPlayers = 9;
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


	public boolean isGame()
	{
		return true;
	}

	public int howManyUsers()
	{
		return this.playerMap.size();
	}

	public boolean isUserInSeat(Player player)
	{
		return this.playerMap.values().contains(player);
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public Integer getMinbuy() {
		return minbuy;
	}

	public void setMinbuy(Integer minbuy) {
		this.minbuy = minbuy;
	}

	public Integer getMaxbuy() {
		return maxbuy;
	}

	public void setMaxbuy(Integer maxbuy) {
		this.maxbuy = maxbuy;
	}
	
	public Map<Integer,Player> userListToPlayerMap()
	{
		Map<Integer,Player> map = new HashMap<Integer,Player>();
		map.putAll(this.playerMap);
		return map;
	}

	public void notifyRoomPlayerButOne(JsonObjectWrapper aObj, ConstList.MessageType msgType, Integer uId)
	{
		long timeStamp = System.currentTimeMillis();
		Set<User> users = new HashSet<User>();

		synchronized (this.playerMap)
		{
			users.addAll(this.playerMap.values());
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
			// TODO 通知其他用户，我做了什么

		}
	}

	public boolean isUserPlaying(int uid) {
		for(Map.Entry<Integer, Player> entry : this.playerMap.entrySet())
		{
			if(entry.getValue().getUid().equals(uid))
			{
				return true;
			}
		}
		return false;
	}

	public void notifyRoomPlayer(JsonObjectWrapper aObj, ConstList.MessageType msgType)
	{
		long timeStamp = System.currentTimeMillis();
		Set<User> users = new HashSet<User>();

		synchronized (this.playerMap)
		{
			users.addAll(this.playerMap.values());
		}
		synchronized (this.spectatorList)
		{
			users.addAll(this.spectatorList);
		}

		//notify all the uers, game start
	}

	public JsonObjectWrapper playersToList() {
		JsonObjectWrapper as_plist = new JsonObjectWrapper();
		for(Map.Entry<Integer, Player> entry : this.playerMap.entrySet())
		{
			JsonObjectWrapper as_player = new JsonObjectWrapper();
			Player player = entry.getValue();

			JsonObjectWrapper dj_func = new JsonObjectWrapper();

			as_player.put("dj_func", dj_func);

			as_player.put("un",player.getAccount());
			as_player.putNumber("lev",0);
			as_player.putNumber("sid",entry.getKey());
			as_player.put("uid",player.getUid());

			as_player.putNumber("pkl",player.getPkLevel());
			as_player.putBool("isp",player.isPlaying());
			as_player.putNumber("tb",player.getTempBet());
			as_player.putNumber("yt",player.getYourTurn());

			as_player.putNumber("cm",player.getRmoney());
			as_player.putNumber("tm",player.getAMoney());
			as_player.putNumber("gs",player.getGameState().value());
			as_player.putNumber("ps",player.getPlayerState().value());


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
		return as_plist;
	}
	
	public Integer getBbet() {
		return bbet;
	}
	
	public void setBbet(Integer bbet) {
		this.bbet = bbet;
	}

	public Integer getSbet() {
		return sbet;
	}

	public void setSbet(Integer sbet) {
		this.sbet = sbet;
	}

	public String getShowname() {
		return showname;
	}

	public void setShowname(String showname) {
		this.showname = showname;
	}
	
}
