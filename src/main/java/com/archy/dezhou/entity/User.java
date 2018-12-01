package com.archy.dezhou.entity;

import java.util.HashMap;


import java.util.Map;
import java.util.logging.Logger;


import com.archy.dezhou.container.ActionscriptObject;


public class User
{
	private int uid = 0;
	private String roomType = "pt";
	private String name = "";
	private String pic = "";
	private String password ="";
	private String roomKey = "";
	private int seatId = -1;

	private int roomid = -1;
	private boolean online = false;
	private boolean isPlaying = false;

	private int roommoney = 0; //带入房间的钱， 变量名与数据库保持一致，主要用于在新用户注册时给rmoney传值
	private int allmoney = 0;  //点数
	private int exprience = 0;
	private int gold = 0;
	private int payGold = 0;
//	private int achievement = 0;
	
	private String sessionkey = "";
	private long ifrewards = 0; // 是否给予奖励 （变量与数据库保持一致）
	private int iftest = 0;
	
	private int id = 0; //道具ID

	private String mobile = "";
	private volatile boolean saveUpdate = false;
	private int maxuid = 0;
	private int rcount = 0;
	
	private int ctype = 0; //成就类型?
	private int num = 0;  //成就数量?
	private String gettime = ""; //获得成就的时间
	
	/**玩家的成就列表**/
	private HashMap<String,  HashMap<String, Integer>> achList = new HashMap<String,  HashMap<String, Integer>>(); 
	private ActionscriptObject achieveList = new ActionscriptObject();
	protected Logger log = Logger.getLogger(getClass().getName());
	private int winTzCount = 0;
	private int lostTzCount = 0;
	private int tributeBet = 0;
	private long baceMPoint = 0;
	private long coolDownTime = 0;
	private int tzmatchpoint = 0;
	private int zfmatchpoint = 0;
	private int cmatchpoint = 0;
	private String cdtype = ""; // zf tz
	private String status = "";
	private String dateStr = "";

	private int pklevel = 0; // 扑克的等级

	private int firstRoundBet = 0; // 第一轮押注筹码
	private int secondRoundBet = 0; // 第二轮押注筹码
	private int thirdRoundBet = 0; // 第三轮押注筹码
	private int fourthRoundBet = 0; // 第四轮押注筹码

	private String email = ""; // 邮箱地址
	private int level = 0; // 等级
	private String sex = ""; // 性别
	private String address = ""; // 住址
	private String regtime = ""; // 注册时间
	private String birthday = ""; // 生日
	private String counttime = ""; // 统计时间
	private String logintime = ""; // 登陆时间
	private String exittime = ""; // 退出时间
	private int loginnum = 0; // 登陆次数
	private int showtype = 0; // 显示类型

	private int functiontype = 0; // 类型？
	private int usecount = 0; // 道具使用次数
	private int visitnum = 0; // 访问次数
	private String muid = ""; // 手机号码
	private int winnum = 0; // 胜利次数
	private int completenum = 0; // 失败次数
	private Map<Integer, Puke> maxHand = new HashMap<Integer, Puke>();
	private long maxTMoney = 0;

	private long lastUpdateTime = System.currentTimeMillis(); // 最后修改时间
	
	public String lastAdwardsTime = "0";
//	public String MaxHandPuker = new String("");
	
	private long oldMaxHandPuker = 0;
	
	private String maxhandstr= "";
	private long maxhandvalue = 0;
	public String isTeachFinished = new String("-1");

	private Integer roomId;

	public int[][] diamondList = new int[][]
	{
	{ 113, 0 },
	{ 114, 0 },
	{ 115, 0 },
	{ 116, 0 }, };
	public ActionscriptObject MyEquipedDaoju = new ActionscriptObject();

	public int vipid = -1;
	/**
	 * 用来存放玩家携带的道具列表
	 */

