package com.yl.dao;

import com.yl.entity.UserInfo;

public interface CommonDao
{
	/**
	 * 征服结束更新数据库
	 * 
	 * @param zworl
	 * @param zuid
	 * @param buid
	 * @param muid
	 * @param flag
	 * @return
	 */
	public void ZhengFuOver(String zworl, String zuid, String buid,
			String muid, String flag);

	/**
	 * 挑战结束更新数据库
	 * 
	 * @param ztm
	 * @param zexp
	 * @param zuid
	 * @param btm
	 * @param bexp
	 * @param buid
	 * @return
	 */
	public void tiaoZhanOver(UserInfo zuinfo, UserInfo buinfo, String zwol,
			int bet, int obet, int zexp, int bexp);

	/**
	 * 征服结束
	 * 
	 * @param zuinfo
	 * @param buinfo
	 * @param db
	 */
	public void zhenFuOver(UserInfo zuinfo, UserInfo buinfo, String wol);

	/**
	 * 删除牌友
	 * 
	 * @param muid
	 * @param fuid
	 * @param db
	 */
	public void delPKFriend(String muid, String fuid);

	/**
	 * 消息置顶
	 * 
	 * @param muid
	 * @param fuid
	 * @param db
	 */
	public void msgTop(String pg, String uid, int type);

	/**
	 * 更新用户信息
	 * 
	 * @param uinfo
	 */
	public void updateUinfo(UserInfo uinfo);

	/**
	 * 失去连接更新
	 * 
	 * @param uinfo
	 * @param db
	 */
	public void updateUinfoLost(UserInfo uinfo);


	/**
	 * 添加牌友
	 * 
	 * @param uinfo
	 * @param db
	 */
	public void addPKFriend(UserInfo muinfo, UserInfo fuinfo);

	/**
	 * 30分钟计划
	 * 
	 * @param uinfo
	 * @param db
	 */
	public void TimePlan(String uid, int time);


	/**
	 * 用户离开
	 * 
	 * @param uinfo
	 * @param db
	 */
	public void updateUserWhenLevel(UserInfo uinfo);

	public boolean checkAchievement(String uid, int type);

	public void insertAchievement(String uid, int type);

	public void updateAchievement(String uid, int type);

	public void delCreditCard(String uid, int type);

	public boolean delAchievement(String uid, int type);
}
