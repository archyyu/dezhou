package com.archy.dezhou.service.Imp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.archy.dezhou.Global.AchValue;
import com.archy.dezhou.Global.ConstList;
import com.archy.dezhou.service.AchievementService;
import com.archy.dezhou.service.PukeModuleService;
import com.archy.dezhou.entity.Puke;

public class AchievementServiceImp implements AchievementService
{
	private PukeModuleService pms;

	public AchievementServiceImp(PukeModuleService pms)
	{
		this.pms = pms;
	}

	public List<Object> getAchievement(HashMap<Integer, Puke> pkmap)
	{
		List<Object> achlist = new ArrayList<Object>();
		if (isRockets(pkmap))
		{
			// 红A一对, 火箭----1--ok
			achlist.add(AchValue.CHENJIU_WIN_ROCKETS);
			// 25次一对A---ok
			achlist.add(AchValue.CHENJIU_WIN_ACES25);
		}
		if (isCowboys(pkmap))
		{
			// 红K一对, 国王----2---ok
			achlist.add(AchValue.CHENJIU_WIN_COWBOYS);
			// 二十五次国王------77--ok
			achlist.add(AchValue.CHENJIU_WIN_KINGS25);
		}

		if (isLadies(pkmap))
		{
			// 红Q一对, 女王--ok
			achlist.add(AchValue.CHENJIU_WIN_LADIES);
		}

		if (pms.getLevel(pkmap) == ConstList.CardState.GAME_STATE_CARD_LEVEL_1.value())// extra--ok
		{
			// 一对赢，成双成对
			achlist.add(AchValue.CHENJIU_WIN_LOW_ROCKETS);
			// 50次一对
			achlist.add(AchValue.CHENJIU_WIN_ANY_POKETS50);
			// 100次一对
			achlist.add(AchValue.CHENJIU_WIN_ANY_POCKETS100);

		}

		if (pms.getLevel(pkmap) == ConstList.CardState.GAME_STATE_CARD_LEVEL_2.value())// extra--ok
		{
			// 50次两对
			achlist.add(AchValue.CHENJIU_WIN_TWO_PAIRS50);
		}

		if (isROYAL_FLUSH(pkmap))// --ok
		{
			// 皇家同花顺,皇家至尊
			achlist.add(AchValue.CHENJIU_WIN_ROYAL_FLUSH);
		}

		if (isSTRAIGHT_FLUSH(pkmap))// -ok
		{
			// 梅花 2到6同花顺 梅花2到6同花顺, 同花至尊
			achlist.add(AchValue.CHENJIU_WIN_STRAIGHT_FLUSH);
		}
		if (isBROADWAY(pkmap))// ok
		{
			// 10~A顺子, 大火车
			achlist.add(AchValue.CHENJIU_WIN_BROADWAY);
		}

		if (isSTR_TO_TEN(pkmap))// ok
		{
			// 6~10顺子 , 小火车
			achlist.add(AchValue.CHENJIU_WIN_STR_TO_TEN);
		}

		if (isALL_WHEEL_DRIVE(pkmap))// --extra--ok
		{
			// 任意4条, 四小龙--11
			achlist.add(AchValue.CHENJIU_WIN_ANY_QUADS);
			// 四条10次 --59
			achlist.add(AchValue.CHENJIU_WIN_QUADS);
			// 四条50次 --70
			achlist.add(AchValue.CHENJIU_WIN_ANY_QUADS50);
		}

		if (isFOUR_ACES(pkmap))// --ok
		{
			// 4条A, 四大皆空

			achlist.add(AchValue.CHENJIU_WIN_FOUR_ACES);
		}
		if (isFOUR_KINDS(pkmap))// --ok
		{
			// 4条K, 四大天王
			achlist.add(AchValue.CHENJIU_WIN_FOUR_KINDS);
		}

		if (pms.isStraight(pkmap))// --extra--ok
		{
			// 25次顺子
			achlist.add(AchValue.CHENJIU_WIN_STRAIGHT25);
		}
		if (pms.getLevel(pkmap) == ConstList.CardState.GAME_STATE_CARD_LEVEL_3.value()
				&& (pkmap.get(2).getNum() == 14 || pkmap.get(2).getNum() == 1))// --extra--ok
		{
			// 10次三条A
			achlist.add(AchValue.CHENJIU_WIN_ANY_SETS10);
			// 25次3条A
			achlist.add(AchValue.CHENJIU_WIN_ANY_SETS25);
		}

		if (isAK_FULL_HOUSE(pkmap))
		{
			// 35 AAAKK,三枪葫芦
			achlist.add(AchValue.CHENJIU_WIN_AK_FULL_HOUSE);
		}
		if (isQS_FULL_JS(pkmap))
		{
			// QQQJJ, 三足鼎立
			achlist.add(AchValue.CHENJIU_WIN_QS_FULL_JS);
		}
		if (isFULL_HOUSE(pkmap))
		{
			// 葫芦
			achlist.add(AchValue.CHENJIU_WIN_FULL_HOUSE);
			// 15次葫芦
			achlist.add(AchValue.CHENJIU_WIN_FULL_HOUSE15);
			// 50次葫芦
			achlist.add(AchValue.CHENJIU_WIN_FULL_HOUSE50);

		}
		if (isMAVERICK(pkmap))
		{
			// 黑桃同花
			achlist.add(AchValue.CHENJIU_WIN_MAVERICK);
			achlist.add(AchValue.CHENJIU_WIN_FLUSH15);
			achlist.add(AchValue.CHENJIU_WIN_ANY_FLUSH50);

		}
		if (isDIAMONDS(pkmap))
		{
			// 方块同花
			achlist.add(AchValue.CHENJIU_WIN_DIAMONDS);
			achlist.add(AchValue.CHENJIU_WIN_FLUSH15);
			achlist.add(AchValue.CHENJIU_WIN_ANY_FLUSH50);
		}
		if (isBIG_HEARTED(pkmap))
		{
			// 红心同花
			achlist.add(AchValue.CHENJIU_WIN_BIG_HEARTED);
			// 15次同花
			achlist.add(AchValue.CHENJIU_WIN_FLUSH15);
			// 50次同花
			achlist.add(AchValue.CHENJIU_WIN_ANY_FLUSH50);
		}
		if (isCLUBS(pkmap))
		{
			// 梅花同花
			achlist.add(AchValue.CHENJIU_WIN_CLUBS);
			achlist.add(AchValue.CHENJIU_WIN_FLUSH15);
			// 50次同花
			achlist.add(AchValue.CHENJIU_WIN_ANY_FLUSH50);
		}
		if (isTHREE_SONS(pkmap))
		{
			// 三条J, 三阳开泰
			achlist.add(AchValue.CHENJIU_WIN_THREE_SONS);
		}
		if (isBEAST(pkmap))
		{
			// 三条6, 六韬三略
			achlist.add(AchValue.CHENJIU_WIN_BEAST);
		}

		return achlist;
	}

