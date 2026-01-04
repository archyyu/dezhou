package com.archy.dezhou.entity.puker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.archy.dezhou.entity.Player;
import com.archy.dezhou.entity.Puke;
import com.archy.dezhou.global.ConstList;

@Component
public class PukerHelp {

    private Map<Integer,Puke> randomPuke = new HashMap<Integer,Puke>();
	
	public Puke getPuke(int index)
	{
		if(randomPuke.isEmpty())
		{
			randomPuke = Puke();
		}
		return randomPuke.get(index);
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
	public boolean isBigFive(List<Puke> map)
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

    public int getLevel(List<Puke> map)
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

    public List<Player> sortPlayerByPukeMap(List<Player> players)
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

    public FivePukeItem getMaxFive(List<Puke> map)
	{
		FivePukeItem item = null;
		Map<Integer,List<Puke>> group = SevenToFiveGroups(map);
		
		List<FivePukeItem> itemList = new ArrayList<FivePukeItem>();
		for(Map.Entry<Integer, List<Puke>> entry : group.entrySet())
		{
			item = new FivePukeItem(entry.getValue());
			item.calculateHandValue(this);
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
			}
		});
		
		return itemList.get(0);
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

		public boolean needDuplicateSort(int level)
	{

        return level == ConstList.CardState.GAME_STATE_CARD_LEVEL_7.value() ||
                level == ConstList.CardState.GAME_STATE_CARD_LEVEL_6.value() ||
                level == ConstList.CardState.GAME_STATE_CARD_LEVEL_3.value() ||
                level == ConstList.CardState.GAME_STATE_CARD_LEVEL_2.value() ||
                level == ConstList.CardState.GAME_STATE_CARD_LEVEL_1.value();
    }
	
	public boolean needResort(int level)
	{
        return level == ConstList.CardState.GAME_STATE_CARD_LEVEL_4.value() ||
                level == ConstList.CardState.GAME_STATE_CARD_LEVEL_8.value();
    }
	
	public void sortDuplicatePukes(List<Puke> list)
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
	
	public class DuplicatePukeItem
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
