package com.yl.backlet;

import io.netty.handler.codec.http.FullHttpResponse;

import java.util.Map;

import com.yl.Global.UserInfoMemoryCache;
import com.yl.Global.UserModule;
import com.yl.backlet.base.DataBacklet;
import com.yl.container.SFSObjectSerializer;
import com.yl.container.User;
import com.yl.httpLogic.ConnectInstance;
import com.yl.vo.UserInfo;

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
			httpResponse.headers().set("cmd",(String) curMessage.aObj.get("_cmd"));
			if (curMessage.aObj.get("ts") != null)
			{
				httpResponse.headers().set("ts",(String) curMessage.aObj.get("ts"));
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