	/**
	 * pkmap 要求排序 是否红A 一对
	 * 
	 * @param pk
	 */
	private boolean isRockets(HashMap<Integer, Puke> pkmap)
	{
		boolean b = false;
		for (Integer key : pkmap.keySet())
		{
			Puke p = pkmap.get(key);
			if ((p.getNum() == 14 || p.getNum() == 1)
					&& ConstList.ht.equals(p.getTag()))
			{
				for (Integer kk : pkmap.keySet())
				{
					Puke puke = pkmap.get(kk);
					if ((puke.getNum() == 14 || p.getNum() == 1)
							&& ConstList.fk.equals(puke.getTag()))
					{
						b = true;
						return b;
					}
				}
			}
		}
		return b;
	}

	/**
	 * 是否红K一对
	 * 
	 * @param pk
	 */
	private boolean isCowboys(HashMap<Integer, Puke> pkmap)
	{
		boolean b = false;
		for (Integer key : pkmap.keySet())
		{
			Puke p = pkmap.get(key);
			if (p.getNum() == 13 && ConstList.ht.equals(p.getTag()))
			{
				for (Integer kk : pkmap.keySet())
				{
					Puke puke = pkmap.get(kk);
					if (puke.getNum() == 13
							&& ConstList.fk.equals(puke.getTag()))
					{
						b = true;
						return b;

					}
				}
			}
		}
		return b;
	}

