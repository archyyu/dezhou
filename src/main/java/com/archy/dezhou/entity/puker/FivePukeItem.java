package com.archy.dezhou.entity.puker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.archy.dezhou.container.ActionscriptObject;
import com.archy.dezhou.entity.Puke;

public class FivePukeItem
{
	
	public FivePukeItem(List<Puke> list)
	{
		this.list.addAll(list);
	}
	
	private long pkValue = 0L;
	
	public long getPkValue()
	{
		return this.pkValue;
	}
	
	private int level = 0;
	
	public int getLevel()
	{
		return this.level;
	}
	
	public List<Puke> getList()
	{
		List<Puke> list = new ArrayList<Puke>();
		list.addAll(this.list);
		return list;
	}
	
	public ActionscriptObject toAobj()
	{
		ActionscriptObject aso = new ActionscriptObject();
		for(int i = 0;i<this.list.size();i++)
		{
			aso.put(i,this.list.get(i).toAobj());
		}
		return aso;
	}
	
	
	private List<Puke> list = new ArrayList<Puke>();
	
	public void calculateHandValue()
	{
		Collections.sort(list,new Comparator<Puke>(){
			@Override
			public int compare( Puke arg0, Puke arg1 )
			{
				return arg1.getNum() - arg0.getNum();
			}
		});
		
		this.level = PukerKit.getLevel(this.list);
		if(PukerKit.needDuplicateSort(this.level))
		{
			PukerKit.sortDuplicatePukes(this.list);
		}
		
		if(PukerKit.needResort(this.level))
		{
			if(this.list.get(0).getNum() == 14 && this.list.get(1).getNum() == 5)
			{
				Puke puke = this.list.get(0);
				this.list.remove(0);
				this.list.add(puke);
			}
		}
		
		long lon = 1000000000L * 10;
		this.pkValue = 0L;
		for(int i = 0 ; i < this.list.size() ; i++)
		{
			int interval = 1;
			for(int j = i ; j < this.list.size() - 1 ; j ++)
			{
				interval *= 10;
				interval *= 10;
			}
			this.pkValue += this.list.get(i).getNum() * interval;
		}
		this.pkValue += this.level * lon;
		
	}
	
}
