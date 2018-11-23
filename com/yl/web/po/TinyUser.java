package com.yl.web.po;

public class TinyUser
{
	/**
	 * 用户ID
	 */

	private String uid = "";
	/**
	 * 昵称
	 */

	private String name = "";

	/**
	 * 性别
	 */
	private String sex = "";

	/**
	 * 生日
	 */

	private String bir = "";

	/**
	 * 住址
	 */

	private String address = "";
	/**
	 * 状态
	 */

	private String status = "";

	/**
	 * 是否在线
	 */
	private String online = "";

	/**
	 * 照片
	 */
	private String pic = "";
	/**
	 * 我的邮箱地址
	 */
	private String email = "";

	/**
	 * 金钱
	 */

	private long money = 0;

	private String myads;

	/**
	 * 注册时间
	 */
	private String regtime = "";

	/**
	 * 登陆时间
	 */
	private String logintime = "";

	/**
	 * 退出时间
	 */
	private String exittime = "";

	/**
	 * 金币数
	 */
	private int gold = 0;

	/**
	 * 经验
	 */
	private int exprience;

	/**
	 * 等级
	 */
	private int level = 0;

	/**
	 * 赌桌上携带的金钱
	 */
	private int roommoney = 0;

	private int zfmatchpoint = 0;
	private int tzmatchpoint = 0;
	private int cmatchpoint = 0;
	private int loginnum = 0;
	private int showtype = 0;
	private int functiontype = 0;
	private int usecount = 0;
	private int visitnum = 0;

	public TinyUser()
	{
		
	}
	
	public String getUid()
	{
		return uid;
	}

	public void setUid(String uid)
	{
		this.uid = uid;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getPic()
	{
		return pic;
	}

	public void setPic(String pic)
	{
		this.pic = pic;
	}

	public long getMoney()
	{
		return money;
	}

	public void setMoney(int money)
	{
		this.money = money;
	}

	public String getSex()
	{
		return sex;
	}

	public void setSex(String sex)
	{
		this.sex = sex;
	}

	public String getBir()
	{
		return bir;
	}

	public void setBir(String bir)
	{
		this.bir = bir;
	}

	public String getAddress()
	{
		return address;
	}

	public void setAddress(String address)
	{
		this.address = address;
	}

	public String getStatus()
	{
		return status;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}

	public String getOnline()
	{
		return online;
	}

	public void setOnline(String online)
	{
		this.online = online;
	}

	public String getMyads()
	{
		return myads;
	}

	public void setMyads(String myads)
	{
		this.myads = myads;
	}

	public String getLoginTime()
	{
		return logintime;
	}

	public void setLoginTime(String logintime)
	{
		this.logintime = logintime;
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public String getRegTime()
	{
		return regtime;
	}

	public void setgetRegTime(String regtime)
	{
		this.regtime = regtime;
	}

	public int getGold()
	{
		return gold;
	}

	public void setGold(int gold)
	{
		this.gold = gold;
	}

	public int getExprience()
	{
		return exprience;
	}

	public void setExprience(int exprience)
	{
		this.exprience = exprience;
	}

	public int getLevel()
	{
		return level;
	}

	public void setLevel(int level)
	{
		this.level = level;
	}

	public int getRoommoney()
	{
		return roommoney;
	}

	public void setRoommoney(int roommoney)
	{
		this.roommoney = roommoney;
	}

	public String getExitTtime()
	{
		return exittime;
	}

	public void setExitTtim(String exittime)
	{
		this.exittime = exittime;
	}

	public int getZfMatchPoint()
	{
		return zfmatchpoint;
	}

	public void setZfMatchPoint(int zfmatchpoint)
	{
		this.zfmatchpoint = zfmatchpoint;
	}

	public int getTzMatchPoint()
	{
		return tzmatchpoint;
	}

	public void setTzMatchPoint(int tzmatchpoint)
	{
		this.tzmatchpoint = tzmatchpoint;
	}

	public int getCMatchPoint()
	{
		return cmatchpoint;
	}

	public void setCMatchPoint(int cmatchpoint)
	{
		this.cmatchpoint = cmatchpoint;
	}

	public int getLoginNum()
	{
		return loginnum;
	}

	public void setLoginNum(int loginnum)
	{
		this.loginnum = loginnum;
	}

	public int getShowType()
	{
		return showtype;
	}

	public void setShowType(int showtype)
	{
		this.showtype = showtype;
	}

	public int getFunctionType()
	{
		return functiontype;
	}

	public void setFunctionType(int functiontype)
	{
		this.functiontype = functiontype;
	}

	public int getUseCount()
	{
		return usecount;
	}

	public void setUseCount(int usecount)
	{
		this.usecount = usecount;
	}

	public int getVisitNum()
	{
		return visitnum;
	}

	public void setVisitNum(int visitnum)
	{
		this.visitnum = visitnum;
	}

}