	private ActionscriptObject DaojuList = new ActionscriptObject();
	/*****************************************************************************************************/
	public User()
	{

	}

    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }

    public long getIfRewards()
	{
		return ifrewards;
	}
	
	public String getSessionKey()
	{
		return sessionkey;
	}
	
	public int getRcount()
	{
		return rcount;
	}
	
	public boolean isSaveUpdate()
	{
		return saveUpdate;
	}
	
	public void setSaveUpdate(boolean saveUpdate)
	{
		this.saveUpdate = saveUpdate;
	}
	public int getMaxUid()
	{
		return maxuid;
	}
	
	public int getPropId()
	{
		return id;
	}
	
	public int getCtype()
	{
		return ctype;
	}
	
	public void addCtype(int ctype)
	{
		this.ctype = ctype;
	}
	
	public int getNum()
	{
		return num;
	}
	
	public void addNum(int num)
	{
		this.num = num;
	}
	
	public String getGetTime()
	{
		return gettime;
	}

	public void setPropId(int id)
	{
		this.id = id;
	}
	public void setSessionKey(String sessionkey)
	{
		this.sessionkey = sessionkey;
	}
	public void setIfRewards(long ifrewards)
	{
		this.ifrewards = ifrewards;
	}
	public void setIfTest(int iftest)
	{
		this.iftest = iftest;
	}
	public ActionscriptObject GetDaojuList()
	{
		return DaojuList;
	}

	public void SetDaojuLis(ActionscriptObject DaojuList)
	{
		this.DaojuList = DaojuList;
	}

	public int getFirstRoundBet()
	{
		return firstRoundBet;
	}

	public void setFirstRoundBet(int firstRoundBet)
	{
		this.firstRoundBet = firstRoundBet;
	}

	public int getSecondRoundBet()
	{
		return secondRoundBet;
	}

	public void setSecondRoundBet(int secondRoundBet)
	{
		this.secondRoundBet = secondRoundBet;
	}

	public int getThirdRoundBet()
	{
		return thirdRoundBet;
	}

	public void setThirdRoundBet(int thirdRoundBet)
	{
		this.thirdRoundBet = thirdRoundBet;
	}

	public int getFourthRoundBet()
	{
		return fourthRoundBet;
	}

	public void setFourthRoundBet(int fourthRoundBet)
	{
		this.fourthRoundBet = fourthRoundBet;
	}

	public int getAchievement()
	{
		return num;
	}

	public void setAchievement(int achievement)
	{
		this.num = achievement;
	}

	public int getWinNum()
	{
		return winnum;
	}

	public void addWinNum()
	{
		this.winnum++;
	}

	public int getCompletePkNum()
	{
		return completenum;
	}

	public void addCompletePkNum()
	{
		this.completenum++;
	}

	public long getmaxTMoney()
	{
		if (maxTMoney < 1)
			maxTMoney = getAMoney();
		return maxTMoney;
	}

	public void setmaxTMoney(long maxTMoney)
	{
		if (maxTMoney < 1)
			maxTMoney = getAMoney();
		else
			this.maxTMoney = maxTMoney;
	}

    public boolean isStandUpExpired(long time)
    {
        return false;
    }

    public boolean isLeaveExpired(long time)
    {
        return false;
    }

	public Map<Integer, Puke> getMaxHand()
	{
		return maxHand;
	}

	public void setMaxHand(Map<Integer, Puke> maxHand)
	{
		this.maxHand = maxHand;
	}

	public int getTributeBet()
	{
		return tributeBet;
	}

	public void setTributeBet(int tributeBet)
	{
		this.tributeBet = tributeBet;
	}

	public boolean isPlaying()
	{
		return isPlaying;
	}

	public void setPlaying(boolean isPlaying)
	{
		this.isPlaying = isPlaying;
	}

	public boolean isOline()
	{
		return online;
	}

	public void setOline(boolean isOline)
	{
		this.online = isOline;
	}

	public String getRoomType()
	{
		return roomType;
	}

	public void setRoomType(String roomType)
	{
		this.roomType = roomType;
	}

	public int getAMoney()
	{
		return allmoney;
	}

	public void setAmoney(int mtmoneyoney)
	{
		this.allmoney = mtmoneyoney;
	}
	
	public void addAmoney(int money)
	{
		if(money > 0)
		{
			this.allmoney += money;
		}
	}
	
	public void deductAmoney(int money)
	{
		if(money < this.allmoney)
		{
			this.allmoney -= money;
		}
		else
		{
			this.allmoney = 0;
		}
	}
	

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public int getWinTzCount()
	{
		return winTzCount;
	}

	public void setWinTzCount(int winTzCount)
	{
		this.winTzCount = winTzCount;
	}

	public Integer getUid()
	{
		return uid;
	}

	public String getPassWord()
	{
		return password;
	}
	public void setPassWord(String password)
	{
		this.password = password;
	}
	public void setUid(int uid)
	{
		this.uid = uid;
	}

	public int getLostTzCount()
	{
		return lostTzCount;
	}

	public void setLostTzCount(int lostTzCount)
	{
		this.lostTzCount = lostTzCount;
	}

	public String getPic()
	{
		return pic;
	}

	public void setPic(String pic)
	{
		this.pic = pic;
	}

	public int getExprience()
	{
		return exprience;
	}

	public void setExprience(int exprience)
	{
		this.exprience = exprience;
	}

	public int getGold()
	{
		return gold;
	}

	public void setGold(int gold)
	{
		this.gold = gold;
	}

	public int getLevel()
	{
		return level;
	}

	public void setLevel(int level)
	{
		this.level = level;
	}

	public String getRoomKey()
	{
		return roomKey;
	}

	public void setRoomKey(String roomKey)
	{
		this.roomKey = roomKey;
	}

	public int getRoomid()
	{
		return roomid;
	}

	public void setRoomid(int roomid)
	{
		this.roomid = roomid;
	}

	public int getRmoney()
	{
		return roommoney;
	}

	public void clearRoomMoney()
	{
		this.roommoney = 0;
	}
	
	public void addRmoney(int rmoney)
	{
		if(rmoney >= 0)
		{
			this.roommoney += rmoney;
		}
	}
	public void deductRmoney(int rmoney)
	{
		if(rmoney >= 0 && rmoney <= this.roommoney)
		{
			this.roommoney -= rmoney;
		}
	}

	public boolean isOffLine()
    {
        return false;
    }
	

	public void setBackupRmoney(int rmoney)
	{
	}

	public int getTzMatchPoint()
	{
		return tzmatchpoint;
	}

	public void setTzMatchPoint(int hPointtzMatchPoint)
	{
		this.tzmatchpoint = hPointtzMatchPoint;
	}

	public int getZfMatchPoint()
	{
		return zfmatchpoint;
	}

	public void setZfMatchPoint(int zfMatchPoint)
	{
		this.zfmatchpoint = zfMatchPoint;
	}

	public int getCMatchPoint()
	{
		return cmatchpoint;
	}

	public void setCMatchPoint(int matchPoint)
	{
		cmatchpoint = matchPoint;
	}

	public long getBaceMPoint()
	{
		return baceMPoint;
	}

	public void setBaceMPoint(long baceMPoint)
	{
		this.baceMPoint = baceMPoint;
	}

	public long getCoolDownTime()
	{
		return coolDownTime;
	}

	public void setCoolDownTime(long coolDownTime)
	{
		this.coolDownTime = coolDownTime;
	}

	public String getCdtype()
	{
		return cdtype;
	}

	public void setCdtype(String cdtype)
	{
		this.cdtype = cdtype;
	}

	public String getLtStatus()
	{
		return status;
	}

	public void setLtStatus(String ltStatus)
	{
		this.status = ltStatus;
	}

	public int getPayGold()
	{
		return payGold;
	}

	public void setPayGold(int payGold)
	{
		this.payGold = payGold;
	}

	public int getPklevel()
	{
		return pklevel;
	}

	public void setPklevel(int pklevel)
	{
		this.pklevel = pklevel;
	}

	public String getDateStr()
	{
		return dateStr;
	}

	public void setDateStr(String dateStr)
	{
		this.dateStr = dateStr;
	}


	public int getUserLevel()
	{
		return level;
	}

	public void setUserLevel(int level)
	{
		this.level = level;
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public String getGendar()
	{
		return sex;
	}

	public void setGendar(String gendar)
	{
		this.sex = gendar;
	}

	public String getAddress()
	{
		return address;
	}

	public void setAddress(String address)
	{
		this.address = address;
	}

	public String getRegtime()
	{
		return regtime;
	}

	public void setRegtime(String regtime)
	{
		this.regtime = regtime;
	}

	public String getBirthday()
	{
		return birthday;
	}

	public void setBirthday(String birthday)
	{
		this.birthday = birthday;
	}

	public String getCounttime()
	{
		return counttime;
	}

	public void setCounttime(String counttime)
	{
		this.counttime = counttime;
	}

	public String getLogintime()
	{
		return logintime;
	}

	public void setLogintime(String logintime)
	{
		this.logintime = logintime;
	}

	public String getExittime()
	{
		return exittime;
	}

	public void setExittime(String exittime)
	{
		this.exittime = exittime;
	}

	public int getLoginNum()
	{
		return loginnum;
	}

	public void setLoginNum(int loginNum)
	{
		this.loginnum = loginNum;
	}

	public int getShowType()
	{
		return showtype;
	}

	public void setShowType(int showType)
	{
		this.showtype = showType;
	}

	public int getFunctiontype()
	{
		return functiontype;
	}

	public void setFunctiontype(int functiontype)
	{
		this.functiontype = functiontype;
	}

	public int getUsecount()
	{
		return usecount;
	}

	public void setUsecount(int usecount)
	{
		this.usecount = usecount;
	}

	public int getVisitnum()
	{
		return visitnum;
	}

	public void setVisitnum(int visitnum)
	{
		this.visitnum = visitnum;
	}

	public String getMobile()
	{
		return muid;
	}

	public void setMobile(String mobile)
	{
		this.muid = mobile;
	}

	public long getLastUpdateTime()
	{
		return lastUpdateTime;
	}

	public void setLastUpdateTime(Long TimeStamp)
	{
		this.lastUpdateTime = TimeStamp;
	}
	
	public void setSeatId(int seatId)
	{
		this.seatId = seatId;
	}
	
	public int getSeatId()
	{
		return this.seatId;
	}

	public HashMap<String, HashMap<String, Integer>> getAchList()
	{
		return achList;
	}
	
	public void setAchList(HashMap<String, HashMap<String, Integer>> achlList)
	{
		this.achList = achlList;
	}
	
	public ActionscriptObject getAcheiveList()
	{
		return achieveList;
	}
	
	public void addAcheiveList(ActionscriptObject acheive)
	{
		this.achieveList = acheive;
	}
	
	public String GetMaxhandStringFromObj(Map<Integer,Puke> pkmap)
	{
		byte[] newdMaxPukerInts = new byte[10];
		String rtnString = "";
		int j = 0;
		for(Integer key:pkmap.keySet())
		{
			Puke puke = pkmap.get(key);
			newdMaxPukerInts[j] = (byte)puke.getNum();
			String tag = puke.getTag();
			if(tag.equals("A"))
				newdMaxPukerInts[j+5] = 0;
			else if(tag.equals("B"))
				newdMaxPukerInts[j+5] = 1;
			else if(tag.equals("C"))
				newdMaxPukerInts[j+5] = 2;
			else if(tag.equals("D"))
				newdMaxPukerInts[j+5] = 3;
			j++;
			if(j>4)
				break;
		}

		for(int i = 0;i<newdMaxPukerInts.length;i++)
		{
			if(i==newdMaxPukerInts.length-1)
				rtnString +=newdMaxPukerInts[i]+"";
			else
				rtnString +=newdMaxPukerInts[i]+"@";
		}
		return rtnString;
	}
	

	public long getOldMaxHandPuke()
	{
		return oldMaxHandPuker;
	}
	
	public void setOldMaxHandPuke(long oldMaxHandPuke)
	{
		this.oldMaxHandPuker = oldMaxHandPuke;
	}
	
	public long getMaxHandValue()
	{
		return maxhandvalue;
	}
	
	public String getMaxHandStr()
	{
		return maxhandstr;
	}
	
	public void setMaxHandStr(String maxhandstr)
	{
		this.maxhandstr = maxhandstr;
	}
	
	public void setMaxHandValue(long maxhandvalue)
	{
		this.maxhandvalue = maxhandvalue;
	}
}
