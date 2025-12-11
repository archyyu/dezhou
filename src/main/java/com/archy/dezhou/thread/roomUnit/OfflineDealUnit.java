package com.archy.dezhou.thread.roomUnit;

import com.archy.dezhou.entity.room.CommonRoom;
import com.archy.dezhou.thread.roomUnit.base.IRoomDealUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class OfflineDealUnit implements IRoomDealUnit
{
	private Logger log = LoggerFactory.getLogger(getClass());

	private CommonRoom room = new CommonRoom();
	
	@Scheduled(fixedDelay = 10)
	public void heartbeat()
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
