package com.yl.backlet;

import io.netty.handler.codec.http.FullHttpResponse;

import java.util.HashMap;
import java.util.Map;

import com.yl.Global.ConstList;
import com.yl.Global.UserInfoMemoryCache;
import com.yl.Global.UserModule;
import com.yl.backlet.base.DataBacklet;
import com.yl.container.User;
import com.yl.httpLogic.PlayerManager;
import com.yl.httplink.RequestHttp;
import com.yl.util.ChargeUtil;
import com.yl.entity.UserInfo;

public class BizcontrolBacklet extends DataBacklet
{
	
	private final static String BUYTOOLREQUEST = "BuyToolRequest";
	private final static String CHARGEREQUEST = "ChargeRequest";
	private final static String QUERYCHARGERECREQUEST = "QueryChargeRecRequest";
	private final static String QUERYCONSUMERECREQUEST = "QueryConsumeRecRequest";
	private final static String QUERYBALREQUEST = "QueryBalRequest";
	
	
	@Override
	public byte[] process(String subCmd, Map<String, String> parms,
			FullHttpResponse httpResponse)
	{
		
		byte[] xmlByteA = null;
		if(subCmd.equals(BUYTOOLREQUEST))
		{
			String uid = parms.get("uid") == null ? "-1": parms.get("uid");
			String userid = parms.get("userid") == null ? "-1": parms.get("userid");
			String mobile = parms.get("mobile") == null ? "-1": parms.get("mobile");
			String DaojuStr = parms.get("djid") == null ? "-1": parms.get("djid");
			String key = parms.get("key") == null ? "-1": parms.get("key");
			String channelId = parms.get("channelId") == null ? "-1": parms.get("channelId");
			
			if(!channelId.equals("-1"))
				uid = "10001";
			
			int djid = Integer.parseInt(DaojuStr);
			int uidNum = Integer.parseInt(uid);
			httpResponse.headers().set("Content-Type","text/xml; charset=UTF8");
			if(djid >0 && djid <=131 && uidNum > 0 && UserModule.getInstance().isValidDaoju(djid) && key!=null && key.length()>0)
			{
				int needGold = UserModule.getInstance().GetConsumeGold(djid);
				User user = UserModule.getInstance().getUserByUserId(uidNum);
				if(user ==null)
				{
					HashMap<String, String> userinfoList = PlayerManager.AutoRegister(userid, key);
					if(userinfoList!= null && userinfoList.get("name") != null && userinfoList.get("password") != null)
					{
						PlayerManager.UserLogin(userinfoList.get("name"),userinfoList.get("password"),true,userid,key,0,false);
					}
				}
				user = UserModule.getInstance().getUserByUserId(uidNum);
				UserInfo ui = UserInfoMemoryCache.getUserInfo(uid);
				
				int goldbefore = ui.getGold();
				ConstList.config.logger.info("金币余额为"+goldbefore+",本次购买道具需要金币"+needGold+"。");
				if(user!= null && key!= null && key.length() >10)
				{
					RequestHttp rh =new RequestHttp("http://gmp.i139.cn/bizcontrol/BuyGameTool",user,HANDLENAME);
					rh.consumeid = UserModule.getInstance().GetConsumeId(djid);
					rh.key = key;
					rh.mobile = mobile;
					rh.userId = userid;
					rh.djid = DaojuStr;
					rh.channelId = channelId;
					Thread rhThread = new Thread(rh);
					rhThread.start();
					if(channelId.equals("-1"))
					    xmlByteA = BackletKit.okXml("BuyGameToolRequestIsSended!") .getBytes();
					else
						xmlByteA = "1".getBytes();
				}
				else if(!UserModule.getInstance().isValidDaoju(djid))
				{
					if(channelId.equals("-1"))
					    xmlByteA = BackletKit.errorXml("YouDaojuIdIsNotValid!").getBytes();
					else
						xmlByteA = "-1".getBytes();
				}
				else
				{
					if(channelId.equals("-1"))
					    xmlByteA = BackletKit.errorXml("UserIsNoLogined").getBytes();
					else
						xmlByteA = "-2".getBytes();
				}
				httpResponse.headers().set("Content-Length", xmlByteA.length+"");
			}
			else
				xmlByteA = BackletKit.errorXml("ParmsIsInvalid").getBytes();

		}
		else if(subCmd.equals(CHARGEREQUEST))
		{

			String uid = parms.get("uid") == null ? "": parms.get("uid");
			String userid = parms.get("userid") == null ? "": parms.get("userid");
			String mobile = parms.get("mobile") == null ? "": parms.get("mobile");
			String chargeStr = parms.get("cn") == null ? "": parms.get("cn");
			String key = parms.get("key") == null ? "": parms.get("key");
			int chargeNum = Integer.parseInt(chargeStr);
			int uidNum = Integer.parseInt(uid);
			if(chargeNum >0 && chargeNum <=10000 && uidNum > 0 && ChargeUtil.isValidChargeNum(chargeNum) && key!=null && key.length()>0)
			{
				httpResponse.headers().set("Content-Type","text/xml; charset=UTF8");
				User user = UserModule.getInstance().getUserByUserId(uidNum);

				if(user!= null && (user.bbx_userkey.equals(key) || key.equals(ConstList.debugUserKey)))
				{
					RequestHttp rh =new RequestHttp("http://gmp.i139.cn/bizcontrol/ChargeUp",user,HANDLENAME);
					rh.chargeNumCode = ChargeUtil.getChargeCode(chargeNum);
					rh.key = key;
					rh.mobile = mobile;
					rh.userId = userid;
					Thread rhThread = new Thread(rh);
					rhThread.start();

					xmlByteA = BackletKit.okXml("chargeRequestIsSended!") .getBytes();
					httpResponse.headers().set("Content-Length", xmlByteA.length+"");
				}
				else
				{
					xmlByteA = BackletKit.errorXml("UserIsNoLogined").getBytes();
				}
				httpResponse.headers().set("cmd", "ChargeUp");
			}
			else
			{
				xmlByteA = BackletKit.errorXml("ChargeNumisInvalid").getBytes();
			}
		}
		else if(subCmd.equals(QUERYBALREQUEST))
		{

			String uid = parms.get("uid") == null ? "": parms.get("uid");
			String userid = parms.get("userid") == null ? "": parms.get("userid");
			String mobile = parms.get("mobile") == null ? "": parms.get("mobile");
			String key = parms.get("key") == null ? "": parms.get("key");
			int uidNum = Integer.parseInt(uid);
			User user = UserModule.getInstance().getUserByUserId(uidNum);

			if(user!= null && (user.bbx_userkey.equals(key) || key.equals(ConstList.debugUserKey)))
			{
				RequestHttp rh =new RequestHttp("http://gmp.i139.cn/bizcontrol/QueryBalance",user,HANDLENAME);
				rh.key = key;
				rh.mobile = mobile;
				rh.userId = userid;
				Thread rhThread = new Thread(rh);
				rhThread.start();
				xmlByteA = BackletKit.infoXml("youQueryBalRequestIsSended").getBytes();
			}
			else
			{
				xmlByteA = BackletKit.errorXml("UserIsNoLogined").getBytes();
			}
		
		}
		else if(subCmd.equals(QUERYCHARGERECREQUEST))
		{

			String uid = parms.get("uid") == null ? "": parms.get("uid");
			String userid = parms.get("userid") == null ? "": parms.get("userid");
			String mobile = parms.get("mobile") == null ? "": parms.get("mobile");
			String key = parms.get("key") == null ? "": parms.get("key");
			String Startdate = parms.get("Startdate") == null ? "": parms.get("Startdate");
			String Enddate = parms.get("Enddate") == null ? "": parms.get("Enddate");

			int uidNum = Integer.parseInt(uid);
			User user = UserModule.getInstance().getUserByUserId(uidNum);

			if(user!= null && (user.bbx_userkey.equals(key) || key.equals(ConstList.debugUserKey)))
			{
				RequestHttp rh =new RequestHttp("http://gmp.i139.cn/bizcontrol/QueryChargeUpRecord",user,HANDLENAME);
				rh.key = key;
				rh.mobile = mobile;
				rh.userId = userid;
				rh.Startdate = Startdate;
				rh.Enddate = Enddate; 

				Thread rhThread = new Thread(rh);
				rhThread.start();
				xmlByteA = BackletKit.infoXml("QueryChargeRequestIsSended").getBytes();
			}
			else
			{
				xmlByteA = BackletKit.errorXml("UserIsNoLogined").getBytes();
			}

		}
		else if(subCmd.equals(QUERYCONSUMERECREQUEST))
		{
			String uid = parms.get("uid") == null ? "": parms.get("uid");
			String userid = parms.get("userid") == null ? "": parms.get("userid");
			String mobile = parms.get("mobile") == null ? "": parms.get("mobile");
			String key = parms.get("key") == null ? "": parms.get("key");
			String OneMonth = parms.get("OneMonth") == null ? "": parms.get("OneMonth");
			String queryrange = parms.get("queryrange") == null ? "": parms.get("queryrange");
			String Type = parms.get("Type") == null ? "": parms.get("Type");

			int uidNum = Integer.parseInt(uid);
			User user = UserModule.getInstance().getUserByUserId(uidNum);
			boolean ifValidType=  false;
			if(Type.equals("3") || Type.equals("5") ||Type.equals("13") )
			{
				ifValidType = true;
			}
			if(ifValidType && user!= null && (user.bbx_userkey.equals(key) || key.equals(ConstList.debugUserKey)))
			{
				RequestHttp rh =new RequestHttp("http://gmp.i139.cn/bizcontrol/QueryConsumeRecord",user,HANDLENAME);
				rh.key = key;
				rh.mobile = mobile;
				rh.userId = userid;
				rh.OneMonth = OneMonth;
				rh.queryType = Type; 
				rh.queryrange = queryrange; 
				Thread rhThread = new Thread(rh);
				rhThread.start();
				xmlByteA =  BackletKit.infoXml("QueryConsumeRequestIsSended").getBytes();
			}
			else if(!ifValidType)
			{
				xmlByteA = BackletKit.errorXml("YourQueryTypeIsInvalid").getBytes();
			}
			else 
			{
				xmlByteA = BackletKit.errorXml("UserIsNoLogined").getBytes();
			}
		
		}
		return xmlByteA;
	}
}
