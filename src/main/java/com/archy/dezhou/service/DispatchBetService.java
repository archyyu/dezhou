package com.archy.dezhou.service;

import java.util.HashMap;

import com.archy.dezhou.entity.RoundBet;

public interface DispatchBetService
{
	/**
	 * 计算每一局中，每一轮,每个人下注的筹码
	 */
	int countBetByPlayer(String userid, int turn, int round, HashMap rmap);

	/**
	 * 计算每一局中，每一轮下注的筹码
	 */
	int countBetByRound(int turn, int round, HashMap rmap);

	/**
	 * 将一局4轮的筹码保存在poolmap中
	 */
	HashMap roundBetToPool(int turn, HashMap rmap);

	/**
	 * 赢家筹码分配
	 */
	HashMap dipatchBet(HashMap rmap,
					   HashMap<Integer, HashMap<Integer, RoundBet>> poolmap);
}
