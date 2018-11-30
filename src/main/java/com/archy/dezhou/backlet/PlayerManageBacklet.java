package com.archy.dezhou.backlet;

/**
 *@author archy_yu 
 **/

import com.archy.dezhou.Global.ConstList;
import com.archy.dezhou.Global.UserInfoMemoryCache;
import com.archy.dezhou.backlet.base.DataBacklet;
import com.archy.dezhou.container.ActionscriptObject;
import com.archy.dezhou.container.SFSObjectSerializer;
import com.archy.dezhou.entity.UserInfo;
import com.archy.dezhou.httpLogic.PlayerManager;
import com.archy.dezhou.thread.AsynchronousModule;
import io.netty.handler.codec.http.FullHttpResponse;

import java.util.HashMap;
import java.util.Map;

import org.jdom.input.SAXBuilder;

public class PlayerManageBacklet extends DataBacklet
{
	
	private final static String USERLOGIN = "userlogin";
	private final static String REGISTER = "register";
	private final static String REGISTERUPDATE = "registerupdate";
	private final static String PASSWORDUPDATE = "passwordupdate";
	private final static String UINFO = "uinfo";
	private final static String LOGOUT = "logout";
	private final static String PICUPDATE = "picUpdate";
	private final static String RUSHACH = "rach";
	@Override
	public byte[] process(String subCmd, Map<String, String> parms,FullHttpResponse response)
	{
		byte[] xmlByteA = null;
		String XmlError = "";
		String XmlOk = "";
		
		if(subCmd.equals(USERLOGIN))
		{
			String userName = parms.get("name");
			String userPassword = parms.get("password");
			String userid = parms.get("userid") ;
			String key = parms.get("key");
			
			response.headers().set("cmd", "userlogin");
			response.headers().set("ts", "-1");
			response.headers().set("num", "0");
			if (userName.equals("") || userName == null
					|| userPassword.equals("")
					|| userPassword == null)
			{
				XmlError = BackletKit.errorXml("NameOrPasswordIdNull");
				xmlByteA = XmlError.getBytes();
			}
			else
			{
				xmlByteA = PlayerManager.UserLogin(userName,
						userPassword, false,
						userid, key, 0, false);
				xmlByteA = BackletKit.SimpleObjectXml(xmlByteA);
			}
		}
		else if(subCmd.equals(REGISTER))
		{
			 // 用户新注册
			// 请求数据的格式为
			// 用户名-密码-电子邮箱-性别-生日
			String auto = parms.get("auto");
			String userid = parms.get("userid");
			String key = parms.get("key");

			String uid = PlayerManager
					.getUidFromMobileUserid(userid);
			if (userid.length() > 0 && !uid.equals("-1"))
			{
				ConstList.config.logger.info("uid=" + uid);
				XmlError = BackletKit.errorXml("UserHasRegistered");
				xmlByteA = XmlError.getBytes();
			}
			if (auto.equals("yes"))
			{
				HashMap<String, String> userinfoList = PlayerManager
						.AutoRegister(userid, key);
				if (userinfoList != null
						&& userinfoList.get("name") != null
						&& userinfoList.get("password") != null)
				{
					response.headers().set("cmd", "autoregister");
					response.headers().set("ts", "-1");
					response.headers().set("num", "0");
					xmlByteA = PlayerManager.UserLogin( userinfoList.get("name"),
							userinfoList.get("password"),true,
							userid, key, 0, false);
					
					xmlByteA = BackletKit.SimpleObjectXml(xmlByteA);
				}
				else if (userid != null
						&& !userid.trim().equals(""))
				{
					xmlByteA = PlayerManager.UserLogin("", "", false,
							 userid, key, 1,
							false);
					xmlByteA = BackletKit.SimpleObjectXml(xmlByteA);
				}
				else
				{
					XmlError = BackletKit.errorXml("AutoRigesterFailed");
					xmlByteA = XmlError.getBytes();
				}
			}
			else
			{
				String userName = parms.get("name");
				String password = parms.get("password");
				String email = parms.get("email");
				String gendar = parms.get("gendar");
				String birthday = parms.get("birthday");
				
				response.headers().set("cmd", "register");
				response.headers().set("ts", "-1");
				response.headers().set("num", "0");
				xmlByteA = PlayerManager.Register(userName, password, email, gendar, birthday, userid, key);
			}
		}
		else if(subCmd.equals(REGISTERUPDATE))
		{

			String uid = parms.get("uid") == null ? "" : parms
					.get("uid");
			String email = parms.get("email") == null ? ""
					: parms.get("email");
			String birthday = parms.get("birthday") == null ? ""
					: parms.get("birthday");
			String gendar = parms.get("gendar") == null ? ""
					: parms.get("gendar");
			String name = parms.get("name") == null ? ""
					: parms.get("name");
			String address = parms.get("address") == null ? ""
					: parms.get("address");
			String mobile = parms.get("mobile") == null ? ""
					: parms.get("mobile");
			String newPassword = parms.get("np") == null ? ""
					: parms.get("np");
			String oldPassword = parms.get("op") == null ? ""
					: parms.get("op");
			UserInfo uinfo = UserInfoMemoryCache.getUserInfo(uid);

			response.headers().set("cmd", "registerupdate");
			response.headers().set("ts", "-1");
			response.headers().set("num", "0");
			if (! UserInfoMemoryCache.isUserInfoOnline(uid) )
			{
				XmlError = BackletKit.errorXml("UserNotLogined");
				xmlByteA = XmlError.getBytes();
			}
			else if (PlayerManager.ifRegistered(name, ""))
			{
				XmlError = BackletKit.errorXml("userNameIsRepeat");
				xmlByteA = XmlError.getBytes();
			}
			else
			{

				if (!email.equals("") && email != null)
					uinfo.setEmail(email);
				if (!birthday.equals("") && birthday != null)
					uinfo.setBirthday(birthday);
				if (!gendar.equals("") && gendar != null)
					uinfo.setGendar(gendar);
				if (!name.equals("") && name != null)
					uinfo.setName(name);
				if (!address.equals("") && address != null)
					uinfo.setAddress(address);
				if (!mobile.equals("") && mobile != null)
					uinfo.setMobile(mobile);

				ActionscriptObject PassWordInfo = new ActionscriptObject();
				if (!newPassword.equals("")
						&& !oldPassword.equals(""))
				{
					PassWordInfo.put("op", oldPassword);
					PassWordInfo.put("np", newPassword);
				}
				
				ActionscriptObject upodatetatus = PlayerManager.UpdateUserInfo(uinfo, PassWordInfo);
				ActionscriptObject asResponse = null;

				asResponse = PlayerManager.getUinfo(uinfo, true);

				if (upodatetatus == null)
				{
					asResponse.put("status", "registerUpdatefail");
					asResponse.put("code", "0");
					asResponse.put("cnt", "用户资料修改失败！");
				}
				else
				{
					asResponse.put("status",
							upodatetatus.get("status"));
					asResponse.put("code",
							upodatetatus.get("code"));
					asResponse.put("cnt", upodatetatus.get("cnt"));
				}
				StringBuffer sb = new StringBuffer();
				sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
				xmlByteA = SFSObjectSerializer.obj2xml(asResponse, 0, "", sb);
				xmlByteA = BackletKit.SimpleObjectXml(xmlByteA);
			}
		}
		else if(subCmd.equals(PASSWORDUPDATE))
		{
			String password = parms.get("password");
			String email = parms.get("email");
			response.headers().set("cmd", "passwordupdate");
			response.headers().set("ts", "-1");
			response.headers().set("num", "0");
			String[] userID =
			{ "uid", "name" };
			if (password.equals("") || email.equals(""))
			{
				xmlByteA = BackletKit.errorXml("parmsInInvalid")
						.getBytes();
			}
			else if (!PlayerManager.isvalidEmail(email, userID))
			{
				xmlByteA = BackletKit.errorXml("EmailIsinValid")
						.getBytes();
			}
			else
			{
				AsynchronousModule mm = new AsynchronousModule(
						password, email, HANDLENAME);
				mm.t.start();
				xmlByteA = BackletKit.infoXml("Dealing").getBytes();
			}
		
		}
		else if(subCmd.equals(UINFO))
		{
			String uid = parms.get("uid");
			String cuid = parms.get("cuid");

			if (uid.equals("") || cuid.equals(""))
			{
				xmlByteA = BackletKit.errorXml("ParmsIsInvalid")
						.getBytes();
			}
			else
			{
				UserInfo uinfo = UserInfoMemoryCache.getUserInfo(uid);
				UserInfo cuinfo = UserInfoMemoryCache.getUserInfo(cuid);
				if (uinfo == null)
				{
					xmlByteA = BackletKit.errorXml("YouAreNotLogined!")
							.getBytes();
				}
				else if (cuinfo == null)
				{
					xmlByteA = BackletKit.errorXml("HeIsNotLogined!!")
							.getBytes();
				}
				else
				{
					ActionscriptObject asResponse = null;
					if (uid.equals(cuid)) // edited by 2014-8-2
					{
						asResponse = PlayerManager.getUinfo(uinfo, true);
					}
					else
					{
						asResponse = PlayerManager.getUinfo(cuinfo, false);
					}
					StringBuffer sb = new StringBuffer();
					sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
					xmlByteA = SFSObjectSerializer.obj2xml(asResponse, 0, "", sb);
					xmlByteA = BackletKit.SimpleObjectXml(xmlByteA);
				}
			}
		
		}
		else if(subCmd.equals(PICUPDATE))
		{

			String uid = parms.get("uid");
			String pic = parms.get("pic");

			if (uid == null || pic == null)
			{
				xmlByteA = BackletKit.errorXml("ParmsIsInvalid")
						.getBytes();
			}
			else
			{
				UserInfo uinfo = UserInfoMemoryCache.getUserInfo(uid);
				if (uinfo == null)
					xmlByteA =BackletKit.errorXml("YouAreNotLogined").getBytes();
				else
				{
					uinfo.setPic(pic);
					String subFolderName = String
							.valueOf(Integer.parseInt(uid) % 20);
					String destFilename = "session/player/"
							+ subFolderName + "/" + uid
							+ "_info.xml";
//					PlayerManager.writeUserInfo2XmlFile(uinfo, HANDLENAME);
					SAXBuilder builder = new SAXBuilder(false);
					ActionscriptObject PassWordInfo = new ActionscriptObject();

					ActionscriptObject upodatetatus = PlayerManager.UpdateUserInfo(uinfo, PassWordInfo);
					ActionscriptObject asResponse = null;

					asResponse = PlayerManager.getUinfo(uinfo, true);

					if (upodatetatus == null)
					{
						asResponse.put("status", "registerUpdatefail");
						asResponse.put("code", "0");
						asResponse.put("cnt", "用户资料修改失败！");
					}
					else
					{
						asResponse.put("status",upodatetatus.get("status"));
						asResponse.put("code",upodatetatus.get("code"));
						asResponse.put("cnt",upodatetatus.get("cnt"));
					}
					StringBuffer sb = new StringBuffer();
					sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
					xmlByteA = SFSObjectSerializer.obj2xml(asResponse, 0, "", sb);
					xmlByteA = BackletKit.SimpleObjectXml(xmlByteA);
				}
			}
		}
		else if(subCmd.equals(LOGOUT))
		{
			String uid = parms.get("uid");
			UserInfo uinfo = UserInfoMemoryCache.getUserInfo(uid);
			if (uinfo != null)
			{
				xmlByteA = BackletKit.infoXml("loginoutOk").getBytes();
				UserInfoMemoryCache.removeUserInfo(uid);
			}
			else
			{
				xmlByteA = BackletKit.infoXml("HasLoginOutBefore")
						.getBytes();
			}
		}
		else if(subCmd.equals(RUSHACH))
		{
			String uid = parms.get("uid");
			String rName = parms.get("rn");
			
			UserInfo uinfo = UserInfoMemoryCache.getUserInfo(uid);
		}
		else
		{
			xmlByteA = BackletKit.infoXml("UserManagerParmsIsValid").getBytes();
		}
		return xmlByteA ;
	}
}
