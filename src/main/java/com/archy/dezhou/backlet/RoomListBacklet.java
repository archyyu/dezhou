package com.archy.dezhou.backlet;

/**
 *@author archy_yu 
 **/

import com.archy.dezhou.Global.UserModule;
import com.archy.dezhou.backlet.base.DataBacklet;
import com.archy.dezhou.container.User;
import com.archy.dezhou.httpLogic.RoomListLevel;
import com.archy.dezhou.entity.room.base.IRoom;
import io.netty.handler.codec.http.FullHttpResponse;

import java.util.Map;

public class RoomListBacklet extends DataBacklet
{
	
	private final static String LIST = "list";
	private final static String JOIN = "join";
	private final static String LEAVE = "leave";
	
	
	@Override
	public byte[] process(String subCmd, Map<String, String> parms,FullHttpResponse httpResponse)
	{
		byte[] xmlByteA = null;
		
		if(subCmd.equals(LIST))
		{
			String rt = parms.get("rt") == null ? "rg" : parms
					.get("rt");
			String bb = parms.get("bb") == null ? "-1" : parms
					.get("bb");
			String sb = parms.get("sb") == null ? "-1" : parms
					.get("sb");
			
			httpResponse.headers().set("cmd", "roomlist");
			httpResponse.headers().set("ts", "-1");
			httpResponse.headers().set("num", "0");
			
			xmlByteA = RoomListLevel.getRoomListFromMemory(rt, bb, sb).getBytes();
		}
		else if(subCmd.equals(JOIN))
		{
			String uid = parms.get("uid") == null ? "" : parms.get("uid");
			String roomname = parms.get("name") == null ? "" : parms.get("name");
			
			httpResponse.headers().set("cmd", "roomjoin");
			httpResponse.headers().set("ts", "-1");
			
			IRoom room = UserModule.getInstance().getRoomByName(roomname);
			User user = UserModule.getInstance().getUserByUserId(Integer.parseInt(uid));
			
			if(room == null || user == null)
			{
				return BackletKit.errorXml("UserNotLogined").getBytes();
			}
			
			IRoom oldRoom = UserModule.getInstance().getRoom(user.getRoomId());
			if(oldRoom != null)
			{
				
//				if(oldRoom.getName().equals(roomname))
//				{
//					return BackletKit.infoXml("haveBeenEntered").getBytes();
//				}
				user.clearBetMessageQuene();
				oldRoom.userLeave(user);
				
//				ConstList.config.logger.info(" leave oldroom: "  + oldRoom.getRoomId() +  " user:" +user);
			}
			
			if(room.isUserInRoom(user))
			{
				//TODO error
			}
			
			int ret = room.userJoin(user);
			user.setRoomId(room.getRoomId());
			if(ret == 0)
			{
				xmlByteA = BackletKit.okXml("UserEnterRoomOk").getBytes();
			}
			else
			{
				xmlByteA = BackletKit.infoXml("haveBeenEntered").getBytes();
			}
			
		}
		else if(subCmd.equals(LEAVE))
		{
			String uid = parms.get("uid");
			String rName = parms.get("name");
			User user = UserModule.getInstance().getUserByUserId(Integer.parseInt(uid));
			
			
			if(user == null)
			{
				return BackletKit.errorXml("UserNotLogined").getBytes();
			}
			user.clearBetMessageQuene();
			IRoom room = UserModule.getInstance().getRoomByName(rName);
			
			if(room == null)
			{
				return BackletKit.errorXml("YourParmsIsInValid").getBytes();
			}
			
			
			int lea = room.userLeave(user);
			
			if(lea == 0)
			{
				xmlByteA = BackletKit.okXml("UserLeaveRoomOk").getBytes();
			}
			else
			{
				xmlByteA = BackletKit.infoXml("haveBeenLeaved").getBytes();
			}
			log.info("user:" + user.getUid() + " leave room: "  +  " roomName:" +rName);
		}
		else
		{
			xmlByteA = BackletKit.infoXml("RoomManagerParmsIsValid")
					.getBytes();
		}
		return xmlByteA;
	}

}
