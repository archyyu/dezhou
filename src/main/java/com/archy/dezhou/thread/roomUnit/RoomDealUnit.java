package com.archy.dezhou.thread.roomUnit;

import java.util.List;
import java.util.ArrayList;

import com.archy.dezhou.global.UserModule;
import com.archy.dezhou.entity.room.base.IRoom;
import org.apache.log4j.Logger;

import com.archy.dezhou.thread.roomUnit.base.IRoomDealUnit;

public class RoomDealUnit implements IRoomDealUnit
{
	
	private Logger log = Logger.getLogger(getClass());
	
	public RoomDealUnit()
	{
		
	}
	
	@Override
	public void run()
	{
		while(true)
		{
			long start = System.currentTimeMillis();
			
			List<IRoom> roomListCpy = new ArrayList<IRoom>();
			roomListCpy.addAll(UserModule.getInstance().getRoomList());
			for(IRoom room : roomListCpy)
			{
				room.beatHeart(start);
			}
			
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
