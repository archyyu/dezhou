package com.archy.dezhou.entity.puker;

/**
 *@author archy_yu 
 **/

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;

import com.archy.dezhou.Global.ConstList;
import com.archy.dezhou.entity.Player;
import com.archy.dezhou.entity.Puke;
import com.archy.dezhou.service.Imp.PukeModuleServiceImp;
import com.archy.dezhou.service.PukeModuleService;

public class PukerKit
{
	
	public static void main(String[] args)
	{
		List<Puke> map = new ArrayList<Puke>();
		
		map.add(new Puke("A",2));
		map.add(new Puke("C",3));
		map.add(new Puke("B",4));
		map.add(new Puke("A",6));
		map.add(new Puke("A",3));
		map.add(new Puke("A",3));
		map.add(new Puke("B",7));
		
		FivePukeItem item = getMaxFive(map);
		item.calculateHandValue();
	}
	
	private static PukeModuleService pms = new PukeModuleServiceImp();
	
	public static PukeModuleService getPms()
	{
		return pms;
	}
	
	private static Map<Integer,Puke> randomPuke = new HashMap<Integer,Puke>();
	
	public static Puke getPuke(int index)
	{
		if(randomPuke.isEmpty())
		{
			randomPuke = pms.Puke();
		}
		return randomPuke.get(index);
	}
	
	private static Integer[] seed =  { 0, 1, 2, 3, 4, 5, 6 , 7 , 8 , 9 , 10 , 11 , 12, 13, 14, 15, 16, 17, 18, 19,
			20, 21, 22, 23, 24, 25,
			26, 27, 28, 29,	30, 31 ,32 ,33 ,34 ,35 ,36 ,37 ,38 , 
			39, 40, 41, 42, 43, 44, 45 , 46, 47, 48, 49, 50, 51 };
	
	public static Integer[] generateRandomNumArray(int size)
	{
		List<Integer> list = Arrays.asList(seed);
		
		for(int i=0;i<3;i++)
		{
			Collections.shuffle(list);
		}
		
		if(size > seed.length)
		{
			size = seed.length;
		}
		
		Set<Integer> set = new HashSet<Integer>();
		Random ran = new Random(seed.length * 10);
		while(true)
		{
			set.add(list.get( ran.nextInt(seed.length * 10) % seed.length ));
			if(set.size() >= size)
			{
				break;
			}
		}
		return set.toArray(new Integer[set.size()]);
	}
	
	public static FivePukeItem getMaxFive(List<Puke> map)
	{
		FivePukeItem item = null;
		Map<Integer,List<Puke>> group = SevenToFiveGroups(map);
		
		List<FivePukeItem> itemList = new ArrayList<FivePukeItem>();
		for(Map.Entry<Integer, List<Puke>> entry : group.entrySet())
		{
			item = new FivePukeItem(entry.getValue());
			item.calculateHandValue();
			itemList.add(item);
		}
		
		Collections.sort(itemList,new Comparator<FivePukeItem>() {
			@Override
			public int compare( FivePukeItem o1, FivePukeItem o2 )
			{
				
				if(o2.getPkValue() > o1.getPkValue())
				{
					return 1;
				}
				else if(o2.getPkValue() < o1.getPkValue())
				{
					return -1;
				}
				
				return 0;
//				return (int)( o2.getPkValue() - o1.getPkValue() );
			}
		});
		
		return itemList.get(0);
	}
	
	/**
	 * 从7张牌中抽出5张牌的21中组合
	 * 
	 * @param args
	 */
	private static Map<Integer, List<Puke>> SevenToFiveGroups(List<Puke> map)
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
	
	public static int getPukeMapLevel(HashMap<Integer,Puke> map)
	{
		return pms.getLevel(map);
	}
	
	public static int compareTwoPukeMap(HashMap<Integer,Puke> leftPukeMap,HashMap<Integer,Puke> rightPukeMap)
	{
		return pms.compareTwoPukeMap(leftPukeMap,rightPukeMap);
	}
	
	public static List<Player> sortPlayerByPukeMap(List<Player> players)
	{
		Collections.sort(players,new Comparator<Player>() {

			@Override
			public int compare( Player o1, Player o2 )
			{
				if(o2.getPkValue() > o1.getPkValue())
				{
					return 1;
				}
				else if(o2.getPkValue() < o1.getPkValue())
				{
					return -1;
				}
				else
				{
					return o1.getTotalGambleBet() - o2.getTotalGambleBet();
				}
			}
			
		});
		
		return players;
	}
	
