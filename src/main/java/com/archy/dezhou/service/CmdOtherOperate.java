package com.archy.dezhou.service;

import java.util.HashMap;

import com.archy.dezhou.container.ActionscriptObject;
import com.archy.dezhou.entity.Prop;

public interface CmdOtherOperate
{
	// 30分钟计划
    ActionscriptObject handleTimePlanRequest();

	// 添加牌友
    ActionscriptObject handleAddPKFriendRequest();

	// 刷新牌友
    ActionscriptObject handleFlushPKFriendRequest();

	// 接受邀请
    ActionscriptObject handleReceivePKFriendRequest();

	// 拒绝牌友
    ActionscriptObject handleRefusePKFriendRequest();

	// 刷新成就列表
    ActionscriptObject handleRefuseAchievementRequest();

	// 购买道具
    ActionscriptObject handleBuyDaoJuRequest(
            HashMap<String, Prop> propMap);

	// 赠送礼品
    ActionscriptObject handleSendDaoJuRequest(
            HashMap<String, Prop> propMap);

	// 使用道具
    ActionscriptObject handleUseDaoJuRequest(
            HashMap<String, Prop> propMap);

	// 兑换金币，筹码
    ActionscriptObject handleExchangeGoldAndBetRequest();

	// 道具和礼品列表
    ActionscriptObject handleDaoJuAndGiftRequest();

	// 消息置顶
    ActionscriptObject handleTopMessageRequest();

	// 删除牌友
    ActionscriptObject handleDelPKFriendRequest();

	// 监听用户进入普通区
    ActionscriptObject handleCommonZoneRequest();

	// 单个的用户信息
    ActionscriptObject handleUserInfoRequest();

	// 冷却加速
    ActionscriptObject handleSpeedCoolDownRequest();

	// 每天赠送5000筹码
    ActionscriptObject handleSendBetEveryDayRequest();

	// 是否满足游戏开始条件
    ActionscriptObject handleCheckSbotRequest();

	// 储值
    ActionscriptObject handleConfirmStoreRequest();

	// 升级 告知房间其他玩家
    ActionscriptObject handleLevelUpRequest();
}
