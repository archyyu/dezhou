package com.yl.backlet;

import io.netty.handler.codec.http.FullHttpResponse;

import java.util.Map;

import com.yl.Global.UserModule;
import com.yl.backlet.base.DataBacklet;
import com.yl.container.SFSObjectSerializer;
import com.yl.container.User;
import com.yl.httpLogic.ConnectInstance;
import com.yl.room.base.IRoom;

public class TimerBetBacklet extends DataBacklet
{

	@Override
	public byte[] process(String subCmd, Map<String, String> parms,FullHttpResponse httpResponse)
	{
		byte[] xmlByteA = null;
		httpResponse.headers().set("ts", "-1");

		IRoom room = null;
		String uid = parms.get("uid") == null ? "" : parms.get("uid");
		String rn = parms.get("rn") == null ? "" : parms.get("rn");
		
		String tmpStr = parms.get("ts");
		tmpStr = tmpStr == null ? "-1" : tmpStr;
		
		
		room = UserModule.getInstance().getRoomByName(rn);
		User user = UserModule.getInstance().getUserByUserId(Integer.parseInt(uid));

		if (user == null || room == null)
		{
			httpResponse.headers().set("cmd", "BetMessageError");
			httpResponse.headers().set("ts", "-1");
			httpResponse.headers().set("num", "0");
			return BackletKit.errorXml("YourUserIdIsInValid").getBytes();
		}
		
		httpResponse.headers().set("ts", "-1");
		httpResponse.headers().set("n", room.getSecsPassByTurn() + "");
		
		user.updateOperateTime();
		
		if (user.getBetMessageSize() == 0)
		{
			httpResponse.headers().set("cmd", "Null");
			return BackletKit.NullXml("N").getBytes();
		}
			
		ConnectInstance curMessage = user.popBetMessage();
		if (curMessage == null)
		{
			httpResponse.headers().set("cmd", "Null");
			return BackletKit.NullXml("N").getBytes();
		}
		curMessage.status = 1;
		
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		httpResponse.headers().set("cmd",(String) curMessage.aObj.get("_cmd"));
		
		if (curMessage.aObj.get("ts") != null)
		{
			httpResponse.headers().set("ts",(String) curMessage.aObj.get("ts"));
		}
		else
		{
			httpResponse.headers().set("ts", "-1");
		}
		
		if (user.getBetMessageSize() > 0)
		{
			httpResponse.headers().set("num",""+ (user.getBetMessageSize() - 1));
		}
		else
		{
			httpResponse.headers().set("num", "0");
		}
		
		xmlByteA = SFSObjectSerializer.obj2xml(curMessage.aObj, 0, "", sb);
//		System.err.println(new String(xmlByteA));
		xmlByteA = BackletKit.SimpleObjectXml(xmlByteA);
	
		return xmlByteA;
	}

}