	/**
	 * 是否红Q一对
	 * 
	 * @param pk
	 */
	private boolean isLadies(HashMap<Integer, Puke> pkmap)
	{
		boolean b = false;
		for (Integer key : pkmap.keySet())
		{
			Puke p = pkmap.get(key);
			if (p.getNum() == 12 && ConstList.ht.equals(p.getTag()))
			{
				for (Integer kk : pkmap.keySet())
				{
					Puke puke = pkmap.get(kk);
					if (puke.getNum() == 12
							&& ConstList.fk.equals(puke.getTag()))
					{
						b = true;
						return b;
					}
				}
			}
		}
		return b;
	}

	/**
	 * 判断是否是皇家同花顺
	 * 
	 * @param pk
	 * @return
	 */
	private boolean isROYAL_FLUSH(HashMap<Integer, Puke> pk)
	{
		return pms.isBigFive(pk);
	}

	/**
	 * 梅花 2到6同花顺
	 * 
	 * @param pk
	 * @return
	 */
	private boolean isSTRAIGHT_FLUSH(HashMap<Integer, Puke> pk)
	{
		boolean b = false;
		if (pms.isFive(pk))
		{
			Puke puke = pk.get(new Integer(pk.size() - 1));
			if (puke.getNum() == 6 && puke.getTag().equals(ConstList.mh))
			{
				b = true;
			}
		}
		return b;
	}

	/**
	 * 10~A的顺子
	 * 
	 * @param pk
	 * @return
	 */
	private boolean isBROADWAY(HashMap<Integer, Puke> pk)
	{
		boolean b = false;
		if (pms.isStraight(pk))
		{
			Puke puke1 = pk.get(new Integer(pk.size() - 1));
			Puke puke2 = pk.get(new Integer(pk.size() - 2));
			if (puke1.getNum() == 14 && puke2.getNum() == 13)
			{
				b = true;
			}
		}
		return b;
	}

	/**
	 * 6~10的顺子
	 * 
	 * @param pk
	 * @return
	 */
	private boolean isSTR_TO_TEN(HashMap<Integer, Puke> pk)
	{
		boolean b = false;
		if (pms.isStraight(pk))
		{
			Puke puke = pk.get(new Integer(pk.size() - 1));
			if (puke.getNum() == 10)
			{
				b = true;
			}
		}
		return b;
	}

	private boolean isANY_QUADS(HashMap<Integer, Puke> pk)
	{
		return false;
	}

	/**
	 * 4条A
	 * 
	 * @param pk
	 * @return
	 */
	private boolean isFOUR_ACES(HashMap<Integer, Puke> pk)
	{
		boolean b = false;
		if (pms.getLevel(pk) == ConstList.CardState.GAME_STATE_CARD_LEVEL_7.value())
		{
			Puke puke = pk.get(new Integer(pk.size() - 2));
			if (puke.getNum() == 14 || puke.getNum() == 1)
			{
				b = true;
			}
		}
		return b;
	}

	/**
	 * 4条k
	 * 
	 * @param pk
	 * @return
	 */
	private boolean isFOUR_KINDS(HashMap<Integer, Puke> pk)
	{
		boolean b = false;
		if (pms.getLevel(pk) == ConstList.CardState.GAME_STATE_CARD_LEVEL_7.value())
		{
			Puke puke = pk.get(new Integer(pk.size() - 2));
			if (puke.getNum() == 13)
			{
				b = true;
			}
		}
		return b;
	}

	/**
	 * 任意4条
	 * 
	 * @param pk
	 * @return
	 */
	private boolean isALL_WHEEL_DRIVE(HashMap<Integer, Puke> pk)
	{
		boolean b = false;
		if (pms.getLevel(pk) == ConstList.CardState.GAME_STATE_CARD_LEVEL_7.value())
		{
			b = true;
		}
		return b;
	}

