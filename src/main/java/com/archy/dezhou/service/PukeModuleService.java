package com.archy.dezhou.service;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

import com.archy.dezhou.entity.Player;
import com.archy.dezhou.entity.Puke;

public interface PukeModuleService
{
	/**
	 * 生成扑克集合
	 * 
	 */
	Map<Integer, Puke> Puke();

	/**
	 * 根据玩家的人数，随机获得底牌的位置
	 * 
	 * @param size
	 * @return
	 */
	int[] generateRandomNumber(int size, int type);

	/**
	 * 判断任意五张牌的级别
	 * 
	 * @param args
	 */
	int getLevel(HashMap map);

	/**
	 * 判断是否是皇家同花顺
	 * 
	 * @param args
	 */
	boolean isBigFive(HashMap map);

	/**
	 * 判断是否是同花顺
	 * 
	 * @param args
	 */
	boolean isFive(HashMap map);

	/**
	 * 判断是否是同花
	 * 
	 * @param args
	 */
	boolean isFiveTag(HashMap map);

	/**
	 * 判断是否是顺子
	 * 
	 * @param args
	 */
	boolean isStraight(HashMap map);

	/**
	 * 从21中组合中找出最大五张牌的组合
	 * 
	 * @param args
	 */
	List<Puke> MaxFive(List<Puke> map);

	void sortMap(HashMap<Integer, Puke> pjMap);

	/**
	 * 比较玩家的最大牌
	 * 
	 * @param args
	 */
	List<Player> sortPlayerByPukeMap(List<Player> players);

	String pkPosition(HashMap<Integer, Puke> pkmap, HashMap<Integer, Puke> userPk);
	
	int compareTwoPukeMap(HashMap<Integer, Puke> leftPukeMap, HashMap<Integer, Puke> rightPukeMap);

}
