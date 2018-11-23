package com.yl.service;

import com.yl.vo.Player;
import com.yl.vo.UserInfo;

public interface MemoryUpdateService
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
			String muid, String flag);

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
			String buid);

	/**
	 * 站起，离开更新userInfo
	 * 
	 * @param p
	 */
	public void userInfoUpdate(UserInfo uinfo);
}
