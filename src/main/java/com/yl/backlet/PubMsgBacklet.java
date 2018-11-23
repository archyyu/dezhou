package com.yl.backlet;

import io.netty.handler.codec.http.FullHttpResponse;

import java.util.Map;

import com.yl.Global.UserModule;
import com.yl.backlet.base.DataBacklet;
import com.yl.container.User;
import com.yl.room.base.IRoom;
import com.yl.thread.AsynchronousModule;

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
			roomkey = room.getName();
			AsynchronousModule mm  = null;
			if(user.getUid().equals("10000") && Command.equals("pubMsg"))
			{
				mm = new AsynchronousModule(msg,user,Command,messageType,HANDLENAME);
			}
			else if(!user.getUid().equals("10000") && Command.equals("pubMsg"))
			{
				mm = new AsynchronousModule(msg,room,user,Command,messageType,HANDLENAME);
			}
			else if(Command.equals("priMsg"))
			{
				mm = new AsynchronousModule(msg,user,fuid,Command,messageType);
			}
			
			if(mm!= null)
			{
				mm.t.start();
				xmlByteA = BackletKit.infoXml("Dealing").getBytes();	
			}
			else if(Command.equals("world"))
			{
				httpResponse.headers().set("cmd", "worldMsg");
				xmlByteA = BackletKit.WorldListXml(user);
			}
			else
			{
				xmlByteA = BackletKit.infoXml("null").getBytes();	
			}
		}
		return xmlByteA;
	}

}
