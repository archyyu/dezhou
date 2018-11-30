package com.archy.dezhou.service;

import java.util.Map;

import com.archy.dezhou.container.ActionscriptObject;
import com.archy.dezhou.entity.Puke;


public interface CmdPukeOperate
{
	// 获取桌面信息
    ActionscriptObject handleRoomInfoRequest();

	// 看牌
    ActionscriptObject handleLookCardRequest();

	// 跟注
    ActionscriptObject handleFollowBetRequest();

	// 弃牌
    ActionscriptObject handleDropCardRequest();

	// 全下
    ActionscriptObject handleAllInRequest();

	// 加注
    ActionscriptObject handleAddBetRequest();

	// 坐下
    ActionscriptObject handleSitDownRequest();

	// 站起
    ActionscriptObject handleStandUpRequest();

	ActionscriptObject handleSystemStandUpRequest();

	// 用户离开
    ActionscriptObject handleLeaveRequest();

	// 每一轮结束
    ActionscriptObject handleRoundOverRequest();

	// 轮到谁！
    ActionscriptObject handleWhoTurnRequest();

	// 结束分配筹码
    ActionscriptObject handleDispatchBetRequest();

	// 开始游戏
    ActionscriptObject handleStartGameRequest(Map<Integer, Puke> randomPuke);

}
