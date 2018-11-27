package com.yl.service.Imp;

/**
 * 管理扑克 
 **/

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import com.yl.Global.ConstList;
import com.yl.puker.PukerKit;
import com.yl.service.PukeModuleService;
import com.yl.util.Utils;
import com.yl.entity.Player;
import com.yl.entity.Puke;

public class PukeModuleServiceImp implements PukeModuleService
{
	
	public PukeModuleServiceImp()
	{
		
	}
	
	/**
	 * 生成扑克集合
	 * 
	 */
	public Map<Integer,Puke> Puke()
	{
		Map<Integer,Puke> map = new HashMap<Integer,Puke>(52);
		int i = 65;
		int k = 2;
		for (int j = 0; j < 52; j++)
		{
			if (j % 13 == 0 && j != 0)
			{
				i++;
				k = 2;
			}
			Puke Puke = new Puke(String.valueOf((char) i), k);
			map.put(j, Puke);
			k++;
		}
		return map;
	}

	/**
	 * 根据玩家的人数，随机获得底牌的位置
	 * 
	 * @param size
	 * @return
	 */
	public int[] generateRandomNumber(int size, int type)
	{
		int seed[] = new int[52];
		for (int i = 0; i < 52; i++)
			seed[i] = i;
		int ranArr[] = new int[size];

		if (type == 0)
		{
			Random ran = new Random();
			for (int i = 0; i < size; i++)
			{
				int j = ran.nextInt(seed.length - i);
				ranArr[i] = seed[j];
				seed[j] = seed[seed.length - 1 - i];
			}
		}
		else if (type == 1)
		{
			int[] pukelist = new int[]
			{ 0, 1, 2, 3, 4, 5, 6, 13, 14, 15, 16, 17, 18, 19, 26, 27, 28, 29,
					30, 31, 39, 40, 41, 42, 43, 44, 45 };
			int[] comList = new int[]
			{ 8, 9, 10, 11, 12 };
			Random ran = new Random();
			for (int i = 0; i < (size - 5); i++)
			{
				int j = ran.nextInt(pukelist.length - i);
				ranArr[i] = pukelist[j];
				pukelist[j] = pukelist[pukelist.length - 1 - i];
			}
			int j = 0;
			for (int i = (size - 5); i < size; i++)
			{
				ranArr[i] = comList[j];
				j++;
			}
		}
		else if (type == 2)
		{
			int[] pukelist = new int[] { 0, 1, 2, 11, 4, 5, 6, 13, 14, 15, 16, 17, 18, 19, 26, 27, 28, 29,
					30, 31, 39, 40, 41, 42, 43, 44, 45 };
			
			int[] comList = new int[]{ 8, 9, 21, 3, 12 };
			
			Random ran = new Random();
			for (int i = 0; i < (size - 5); i++)
			{
				int j = ran.nextInt(pukelist.length - i);
				ranArr[i] = pukelist[j];
				pukelist[j] = pukelist[pukelist.length - 1 - i];
			}
			int j = 0;
			for (int i = (size - 5); i < size; i++)
			{
				ranArr[i] = comList[j];
				j++;
			}
		}
		return ranArr;
	}
	
	/**
	 * 从7张牌中抽出5张牌的21中组合
	 * 
	 * @param args
	 */
	private Map<Integer, List<Puke>> SevenToFiveGroups(List<Puke> map)
	{
		Map<Integer, List<Puke>> group = new HashMap<Integer, List<Puke>>();
		int num = 0;
		for (int a = 0; a < 3; a++)
		{
			for (int b = a + 1; b < 4; b++)
			{
				for (int c = b + 1; c < 5; c++)
				{
					for (int d = c + 1; d < 6; d++)
					{
						for (int e = d + 1; e < 7; e++)
						{
							List<Puke> pukeGroup = new ArrayList<Puke>();
							pukeGroup.add(map.get(a));
							pukeGroup.add(map.get(b));
							pukeGroup.add(map.get(c));
							pukeGroup.add(map.get(d));
							pukeGroup.add(map.get(e));
							group.put(num++, pukeGroup);
						}
					}
				}
			}
		}
		num = 0;
		return group;
	}

