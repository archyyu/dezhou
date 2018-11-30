package com.archy.dezhou.container;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.archy.dezhou.httpLogic.ConnectInstance;
import com.archy.dezhou.entity.room.Room;

public class User
{
	Logger log = Logger.getLogger(getClass());
	
	private int userid;
	private String zone;
	private String uid = "10000";
	private String name;
	private int roomId = 0;
	private long lastMessageTime;
	private long loginTime;
	
	private boolean isChallenging;
	
	private boolean markedForRemoval;
	private boolean blueBoxed;
	
	private boolean isPlayer;
	private boolean isPlaying;
	
	public boolean isBeingKicked;

	private boolean admin;
	private boolean moderator;
	private boolean buddyListChanged;
	private int userPrivileges;
	private HashMap<Room, Integer> playerIndexes;
	private String ipAddr;
	private long droppedPackets;
	private String bbSessionId;
	public int playerIndex;
	public int floodCounter;
	public int repeatedMsgCounter;
	public int floodWarningsCounter;
	public int badwordsWarningCounter;
	public int TimeOutOperateCounter;
	public String lastMessage;
	public String messageFlag = "none";

	// 百宝箱用户伪码
	public String bbx_userId = "";

	// 百宝箱32为登陆验证key
	public String bbx_userkey = "";

	// 百宝箱用户用户手机号码
	public String bbx_mobile = "";

	private volatile long operateTime = System.currentTimeMillis();
	
	private volatile long onlineHeartTime = System.currentTimeMillis();
	
	private volatile long sitdownTime = System.currentTimeMillis();

	public Map<Object, Object> properties;
	public Map<Object, Object> buddyVariables;

	// 赌桌交互数据
	private Queue<ConnectInstance> betMessageList = new LinkedList<ConnectInstance>();

	// 用户之间文本信息交互信息
	private Queue<ConnectInstance> bstMessageList = new LinkedList<ConnectInstance>();

	public User(String name)
	{
		setUserID();
		initUser();
		this.name = name;
		
		playerIndexes = new HashMap<Room, Integer>();
		properties = new HashMap<Object, Object>();
		buddyVariables = new ConcurrentHashMap<Object, Object>();
		admin = false;
		moderator = false;
		isBeingKicked = false;
		markedForRemoval = false;
		lastMessageTime = System.currentTimeMillis();
		floodCounter = 0;
		repeatedMsgCounter = 0;
		floodWarningsCounter = 0;
		badwordsWarningCounter = 0;
		droppedPackets = 0L;
		blueBoxed = false;
		bbSessionId = "This user was created manually";
	}

	public void clearBetMessageQuene()
	{
		betMessageList.clear();
	}

	public void clearBbsMessageQuene()
	{
		bstMessageList.clear();
	}

	public void sendBetMessage(ConnectInstance nextConnection)
	{
		betMessageList.offer(nextConnection);
	}
	
	public ConnectInstance popBetMessage()
	{
		return betMessageList.poll();
	}
	
	public int getBetMessageSize()
	{
		return this.betMessageList.size();
	}

	public void sendBbsMessage(ConnectInstance nextConnect)
	{
		bstMessageList.offer(nextConnect);
	}
	
	public ConnectInstance popBbsMessage()
	{
		return bstMessageList.poll();
	}
	
	public int getBbsMessageSize()
	{
		return this.bstMessageList.size();
	}

	public void initUser()
	{
		long t = System.currentTimeMillis();
		floodCounter = 0;
		loginTime = t;
		lastMessageTime = t;
		buddyListChanged = false;
	}

	public long getDroppedPackets()
	{
		return droppedPackets;
	}

	public void setDroppedPackets(long droppedPackets)
	{
		this.droppedPackets = droppedPackets;
	}

	public int getRoomId()
	{
		return roomId;
	}

	public void setRoomId(int id)
	{
		this.roomId = id;
	}

	public void increaseDroppedPackets()
	{
		droppedPackets++;
	}

	public boolean buddyListChanged()
	{
		return buddyListChanged;
	}

	public void setBuddyListChanged()
	{
		buddyListChanged = true;
	}

	public void setBuddyListSaved()
	{
		buddyListChanged = false;
	}

