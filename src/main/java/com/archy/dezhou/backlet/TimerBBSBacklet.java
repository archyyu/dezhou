package com.archy.dezhou.backlet;

import com.archy.dezhou.Global.UserInfoMemoryCache;
import com.archy.dezhou.Global.UserModule;
import com.archy.dezhou.backlet.base.DataBacklet;
import com.archy.dezhou.container.SFSObjectSerializer;
import com.archy.dezhou.container.User;
import com.archy.dezhou.entity.UserInfo;
import com.archy.dezhou.httpLogic.ConnectInstance;
import io.netty.handler.codec.http.FullHttpResponse;

import java.util.Map;

public class TimerBBSBacklet extends DataBacklet
{

	@Override
	public byte[] process(String subCmd, Map<String, String> parms,FullHttpResponse httpResponse)
	{
		byte[] xmlByteA = null;
		String uid = parms.get("uid") == null ? "" : parms.get("uid");
		
		UserInfo uinfo = UserInfoMemoryCache.getUserInfo(uid);
		User user = UserModule.getInstance().getUserByUserId(Integer.parseInt(uid));
		
		if (user == null || uinfo == null)  
		{
			httpResponse.headers().set("Content-Type","text/xml; charset=utf-8");
			httpResponse.headers().set("cmd", "BbsMessageError");
			httpResponse.headers().set("ts", "-1");
			httpResponse.headers().set("num", "0");
			return BackletKit.errorXml("YourUserIdIsInValid").getBytes();
		} 
		
		user.updateOnlineHeatTime();
		
		if (user.getBbsMessageSize() == 0) 
		{
			httpResponse.headers().set("cmd", "Null");
			xmlByteA = BackletKit.NullXml("N").getBytes();
		} 
		else 
		{
			ConnectInstance curMessage = user.popBbsMessage();
			curMessage.status = 1;
			StringBuffer sb = new StringBuffer();
			sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
			httpResponse.headers().set("cmd", curMessage.aObj.get("_cmd"));
			if (curMessage.aObj.get("ts") != null)
			{
				httpResponse.headers().set("ts", curMessage.aObj.get("ts"));
			}
			else
			{
				httpResponse.headers().set("ts", "-1");
			}
			
			if (user.getBbsMessageSize() > 0)
			{
				httpResponse.headers().set("num",""+ (user.getBbsMessageSize() - 1));
			}
			else
			{
				httpResponse.headers().set("num", "0");
			}
			
			xmlByteA = SFSObjectSerializer.obj2xml(curMessage.aObj, 0, "", sb);
			xmlByteA = BackletKit.SimpleObjectXml(xmlByteA);

		}
		return xmlByteA;
	}

}