	/**
	 * 从6张牌中抽出5张牌的6中组合
	 * 
	 * @param args
	 */
	private HashMap SixToFiveGroups(HashMap<Integer, Puke> map)
	{
		HashMap<Integer, HashMap<Integer, Puke>> group = new HashMap<Integer, HashMap<Integer, Puke>>();
		int num = 0;
		for (int a = 0; a < 2; a++)
		{
			for (int b = a + 1; b < 3; b++)
			{
				for (int c = b + 1; c < 4; c++)
				{
					for (int d = c + 1; d < 5; d++)
					{
						for (int e = d + 1; e < 6; e++)
						{
							HashMap<Integer, Puke> Puke = new HashMap<Integer, Puke>();
							Puke.put(0, map.get(a));
							Puke.put(1, map.get(b));
							Puke.put(2, map.get(c));
							Puke.put(3, map.get(d));
							Puke.put(4, map.get(e));
							group.put(num++, Puke);
						}
					}
				}
			}
		}
		return group;
	}

	/**
	 * 判断任意五张牌的级别
	 * 
	 * @param args
	 */
	public int getLevel(HashMap map)
	{
		ConstList.CardState Pukelevel = ConstList.CardState.GAME_STATE_CARD_LEVEL_END;
		int flag = 0;
		for (int i = 0; i < map.size(); i++)
		{
			for (int j = i + 1; j < map.size(); j++)
			{
				if (((Puke) map.get(i)).getNum() == ((Puke) map.get(j))
						.getNum())
				{
					flag++;
				}
			}
		}
		switch (flag)
		{
		case 6:
			Pukelevel = ConstList.CardState.GAME_STATE_CARD_LEVEL_7;
			break;
		case 4:
			Pukelevel = ConstList.CardState.GAME_STATE_CARD_LEVEL_6;
			break;
		case 3:
			Pukelevel = ConstList.CardState.GAME_STATE_CARD_LEVEL_3;
			break;
		case 2:
			Pukelevel = ConstList.CardState.GAME_STATE_CARD_LEVEL_2;
			break;
		case 1:
			Pukelevel = ConstList.CardState.GAME_STATE_CARD_LEVEL_1;
			break;
		case 0:
			if (isBigFive(map))
			{
				Pukelevel = ConstList.CardState.GAME_STATE_CARD_LEVEL_9;
			}
			else if (isFive(map))
			{
				Pukelevel = ConstList.CardState.GAME_STATE_CARD_LEVEL_8;
			}
			else if (isFiveTag(map))
			{
				Pukelevel = ConstList.CardState.GAME_STATE_CARD_LEVEL_5;
			}
			else if (isStraight(map))
			{
				Pukelevel = ConstList.CardState.GAME_STATE_CARD_LEVEL_4;
			}
			else
			{
				Pukelevel = ConstList.CardState.GAME_STATE_CARD_LEVEL_0;
			}
			break;
		}
		return Pukelevel.value();
	}

	/**
	 * 判断是否是皇家同花顺
	 * 
	 * @param args
	 */
	public boolean isBigFive(HashMap map)
	{
		if (isFive(map))
		{
			if (((Puke) map.get(map.size() - 1)).getNum() == 14)
			{
				if (((Puke) map.get(map.size() - 2)).getNum() == 5)
				{
					return false;
				}
				else
				{
					return true;
				}
			}
			else
			{
				return false;
			}
		}
		else
		{
			return false;
		}
	}

	/**
	 * 判断是否是同花顺
	 * 
	 * @param args
	 */
	public boolean isFive(HashMap map)
	{
		if (isFiveTag(map))
		{
			if (isStraight(map))
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			return false;
		}
	}

	/**
	 * 判断是否是同花
	 * 
	 * @param args
	 */
	public boolean isFiveTag(HashMap map)
	{
		String tag = ((Puke) map.get(0)).getTag();
		for (int i = 0; i < map.size(); i++)
		{
			String tg = ((Puke) map.get(i)).getTag();
			if (!tag.equals(tg))
			{
				return false;
			}
		}
		return true;
	}