	public static String pkPosition(List<Puke> pkmap,
			List<Puke> userPk)
	{
		StringBuffer sbuffer = new StringBuffer();
		for (int i=0;i<pkmap.size();i++)
		{
			Puke pk1 = pkmap.get(i);
			for (int k=0; k<userPk.size() ; k++)
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
	
	public static boolean isStraightas(List<Puke> list)
	{
		
		return false;
	}
	
	public static boolean isStraight(List<Puke> list)
	{
		for (int i = 1; i < list.size(); i++)
		{ // 判断是不是顺子
			int min = list.get(i).getNum();
			int max = list.get(i - 1).getNum();
			if (max != min + 1)
			{
                return max == 14 && min == 5;
			}
		}
		return true;
	}
	
	public static boolean isFiveTag(List<Puke> list)
	{
		String tag = list.get(0).getTag();
		for (int i = 0; i < list.size(); i++)
		{
			String tg = list.get(i).getTag();
			if (!tag.equals(tg))
			{
				return false;
			}
		}
		return true;
	}
	
	public static boolean isFive(List<Puke> map)
	{
		if (isFiveTag(map))
		{
            return isStraight(map);
		}
		else
		{
			return false;
		}
	}
	
	/**
	 * 判断是否是皇家同花顺
	 * 
	 * @param args
	 */
	public static boolean isBigFive(List<Puke> map)
	{
		if (isFive(map))
		{
			if (map.get(0).getNum() == 14)
			{
                return map.get(1).getNum() != 5;
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
	
	public static int getLevel(List<Puke> map)
	{
		if (isBigFive(map))
		{
			return ConstList.CardState.GAME_STATE_CARD_LEVEL_9.value();
		}
		
		if (isFive(map))
		{
			return ConstList.CardState.GAME_STATE_CARD_LEVEL_8.value();
		}

		ConstList.CardState Pukelevel = ConstList.CardState.GAME_STATE_CARD_LEVEL_END;
		int flag = 0;
		for (int i = 0; i < map.size(); i++)
		{
			for (int j = i + 1; j < map.size(); j++)
			{
				if (map.get(i).getNum() == map.get(j)
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
			if (isFiveTag(map))
			{
				Pukelevel = ConstList.CardState.GAME_STATE_CARD_LEVEL_5;
			}
			break;
		case 2:
			Pukelevel = ConstList.CardState.GAME_STATE_CARD_LEVEL_2;
			if (isFiveTag(map))
			{
				Pukelevel = ConstList.CardState.GAME_STATE_CARD_LEVEL_5;
			}
			break;
		case 1:
			Pukelevel = ConstList.CardState.GAME_STATE_CARD_LEVEL_1;
			if (isFiveTag(map))
			{
				Pukelevel = ConstList.CardState.GAME_STATE_CARD_LEVEL_5;
			}
			break;
		case 0:
			if (isFiveTag(map))
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
	
	public static boolean needDuplicateSort(int level)
	{

        return level == ConstList.CardState.GAME_STATE_CARD_LEVEL_7.value() ||
                level == ConstList.CardState.GAME_STATE_CARD_LEVEL_6.value() ||
                level == ConstList.CardState.GAME_STATE_CARD_LEVEL_3.value() ||
                level == ConstList.CardState.GAME_STATE_CARD_LEVEL_2.value() ||
                level == ConstList.CardState.GAME_STATE_CARD_LEVEL_1.value();
    }
	
	public static boolean needResort(int level)
	{
        return level == ConstList.CardState.GAME_STATE_CARD_LEVEL_4.value() ||
                level == ConstList.CardState.GAME_STATE_CARD_LEVEL_8.value();
    }
	
	public static void sortDuplicatePukes(List<Puke> list)
	{
		Map<Integer,DuplicatePukeItem> map = new HashMap<Integer,DuplicatePukeItem>();
		for(Puke puke : list)
		{
			DuplicatePukeItem item = map.get(puke.getNum());
			if(item == null)
			{
				item = new DuplicatePukeItem();
				item.setValue(puke.getNum());
				item.insertValue(puke);
				map.put(puke.getNum(),item);
			}
			else
			{
				item.insertValue(puke);
			}
		}
		
		List<DuplicatePukeItem> tempList = new ArrayList<DuplicatePukeItem>();
		tempList.addAll(map.values());
		
		Collections.sort(tempList,new Comparator<DuplicatePukeItem>(){
			@Override
			public int compare( DuplicatePukeItem o1, DuplicatePukeItem o2 )
			{
				if(o2.getSize() > o1.getSize())
				{
					return 1;
				}
				else if(o2.getSize() < o1.getSize())
				{
					return -1;
				}
				else
				{
					return o2.getValue() - o1.getValue();
				}
			}
		});
		
		list.clear();
		for(DuplicatePukeItem item : tempList)
		{
			list.addAll(item.getList());
		}
		
	}
	
	public static class DuplicatePukeItem
	{
		public DuplicatePukeItem()
		{
			
		}
		
		private int value = 0;
		
		public void setValue(int value)
		{
			this.value = value;
		}
		
		public int getValue()
		{
			return this.value;
		}
		
		private List<Puke> list = new ArrayList<Puke>();
		
		public int getSize()
		{
			return this.list.size();
		}
		
		public void insertValue(Puke value)
		{
			this.list.add(value);
		}
		
		public List<Puke> getList()
		{
			return this.list;
		}
		
	}
	
	
}
