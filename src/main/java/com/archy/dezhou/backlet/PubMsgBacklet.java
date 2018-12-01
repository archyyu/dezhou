package com.archy.dezhou.backlet;

import com.archy.dezhou.backlet.base.DataBacklet;
import io.netty.handler.codec.http.FullHttpResponse;

import java.util.Map;

import com.archy.dezhou.global.UserModule;
import com.archy.dezhou.container.User;
import com.archy.dezhou.entity.room.base.IRoom;

public class PubMsgBacklet extends DataBacklet
{
	
	@Override
	public byte[] process(String subCmd, Map<String, String> parms,
			FullHttpResponse httpResponse)
	{
		byte[] xmlByteA = null;

		IRoom room = null;
		String roomkey = null;
		String uid = parms.get("uid") == null ? "" : parms.get("uid");
		String rn = parms.get("rn") == null ? "" : parms.get("rn");
		String msg = parms.get("msg") == null ? "" : parms.get("msg");
		String messageType = parms.get("mt") == null ? "" : parms.get("mt");
		String Command = parms.get("cmd") == null ? "": parms.get("cmd");
		String fuid = parms.get("fuid") == null ? "": parms.get("fuid");

		room = UserModule.getInstance().getRoomByName(rn); 

		User user = UserModule.getInstance().getUserByUserId(new Integer(uid));
		httpResponse.headers().set("Content-Type","text/xml; charset=utf-8");
		httpResponse.headers().set("cmd", "pubMsg");

		
		if(room == null)
		{
			return BackletKit.errorXml("YourRoomNameIsInValid").getBytes();
		}
		
		
		if (!Command.equals("world")) 
		{
			xmlByteA = BackletKit.errorXml("YourRoomNameIsInValid").getBytes();
		}
		else if (user == null) 
		{
			roomkey = room.getName();
			xmlByteA = BackletKit.errorXml("YourUserIdIsInValid").getBytes();
		} 
		else if((msg == null || msg.length()==0) && !Command.equals("world"))
		{
			roomkey = room.getName();
			xmlByteA = BackletKit.errorXml("messageIsNull").getBytes();
		}
		else if((Command == null || Command.equals("") || 
				messageType == null || messageType.equals("")) && !Command.equals("world"))
		{
			roomkey = room.getName();
			xmlByteA = BackletKit.errorXml("ParmIsInvalid").getBytes();
		}
		else 
		{

		}
		return xmlByteA;
	}

}