	/**
	 * AAAKK
	 * 
	 * @param pk
	 * @return
	 */
	private boolean isAK_FULL_HOUSE(HashMap<Integer, Puke> pk)
	{
		boolean b = false;
		if (pms.getLevel(pk) == ConstList.CardState.GAME_STATE_CARD_LEVEL_6.value())
		{
			Puke puke1 = pk.get(new Integer(2));
			Puke puke2 = pk.get(new Integer(0));
			if (puke1.getNum() == 14 && puke2.getNum() == 13)
			{
				b = true;
				return b;
			}
		}
		return b;
	}

	/**
	 * QQQJJ
	 * 
	 * @param pk
	 * @return
	 */
	private boolean isQS_FULL_JS(HashMap<Integer, Puke> pk)
	{
		boolean b = false;
		if (pms.getLevel(pk) == ConstList.CardState.GAME_STATE_CARD_LEVEL_6.value())
		{
			Puke puke1 = pk.get(new Integer(2));
			Puke puke2 = pk.get(new Integer(0));
			if (puke1.getNum() == 12 && puke2.getNum() == 11)
			{
				b = true;
				return b;
			}
		}
		return b;
	}

	/**
	 * 三条一对，葫芦
	 * 
	 * @param pk
	 * @return
	 */
	private boolean isFULL_HOUSE(HashMap<Integer, Puke> pk)
	{
		boolean b = false;
		if (pms.getLevel(pk) == ConstList.CardState.GAME_STATE_CARD_LEVEL_6.value())
		{
			b = true;
			return b;
		}
		return b;
	}

	/**
	 * 5黑桃
	 * 
	 * @param pk
	 * @return
	 */
	private boolean isMAVERICK(HashMap<Integer, Puke> pk)
	{
		boolean b = false;
		if (pms.isFiveTag(pk))
		{
			if ((pk.get(new Integer(0)).getTag()).equals(ConstList.bt))
			{
				b = true;
			}
		}
		return b;
	}

	/**
	 * 5方块
	 * 
	 * @param pk
	 * @return
	 */
	private boolean isDIAMONDS(HashMap<Integer, Puke> pk)
	{
		boolean b = false;
		if (pms.isFiveTag(pk))
		{
			if ((pk.get(new Integer(0)).getTag()).equals(ConstList.fk))
			{
				b = true;
			}
		}
		return b;
	}

	/**
	 * 5红桃
	 * 
	 * @param pk
	 * @return
	 */
	private boolean isBIG_HEARTED(HashMap<Integer, Puke> pk)
	{
		boolean b = false;
		if (pms.isFiveTag(pk))
		{
			if ((pk.get(new Integer(0)).getTag()).equals(ConstList.ht))
			{
				b = true;
			}
		}
		return b;
	}

	/**
	 * 5梅花
	 * 
	 * @param pk
	 * @return
	 */
	private boolean isCLUBS(HashMap<Integer, Puke> pk)
	{
		boolean b = false;
		if (pms.isFiveTag(pk))
		{
			if ((pk.get(new Integer(0)).getTag()).equals(ConstList.mh))
			{
				b = true;
			}
		}
		return b;
	}

	/**
	 * 三条J
	 * 
	 * @param pk
	 * @return
	 */
	private boolean isTHREE_SONS(HashMap<Integer, Puke> pk)
	{
		boolean b = false;
		int m = 0;
		for (Integer key : pk.keySet())
		{
			Puke p = pk.get(key);
			if (p.getNum() == 11)
			{
				m++;
			}
		}
		if (m == 3)
		{
			b = true;
		}
		return b;
	}

	/**
	 * 三条6
	 * 
	 * @param pk
	 * @return
	 */
	private boolean isBEAST(HashMap<Integer, Puke> pk)
	{
		boolean b = false;
		int m = 0;

		for (Integer key : pk.keySet())
		{
			Puke p = pk.get(key);
			if (p.getNum() == 6)
			{
				m++;
			}
		}

		if (m == 3)
		{
			b = true;
		}
		return b;
	}

}
