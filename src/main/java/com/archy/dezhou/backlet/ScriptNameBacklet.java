package com.archy.dezhou.backlet;

import com.archy.dezhou.entity.User;
import com.archy.dezhou.global.ConstList;
import com.archy.dezhou.global.UserModule;
import com.archy.dezhou.backlet.base.DataBacklet;
import com.archy.dezhou.container.ActionscriptObject;
import com.archy.dezhou.container.SFSObjectSerializer;
import com.archy.dezhou.global.PlayerService;
import io.netty.handler.codec.http.FullHttpResponse;

import java.util.Map;
import java.util.HashMap;

public class ScriptNameBacklet extends DataBacklet
{
	
	@Override
	public byte[] process(String subCmd, Map<String, String> parms,
			FullHttpResponse httpResponse)
	{
		
		byte[] xmlByteA = null;
		String XmlError = "";

		String ismobile = parms.get("ismobile") == null ? "": parms.get("ismobile");
		String resetPassword = parms.get("resetPwd") == null ? "": parms.get("resetPwd");

		if(ismobile.equals("yes"))
		{
			String userid = parms.get("userid") == null ? "": parms.get("userid");
			String mobile = parms.get("mobile") == null ? "": parms.get("mobile");
			String key = parms.get("key") == null ? "": parms.get("key");
			//1 ：真实手机  2：模拟器
			String uid = userid;
			User user = null;
			//该用户已经注册了，判断他是否在线
			if(!uid.equals("-1"))
			{
				user = UserModule.getInstance().getUserByUserId(Integer.parseInt(uid));
			}
			log.info("userid="+userid+",mobile="+mobile+",key="+key+",compressFlag=" +",uid="+uid);
			//新用户，还没有注册过
			if(user == null && userid.length() >4 && key.length() >25 && uid.equals("-1"))
			{
				HashMap<String, String> userinfoList = PlayerService.AutoRegister(userid, key);
				if(userinfoList.get("name") != null && userinfoList.get("password") != null)
				{
					httpResponse.headers().set("cmd", "autoregister");
					httpResponse.headers().set("ts", "-1");
					httpResponse.headers().set("num", "0");
					xmlByteA = PlayerService.UserLogin(userinfoList.get("name"),userinfoList.get("password"),true,userid,key,1,false);
					xmlByteA = BackletKit.SimpleObjectXml(xmlByteA);
				}
				else 
				{
					XmlError = BackletKit.errorXml("AutoRigesterFailed");
					xmlByteA = XmlError.getBytes();
				}							
			}
			//老用户并且没有登陆
			else if(user == null && userid.length() >4 && key.length() >25 && !uid.equals("-1"))
			{
				if(resetPassword.equals("yes"))
				{
					xmlByteA =  PlayerService.UserLogin("","",false,userid,key,1,true);
				}
				else
				{
					xmlByteA =  PlayerService.UserLogin("","",false,userid,key,1,false);
				}
				
				log.info("老用户并且没有登陆");
			}
			//老用户手机用户，并且已经登陆在线
			else if( user != null && key.length() >25 && userid.length()>0)
			{
				log.info("该用户已经处于在线状态！");

//				PlayerService.writeUserStatus2XmlFile(user,"nosend");
				User uinfo  = UserModule.getInstance().getUserByUserId(Integer.parseInt(uid));
				ActionscriptObject response = PlayerService.getUinfo(uinfo,true);
				response.put("Ver", ConstList.gameVersion);

				if(resetPassword.equals("yes"))
				{
					response.put("password", PlayerService.resetPasswd(uinfo));
				}

				ActionscriptObject djObjList = PlayerService.getUsedDj(uinfo, user);
				int[][] diamondList = PlayerService.getDiamondList(djObjList,uinfo);
				response.put("diamond", PlayerService.getDiamondListStr(diamondList));
				response.put("vip", PlayerService.getVipid(djObjList,uinfo)+"");

				StringBuffer sb = new StringBuffer();
				sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
				httpResponse.headers().set("cmd", "synUserInfo");
				xmlByteA =  SFSObjectSerializer.obj2xml(response, 0, "", sb);
				xmlByteA = BackletKit.SimpleObjectXml(xmlByteA);
			}
			else 
			{
				xmlByteA = BackletKit.errorXml("EmulatorCantAutoLogin").getBytes();
			}

			httpResponse.headers().set("cmd", "synUserInfo");
			httpResponse.headers().set("ts", "-1");
			httpResponse.headers().set("num", "0");
		}
		else if(parms.get("userId")!= null)
		{
//			String userId = parms.get("userId") == null ? "": parms.get("userId");
//			String key = parms.get("key") == null ? "-1": parms.get("key");
			xmlByteA = "0".getBytes();
			httpResponse.headers().set("Content-Type","text/plain; charset=UTF8");
		}
		else
		{
			xmlByteA = "0".getBytes();
			httpResponse.headers().set("Content-Type","text/plain; charset=UTF8");
		}
		
		return xmlByteA;
	}

}
