package com.archy.dezhou.thread.roomUnit;

import com.archy.dezhou.room.CommonRoom;
import com.archy.dezhou.thread.roomUnit.base.IRoomDealUnit;
import org.apache.log4j.Logger;

public class OfflineDealUnit implements IRoomDealUnit
{
	private Logger log = Logger.getLogger(getClass());
	
	private CommonRoom room = new CommonRoom();
	
	@Override
	public void run()
	{
		while(true)
		{
			long start = System.currentTimeMillis();
			
			room.beatHeart(start);
			
			long end = System.currentTimeMillis();
			if((end - start) < 30)
			{
				try
				{
					Thread.sleep(10);
				}
				catch (Exception e)
				{
					log.error("Error",e);
				}
			}
		}
	}

}