	/**
	 * 判断是否是顺子
	 * 
	 * @param args
	 */
	public boolean isStraight(HashMap map)
	{
		for (int i = map.size() - 1; i > 0; i--)
		{ // 判断是不是顺子
			int max = ((Puke) map.get(i)).getNum();
			int min = ((Puke) map.get(i - 1)).getNum();
			if (max != min + 1)
			{
				if (max == 14 && min == 5)
				{
					return true;
				}
				else
				{
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * 取出5张牌的Num值组成字符串，10 11 12 13 14分别用a,b,c,d,e替换
	 */
	private String retStr(HashMap<Integer, Puke> map)
	{
		Puke puke = null;
		int[] arr = new int[map.size()];
		int n = 0;
		for (Integer key : map.keySet())
		{
			puke = map.get(key);
			arr[n] = puke.getNum();
			n++;
		}
		arr = Utils.sortBigToSmall(arr);
		String ps = Utils.arrayToStr(arr);
		return ps;
	}

	/**
	 * 从21中组合中找出最大五张牌的组合
	 * 
	 * @param args
	 */
	public List<Puke> MaxFive(List<Puke> map)
	{
		Map<Integer, List<Puke>> map2 =  SevenToFiveGroups(map);
		for(Map.Entry<Integer, List<Puke>> entry : map2.entrySet())
		{
			
		}
		
		return map;

	}

	/**
	 * 从6中组合中找出最大五张牌的组合
	 * 
	 * @param args
	 */
	public HashMap<Integer, Puke> MaxFiveFromSix(HashMap<Integer, Puke> map)
	{
		HashMap<Integer, HashMap<Integer, Puke>> map2 = (HashMap<Integer, HashMap<Integer, Puke>>) SixToFiveGroups(map);
		int level = -1;
		HashMap<String, HashMap<Integer, Puke>> temp = new HashMap<String, HashMap<Integer, Puke>>();
		int[] larry = new int[map2.size()];
		for (int i = 0; i < map2.size(); i++)
		{
			HashMap<Integer, Puke> t = (HashMap<Integer, Puke>) map2.get(i);
			level = getLevel(t);
			larry[i] = level;
			temp.put(i + "L" + level, t);
		}
		Map<String,Integer> hmap = Utils.getMaxAndNum(larry);
		level = (Integer) hmap.get("level");
		int num = (Integer) hmap.get("num");
		String str = "";
		String s = "";
		if (num == 0)
		{
			for (String key : temp.keySet())
			{
				str = key;
				s = str.substring(str.indexOf("L") + 1);
				if (s.equals(String.valueOf(level)))
				{
					map = (HashMap<Integer, Puke>) temp.get(str);
				}
			}
		}
		else
		{
			String[] sarray = new String[num];
			int k = 0;
			for (String key : temp.keySet())
			{
				str = key;
				s = str.substring(str.indexOf("L") + 1);
				if (s.equals(String.valueOf(level)))
				{
					HashMap<Integer, Puke> t = (HashMap<Integer, Puke>) temp
							.get(str);
					sarray[k] = retStr(t) + "@" + str;
					k++;

				}
			}
			String key = Utils.compareStrRetBigA(sarray);
			map = (HashMap<Integer, Puke>) temp.get(key);
		}
		return map;
	}

	// 排序 Integer
	public HashMap<Integer, HashMap<Integer, Puke>> orderIntegerPukeHashMap(
			HashMap<Integer, HashMap<Integer, Puke>> comparePkmap)
	{
		HashMap<Integer, HashMap<Integer, Puke>> cPkmap = new HashMap<Integer, HashMap<Integer, Puke>>();
		for (Integer key : comparePkmap.keySet())
		{
			HashMap<Integer, Puke> table = comparePkmap.get(key);
			Puke temp;
			for (int i = 0; i < table.size(); i++)
			{
				for (int j = 0; j < table.size() - i - 1; j++)
				{
					if (((Puke) table.get(j)).getNum() < ((Puke) table.get(j + 1)).getNum())
					{
						temp = (Puke) table.get(j);
						table.put(j, table.get(j + 1));
						table.put(j + 1, temp);
					}
				}
			}
			cPkmap.put(key, table);
		}
		return cPkmap;
	}

	public void printPkList(HashMap<String, HashMap<Integer, Puke>> comparePkmap)
	{

		for (String key : comparePkmap.keySet())
		{

			HashMap<Integer, Puke> pklist = comparePkmap.get(key);
			ConstList.config.logger
					.info("___________________begin___________________key="
							+ key);
			for (Integer skey : pklist.keySet())
			{
				ConstList.config.logger.info(skey + "---"
						+ pklist.get(skey).getNum() + ","
						+ pklist.get(skey).getTag() + "|");
			}
		}
	}
	
	public class ComparatorPukeMap implements Comparator<Player>
	{
		@Override
		public int compare( Player leftPlayer, Player rightPlayer )
		{
			return PukerKit.compareTwoPukeMap((HashMap<Integer,Puke>)leftPlayer.getFivePk(),(HashMap<Integer,Puke>)rightPlayer.getFivePk());
		}
	}
	
	@Override
	public int compareTwoPukeMap(HashMap<Integer,Puke> leftPukeMap,HashMap<Integer,Puke> rightPukeMap)
	{
		int leftLevel = this.getLevel(leftPukeMap);
		int rightLevel = this.getLevel(rightPukeMap);
		
		if(leftLevel > rightLevel)
		{
			return 1;
		}
		else if(leftLevel < rightLevel)
		{
			return -1;
		}
		
		Iterator<Map.Entry<Integer, Puke>> leftEntry = leftPukeMap.entrySet().iterator();
		Iterator<Map.Entry<Integer, Puke>> rightEntry = rightPukeMap.entrySet().iterator();
		while(leftEntry.hasNext() && rightEntry.hasNext())
		{
			if(leftEntry.next().getValue().getNum()!= rightEntry.next().getValue().getNum())
			{
				return leftEntry.next().getValue().getNum() - rightEntry.next().getValue().getNum();
			}
		}
		
		return 0;
	}
	
	@Override
	public List<Player> sortPlayerByPukeMap(List<Player> players)
	{
		List<Player> playersList = new ArrayList<Player>();
		Collections.sort(playersList,new ComparatorPukeMap());
		return playersList;
	}

	private HashMap compareBigFive(HashMap<Integer, HashMap<Integer, Puke>> hmap)
	{
		HashMap retMap = new HashMap();
		boolean ifHavaA = false;
		boolean ifHavaK = false;
		for (Integer key : hmap.keySet())
		{
			HashMap<Integer, Puke> pkList = hmap.get(key);
			for (Integer skey : pkList.keySet())
			{
				if (pkList.get(skey).getNum() == 14)
				{
					ifHavaA = true;
				}
				if (pkList.get(skey).getNum() == 13)
				{
					ifHavaK = true;
				}
			}
			if (ifHavaA && ifHavaK)
			{
				retMap.put(key, pkList);
				ifHavaA = false;
				ifHavaK = false;
			}
		}

		return retMap;
	}
	
	@Override
	public void sortMap(HashMap<Integer,Puke> pkMap)
	{
		for(int i=pkMap.size() - 1 ; i > 0 ; i--)
		{
			Puke p = pkMap.get(i);
		}
	}

	public String pkPosition(HashMap<Integer, Puke> pkmap,
			HashMap<Integer, Puke> userPk)
	{
		StringBuffer sbuffer = new StringBuffer();
		for (Integer i : pkmap.keySet())
		{
			Puke pk1 = pkmap.get(i);
			for (Integer k : userPk.keySet())
			{
				Puke pk2 = userPk.get(k);
				if (pk1.getTag().equals(pk2.getTag())
						&& pk1.getNum() == pk2.getNum())
				{
					sbuffer.append(String.valueOf(k) + "@");
				}
			}
		}
		return sbuffer.toString();
	}


}
