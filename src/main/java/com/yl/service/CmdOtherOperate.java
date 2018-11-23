package com.yl.service;

import java.util.HashMap;

import com.yl.container.ActionscriptObject;
import com.yl.vo.Prop;

public interface CmdOtherOperate
{
	// 30分钟计划
	public ActionscriptObject handleTimePlanRequest();

	// 添加牌友
	public ActionscriptObject handleAddPKFriendRequest();

	// 刷新牌友
	public ActionscriptObject handleFlushPKFriendRequest();

	// 接受邀请
	public ActionscriptObject handleReceivePKFriendRequest();

	// 拒绝牌友
	public ActionscriptObject handleRefusePKFriendRequest();

	// 刷新成就列表
	public ActionscriptObject handleRefuseAchievementRequest();

	// 购买道具
	public ActionscriptObject handleBuyDaoJuRequest(
			HashMap<String, Prop> propMap);

	// 赠送礼品
	public ActionscriptObject handleSendDaoJuRequest(
			HashMap<String, Prop> propMap);

	// 使用道具
	public ActionscriptObject handleUseDaoJuRequest(
			HashMap<String, Prop> propMap);

	// 兑换金币，筹码
	public ActionscriptObject handleExchangeGoldAndBetRequest();

	// 道具和礼品列表
	public ActionscriptObject handleDaoJuAndGiftRequest();

	// 消息置顶
	public ActionscriptObject handleTopMessageRequest();

	// 删除牌友
	public ActionscriptObject handleDelPKFriendRequest();

	// 监听用户进入普通区
	public ActionscriptObject handleCommonZoneRequest();

	// 单个的用户信息
	public ActionscriptObject handleUserInfoRequest();

	// 冷却加速
	public ActionscriptObject handleSpeedCoolDownRequest();

	// 每天赠送5000筹码
	public ActionscriptObject handleSendBetEveryDayRequest();

	// 是否满足游戏开始条件
	public ActionscriptObject handleCheckSbotRequest();

	// 储值
	public ActionscriptObject handleConfirmStoreRequest();

	// 升级 告知房间其他玩家
	public ActionscriptObject handleLevelUpRequest();
}
