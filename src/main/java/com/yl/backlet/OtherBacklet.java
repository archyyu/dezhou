package com.yl.backlet;

import io.netty.handler.codec.http.FullHttpResponse;

import java.util.Map;

import com.yl.Global.ConstList;
import com.yl.Global.UserInfoMemoryCache;
import com.yl.backlet.base.DataBacklet;
import com.yl.container.ActionscriptObject;
import com.yl.container.SFSObjectSerializer;
import com.yl.entity.UserInfo;

public class OtherBacklet extends DataBacklet
{

	private final static String RANK = "rank";
	private final static String VERSION = "version";
	private final static String SENDMONEY = "sendMoney";
	
	@Override
	public byte[] process(String subCmd, Map<String, String> parms,
			FullHttpResponse httpResponse)
	{
		
		byte[] xmlByteA = null;

		if (subCmd.equals(RANK))
		{
			String uid = parms.get("uid") == null ? "" : parms
					.get("uid");
			httpResponse.headers().set("cmd", "rank");
			httpResponse.headers().set("ts", "-1");
			StringBuffer sb = new StringBuffer();
			sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
			xmlByteA = SFSObjectSerializer.obj2xml(UserInfoMemoryCache.RankList, 0, "", sb);
			xmlByteA = BackletKit.SimpleObjectXml(xmlByteA);
		}
		else if (subCmd.equals(VERSION))
		{
			String uid = parms.get("uid") == null ? "" : parms
					.get("uid");
			String cmd = parms.get("cmd") == null ? "" : parms
					.get("cmd");
			String passwd = parms.get("passwd") == null ? ""
					: parms.get("passwd");
			String gameVer = parms.get("gameVer") == null ? ""
					: parms.get("gameVer");
			httpResponse.headers().set("cmd", cmd);
			httpResponse.headers().set("ts", "-1");

			if (cmd.equals("getVer"))
			{
				xmlByteA = BackletKit.infoXml(ConstList.gameVersion).getBytes();
			}
			else if (cmd.equals("setVer")
					&& passwd.equals("defenceIllegalAccess")
					&& gameVer.length() > 1)
			{
				ConstList.gameVersion = gameVer;
				xmlByteA = BackletKit.okXml(ConstList.gameVersion)
						.getBytes();
			}
		}
		else if (subCmd.equals(SENDMONEY))
		{
			String uid = parms.get("uid") == null ? "" : parms
					.get("uid");
			String cmd = parms.get("cmd") == null ? "" : parms
					.get("cmd");
			String type = parms.get("type") == null ? ""
					: parms.get("type");
			UserInfo uinfo = UserInfoMemoryCache.getUserInfo(uid + "");
			httpResponse.headers().set("cmd", cmd);
			httpResponse.headers().set("ts", "-1");
			if (type.equals("teach"))
			{
				if (uinfo.isTeachFinished.equals("1"))
				{
					return BackletKit.infoXml( "YouHaveSendteachMoneyBefore").getBytes();
				}
				else
				{
					int preTmoney = uinfo.getAMoney();
					uinfo.setAmoney(uinfo.getAMoney() + 1000);
					ActionscriptObject response = new ActionscriptObject();
					response.put("ptm", preTmoney + "");
					response.put("tMoney", uinfo.getAMoney()
							+ "");
					response.put("status", "ok");
					response.put("_cmd", cmd);
					response.put("info", "教程完毕，赠送1000筹码！");
					uinfo.isTeachFinished = "1";
					StringBuffer sb = new StringBuffer();
//					PlayerManager.writeUserInfo2XmlFile(uinfo, HANDLENAME);
					sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
					xmlByteA = SFSObjectSerializer.obj2xml(
							response, 0, "", sb);
					xmlByteA = BackletKit.SimpleObjectXml(xmlByteA);
				}
			}
			else
			{
				xmlByteA = BackletKit.infoXml("FunctionisNotOpened").getBytes();
			}
		}
	
		return xmlByteA;
	}

}
