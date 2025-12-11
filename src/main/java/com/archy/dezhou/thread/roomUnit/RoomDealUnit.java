package com.archy.dezhou.thread.roomUnit;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

import com.archy.dezhou.entity.room.GameRoom;
import com.archy.dezhou.service.RoomService;
import com.archy.dezhou.thread.roomUnit.base.IRoomDealUnit;

import jakarta.annotation.Resource;

@Service
public class RoomDealUnit implements IRoomDealUnit
{

	private Logger log = LoggerFactory.getLogger(getClass());

	@Resource
	private RoomService roomService;

	public RoomDealUnit()
	{
		
	}
	
	@Scheduled(fixedDelay = 10)
	public void heartbeat()
	{
		long start = System.currentTimeMillis();

		List<GameRoom> roomListCpy = new ArrayList<GameRoom>();
		roomListCpy.addAll(roomService.getRoomList());
		roomListCpy.forEach(room -> {
			room.beatHeart(start);
		});


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
