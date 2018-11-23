package com.yl.service;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

import com.yl.vo.Player;
import com.yl.vo.Puke;

public interface PukeModuleService
{
	/**
	 * 生成扑克集合
	 * 
	 */
	public Map<Integer,Puke> Puke();

	/**
	 * 根据玩家的人数，随机获得底牌的位置
	 * 
	 * @param size
	 * @return
	 */
	public int[] generateRandomNumber(int size, int type);

	/**
	 * 判断任意五张牌的级别
	 * 
	 * @param args
	 */
	public int getLevel(HashMap map);

	/**
	 * 判断是否是皇家同花顺
	 * 
	 * @param args
	 */
	public boolean isBigFive(HashMap map);

	/**
	 * 判断是否是同花顺
	 * 
	 * @param args
	 */
	public boolean isFive(HashMap map);

	/**
	 * 判断是否是同花
	 * 
	 * @param args
	 */
	public boolean isFiveTag(HashMap map);

	/**
	 * 判断是否是顺子
	 * 
	 * @param args
	 */
	public boolean isStraight(HashMap map);

	/**
	 * 从21中组合中找出最大五张牌的组合
	 * 
	 * @param args
	 */
	public List<Puke> MaxFive(List<Puke> map);

	public void sortMap(HashMap<Integer,Puke> pjMap);

	/**
	 * 比较玩家的最大牌
	 * 
	 * @param args
	 */
	public List<Player> sortPlayerByPukeMap(List<Player> players);

	public String pkPosition(HashMap<Integer, Puke> pkmap,HashMap<Integer, Puke> userPk);
	
	public int compareTwoPukeMap(HashMap<Integer,Puke> leftPukeMap,HashMap<Integer,Puke> rightPukeMap);

}
