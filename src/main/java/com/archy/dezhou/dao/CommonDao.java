package com.archy.dezhou.dao;

import com.archy.dezhou.entity.UserInfo;

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
    void ZhengFuOver(String zworl, String zuid, String buid,
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
    void tiaoZhanOver(UserInfo zuinfo, UserInfo buinfo, String zwol,
                      int bet, int obet, int zexp, int bexp);

	/**
	 * 征服结束
	 * 
	 * @param zuinfo
	 * @param buinfo
	 * @param db
	 */
    void zhenFuOver(UserInfo zuinfo, UserInfo buinfo, String wol);

	/**
	 * 删除牌友
	 * 
	 * @param muid
	 * @param fuid
	 * @param db
	 */
    void delPKFriend(String muid, String fuid);

	/**
	 * 消息置顶
	 * 
	 * @param muid
	 * @param fuid
	 * @param db
	 */
    void msgTop(String pg, String uid, int type);

	/**
	 * 更新用户信息
	 * 
	 * @param uinfo
	 */
    void updateUinfo(UserInfo uinfo);

	/**
	 * 失去连接更新
	 * 
	 * @param uinfo
	 * @param db
	 */
    void updateUinfoLost(UserInfo uinfo);


	/**
	 * 添加牌友
	 * 
	 * @param uinfo
	 * @param db
	 */
    void addPKFriend(UserInfo muinfo, UserInfo fuinfo);

	/**
	 * 30分钟计划
	 * 
	 * @param uinfo
	 * @param db
	 */
    void TimePlan(String uid, int time);


	/**
	 * 用户离开
	 * 
	 * @param uinfo
	 * @param db
	 */
    void updateUserWhenLevel(UserInfo uinfo);

	boolean checkAchievement(String uid, int type);

	void insertAchievement(String uid, int type);

	void updateAchievement(String uid, int type);

	void delCreditCard(String uid, int type);

	boolean delAchievement(String uid, int type);
}
