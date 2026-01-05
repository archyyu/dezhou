package com.archy.texasholder.entity.puker;

/**
 *@author archy_yu 
 **/

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class PukerKit
{

	private List<Integer> seed = new ArrayList<Integer>();
	

	public void reSeed() {
		this.seed =  IntStream.range(0, 52).boxed().collect(Collectors.toList());
	}
	
	public List<Integer> generateRandomNumArray(int size)
	{
		
		Collections.shuffle(this.seed);
		
		if(size > this.seed.size())
		{
			size = this.seed.size();
		}
		
		List<Integer> set = new ArrayList<Integer>();
		Random ran = new Random(seed.size() * 10);
		while(true)
		{
			Integer item = this.seed.get( ran.nextInt(seed.size() * 10) % seed.size() );
			set.add(item);
			this.seed.remove(item);
			if(set.size() >= size)
			{
				break;
			}
		}
		return set;
	}
	
	
}
