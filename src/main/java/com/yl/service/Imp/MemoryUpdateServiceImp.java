package com.yl.service.Imp;

import java.util.HashMap;

import com.yl.Global.UserInfoMemoryCache;
import com.yl.service.MemoryUpdateService;
import com.yl.util.Utils;
import com.yl.vo.Campaign;
import com.yl.vo.GoldMatchInfo;
import com.yl.vo.Player;
import com.yl.vo.UserInfo;

public class MemoryUpdateServiceImp implements MemoryUpdateService
{
	/**
	 * 征服结束更新内存
	 * 
	 * @param zworl
	 * @param zuid
	 * @param buid
	 * @param muid
	 * @param flag
	 */
	public void ZhengFuOver(String zworl, String zuid, String buid,
			String muid, String flag)
	{
		HashMap<String, Campaign> mmap = null;
		if (zworl.equals("win"))
		{
			if (flag.equals("removebind"))
			{
				UserInfoMemoryCache.leadInfo.put(zuid, "noleader");
				mmap = UserInfoMemoryCache.memberInfo.get(buid);
				if (mmap.containsKey(zuid))
				{
					mmap.remove(zuid);
				}
			}
			else if (flag.equals("challenge"))
			{
				UserInfoMemoryCache.leadInfo.put(buid, zuid);
				mmap = UserInfoMemoryCache.memberInfo.get(zuid);
				if (mmap == null)
				{
					mmap = new HashMap<String, Campaign>();
				}
				Campaign cp = new Campaign();
				cp.setLuid(zuid);
				cp.setMuid(buid);
				cp.setTimemillis(Utils.retCurrentTiem());
				cp.setTributebet(0);
				mmap.put(buid, cp);
			}
			else if (flag.equals("fight"))
			{
				UserInfoMemoryCache.leadInfo.put(muid, zuid);
				mmap = UserInfoMemoryCache.memberInfo.get(zuid);
				if (mmap == null)
				{
					mmap = new HashMap<String, Campaign>();
				}
				Campaign cp = new Campaign();
				cp.setLuid(zuid);
				cp.setMuid(muid);
				cp.setTimemillis(Utils.retCurrentTiem());
				cp.setTributebet(0);
				mmap.put(buid, cp);
			}
		}
		else
		{
			UserInfoMemoryCache.leadInfo.put(zuid, buid);
			mmap = UserInfoMemoryCache.memberInfo.get(buid);
			if (mmap == null)
			{
				mmap = new HashMap<String, Campaign>();
			}
			Campaign cp = new Campaign();
			cp.setLuid(buid);
			cp.setMuid(zuid);
			cp.setTimemillis(Utils.retCurrentTiem());
			cp.setTributebet(0);
			mmap.put(zuid, cp);
		}
	}

	/**
	 * 挑战结束更新内存
	 * 
	 * @param ztm
	 * @param zexp
	 * @param zuid
	 * @param btm
	 * @param bexp
	 * @param buid
	 */
	public void tiaoZhanOver(int ztm, int zexp, String zuid, int btm, int bexp,
			String buid)
	{
		UserInfo zuinfo = UserInfoMemoryCache.getUserInfo(zuid);
		UserInfo buinfo = UserInfoMemoryCache.getUserInfo(buid);
		zuinfo.setAmoney(ztm);
		zuinfo.setExprience(zexp);

		zuinfo.setPlaying(false);
		zuinfo.setCdtype("tz");

		buinfo.setPlaying(false);
		buinfo.setAmoney(btm);
		buinfo.setExprience(bexp);
	}

	/**
	 * 站起，离开更新userInfo
	 * 
	 * @param p
	 */
	public void userInfoUpdate(UserInfo uinfo)
	{
		uinfo.setPlaying(false);
		uinfo.setAmoney(uinfo.getAMoney() + uinfo.getRmoney());
		uinfo.clearRoomMoney();
		GoldMatchInfo gminfo = uinfo.getGmInfo();
		if (gminfo != null)
		{
			uinfo.setGold(uinfo.getGold() + gminfo.getRoomGold());
			gminfo.setRoomGold(0);
		}
	}
}
