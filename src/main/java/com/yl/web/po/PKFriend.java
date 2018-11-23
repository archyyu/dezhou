package com.yl.web.po;

public class PKFriend
{
	/**
	 * 我的uid
	 */
	private String muid;
	/**
	 * 我的昵称
	 */
	private String mname;
	/**
	 * 我的照片
	 */

	private String mpic;
	/**
	 * 牌友的uid
	 */

	private String fuid;
	/**
	 * 牌友的昵称
	 */

	private String fname;
	/**
	 * 牌友的照片
	 */

	private String fpic;

	public String getMuid()
	{
		return muid;
	}

	public void setMuid(String muid)
	{
		this.muid = muid;
	}

	public String getMname()
	{
		return mname;
	}

	public void setMname(String mname)
	{
		this.mname = mname;
	}

	public String getMpic()
	{
		return mpic;
	}

	public void setMpic(String mpic)
	{
		this.mpic = mpic;
	}

	public String getFuid()
	{
		return fuid;
	}

	public void setFuid(String fuid)
	{
		this.fuid = fuid;
	}

	public String getFname()
	{
		return fname;
	}

	public void setFname(String fname)
	{
		this.fname = fname;
	}

	public String getFpic()
	{
		return fpic;
	}

	public void setFpic(String fpic)
	{
		this.fpic = fpic;
	}
}
