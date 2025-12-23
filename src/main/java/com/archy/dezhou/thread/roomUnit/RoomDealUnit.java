package com.archy.dezhou.thread.roomUnit;

import java.util.List;
import java.util.concurrent.TimeUnit;

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
	
	// @Scheduled(fixedDelay = 10, initialDelay = 1000, timeUnit = java.util.concurrent.TimeUnit.MILLISECONDS)
	@Scheduled(fixedDelay = 1000, initialDelay = 5000, timeUnit = TimeUnit.MILLISECONDS)
	public void heartbeat()
	{

		// log.info("RoomDealUnit heartbeat");
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
