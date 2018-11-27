package com.yl.service;

import java.util.Map;

import com.yl.container.ActionscriptObject;
import com.yl.entity.Puke;


public interface CmdPukeOperate
{
	// 获取桌面信息
	public ActionscriptObject handleRoomInfoRequest();

	// 看牌
	public ActionscriptObject handleLookCardRequest();

	// 跟注
	public ActionscriptObject handleFollowBetRequest();

	// 弃牌
	public ActionscriptObject handleDropCardRequest();

	// 全下
	public ActionscriptObject handleAllInRequest();

	// 加注
	public ActionscriptObject handleAddBetRequest();

	// 坐下
	public ActionscriptObject handleSitDownRequest();

	// 站起
	public ActionscriptObject handleStandUpRequest();

	public ActionscriptObject handleSystemStandUpRequest();

	// 用户离开
	public ActionscriptObject handleLeaveRequest();

	// 每一轮结束
	public ActionscriptObject handleRoundOverRequest();

	// 轮到谁！
	public ActionscriptObject handleWhoTurnRequest();

	// 结束分配筹码
	public ActionscriptObject handleDispatchBetRequest();

	// 开始游戏
	public ActionscriptObject handleStartGameRequest(Map<Integer,Puke> randomPuke);

}