	public boolean isSpectator()
	{
		return getPlayerIndex() == -1;
	}

	public void setPlayerIndex(Room r, int id)
	{
		synchronized (playerIndexes)
		{
			playerIndexes.put(r, new Integer(id));
		}
	}

	public int getPlayerIndex()
	{
		return 0;
	}

	public int getPlayerIndex(Room r)
	{
		if(r == null)
		{
			return 0;
		}
		
		int res = -1;
		Integer id = playerIndexes.get(r);
		if (id != null)
			return id.intValue();
		return res;
	}

	public void setPrivilege(int p)
	{
		userPrivileges = p;
	}

	public int getPrivilege()
	{
		return userPrivileges;
	}

	public boolean isModerator()
	{
		return moderator;
	}

	public void setAsModerator()
	{
		moderator = true;
	}

	public void setAsModerator(boolean b)
	{
		moderator = b;
	}

	public boolean isAdmin()
	{
		return admin;
	}

	public void setAsAdmin()
	{
		admin = true;
	}

	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}

	public String getZone()
	{
		return zone;
	}

	public long getLastMessageTime()
	{
		return lastMessageTime;
	}

	public void setLastMessageTime(long lastMessageTime)
	{
		this.lastMessageTime = lastMessageTime;
	}

	public long getLoginTime()
	{
		return loginTime;
	}

	public void updateMessageTime()
	{
		lastMessageTime = System.currentTimeMillis();
	}

	private void setUserID()
	{
		userid = Integer.parseInt(uid);
	}

	public int getUserId()
	{
		return Integer.parseInt(uid);
	}


	public boolean isBusyForChallenge()
	{
		return isChallenging;
	}

	public void setBusyForChallenge(boolean status)
	{
		isChallenging = status;
	}

	public boolean isBusyForPlay()
	{
		return isPlaying;
	}

	public void setIsBusyForPlay(boolean status)
	{
		isPlaying = status;
	}

	public String getIpAddress()
	{
		return ipAddr;
	}

	public boolean isMarkedForRemoval()
	{
		return markedForRemoval;
	}

	public void setMarkedForRemoval(boolean b)
	{
		markedForRemoval = b;
	}

	public boolean isBlueBoxed()
	{
		return blueBoxed;
	}

	public void SetBlueBoxed(boolean blueBoxed)
	{
		this.blueBoxed = blueBoxed;
	}

	public String getBlueBoxSessionId()
	{
		return bbSessionId;
	}

	public void setBlueBoxSessionId(String bbSessionId)
	{
		this.bbSessionId = bbSessionId;
	}

	public User getChannel()
	{
		return this;
	}

	public String getUid()
	{
		return uid;
	}

	public void setUid(String uid)
	{
		this.uid = uid;
	}
	
	public void updateOperateTime()
	{
		this.operateTime = System.currentTimeMillis();
//		log.info("at time: " + System.currentTimeMillis() + " userId: " + this.getUid() + " beat heart");
	}
	
	public long getOperateTime()
	{
		return this.operateTime;
	}
	
	public boolean isStandUpExpired(long now)
	{
        return now - this.operateTime > 10 * 1000;
    }
	
	public boolean isLeaveExpired(long now)
	{
        return now - this.operateTime > 15 * 1000;
    }
	
	public boolean isPlaying()
	{
		return this.isPlaying;
	}
	
	public void startPlaying()
	{
		this.isPlaying = true;
	}
	
	public void stopPlaying()
	{
		this.isPlaying = false;
	}
	
	public boolean isPlayer()
	{
		return this.isPlayer;
	}
	
	public void setPlayer(boolean bool)
	{
		this.isPlayer = bool;
	}
	
	public void updateSitdownTime()
	{
		this.sitdownTime = System.currentTimeMillis();
	}
	
	public boolean isInSitdownCd()
	{
		int interval = (int)(System.currentTimeMillis() - this.sitdownTime);
        return interval < 3 * 1000;
    }
	
	public void updateOnlineHeatTime()
	{
		this.onlineHeartTime = System.currentTimeMillis();
	}
	
	public boolean isOffLine()
	{
		long now = System.currentTimeMillis();
        return (now - this.onlineHeartTime) >= 5 * 60 * 1000;
    }
	
}
