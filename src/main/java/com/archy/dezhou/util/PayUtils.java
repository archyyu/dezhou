package com.archy.dezhou.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;


public class PayUtils
{
	public static HashMap<String, Integer> excmap = null;
	public static List pvlist = null;
	public static HashMap<String, String> mcBilling = null;

	// 判断储值额是否满足条件
	public static boolean checkPayMoney(String money)
	{
		return getExchangeMap().containsKey(money);
	}

	// 生成4个随机数组成字符串
	public static String strByRandomNum()
	{
		String str = "";
		Random r = new Random();
		for (int i = 0; i < 4; i++)
		{
			str += r.nextInt(10);
		}
		return str;
	}

	// 加密
	public static String bytes2String(byte[] bytes)
	{
		StringBuilder string = new StringBuilder();
		for (byte b : bytes)
		{
			String hexString = Integer.toHexString(0x00FF & b);
			string.append(hexString.length() == 1 ? "0" + hexString : hexString);
		}
		return string.toString();
	}

	// 将信息封装，发送到橘子游戏支付平台
	public static String postMsgToGamania(String ptype, String pmoney,
			String clientIP, String orderID)
	{
		// post过去的参数
		// String str3in1URL = "http://202.80.108.209/3in1main/DirectPay.aspx";
		String str3in1URL = "https://vp.gamania.com/3in1main/DirectPay.aspx";
		String strAAAStoreID = "P10910";
		// String strVPServiceProviderID =
		// ptype.equals("p")?"ServiceProvider00025":"ServiceProvider00207";
		String strVPServiceProviderID = "ServiceProvider00207";
		String strPayMethod = ptype;
		String strPayAgentID = "";
		String strItemID = "";
		String strPayMoney = ptype.equals("p") ? "" : pmoney;
		// String strPayMoney = ptype.equals("p")?"":pmoney;
		String strPayInfoPassURL = "http://www.putzone.com/putexas/grpm.do";
		String strPostBackURL = "http://www.putzone.com/putexas/ggitem.do";
		String strCPOrderID = orderID;
		String strMemo1 = "";
		String strMemo2 = "";
		// String strKey =
		// ptype.equals("p")?"YD1LJL4MKXQCU3YLCPHL1HAAXZ4FKO04WCX9W8U2JA1WUPHIW":"HUKVRO00RACKKRA5VAR8P2PVOUUK60JI1SIU0F3Y2LHZ6E423L0";
		String strKey = "1KKUP9FB75XV65YG0HNIUDKD9QI0WGGQGE214OQX93";
		String strClientIP = clientIP;

		String strTohash = (strAAAStoreID + "&" + strVPServiceProviderID + "&"
				+ strPayMethod + "&" + strPayAgentID + "&" + strPayMoney + "&"
				+ strCPOrderID + "&" + strKey).toLowerCase();
		// 生产认证码
		MessageDigest sha1 = null;
		try
		{
			sha1 = MessageDigest.getInstance("SHA1");
		}
		catch (NoSuchAlgorithmException e)
		{
			System.out.println("===MessageDigest==");
		}
		String strVCode = (PayUtils.bytes2String(sha1.digest(strTohash
				.getBytes()))).toLowerCase();
		// 将信息提交到橘子游戏支付平台
		String post = "<html><body><form name='contentPay' id='contentPay' method='post' action='"
				+ str3in1URL + "'>";
		post += "<input type='hidden' id='AAAStoreID' name='AAAStoreID' value='"
				+ strAAAStoreID + "' />";
		post += "<input type='hidden' id='VPServiceProviderID' name='VPServiceProviderID' value='"
				+ strVPServiceProviderID + "' />";
		post += "<input type='hidden' id='PayMethod' name='PayMethod' value='"
				+ strPayMethod + "'/>";
		post += "<input type='hidden' id='PayAgentID' name='PayAgentID' value='"
				+ strPayAgentID + "' />";
		post += "<input type='hidden' id='ItemID' name='ItemID' value='"
				+ strItemID + "' />";
		post += "<input type='hidden' id='PayMoney' name='PayMoney' value='"
				+ strPayMoney + "' />";
		post += "<input type='hidden' id='PayInfoPassURL' name='PayInfoPassURL' value='"
				+ strPayInfoPassURL + "' />";
		post += "<input type='hidden' id='PostBackURL' name='PostBackURL' value='"
				+ strPostBackURL + "' />";
		post += "<input type='hidden' id='CPOrderID' name='CPOrderID' value='"
				+ strCPOrderID + "' />";
		post += "<input type='hidden' id='Memo1' name='Memo1' value='"
				+ strMemo1 + "' />";
		post += "<input type='hidden' id='Memo2' name='Memo2' value='"
				+ strMemo2 + "' />";
		post += "<input type='hidden' id='Vcode' name='Vcode' value='"
				+ strVCode + "' />";
		post += "<input type='hidden' id='ClientIP' name='ClientIP' value='"
				+ strClientIP + "' />";
		post += "</form>";
		post += "<script language='javascript' type='text/javascript'>document.contentPay.submit();</script></body></html>";
		return post;

	}

	// 将信息封装，发送到Paypal支付平台
	public static String postMsgToPaypal(String custom, String pmoney)
	{
		String paypalurl = "https://www.paypal.com/cgi-bin/webscr";
		// String paypalurl = "http://www.sandbox.paypal.com/cgi-bin/webscr";
		String post = "<html><body><form name='paypalform' target='paypal' method='post' action='"
				+ paypalurl + "'>";
		post += "<input type='hidden' name='cmd' value='_xclick'/>";
		post += "<input type='hidden' name='business' value='40143116@qq.com'/>";
		post += "<input type='hidden' name='item_name' value='Happy gold coin'/>";
		post += "<input type='hidden' name='item_number' value='1'/>";
		post += "<input type='hidden' name='amount' value='" + pmoney + "'/>";
		post += "<input type='hidden' name='custom' value='" + custom + "'/>";
		post += "<input type='hidden' name='currency_code' value='USD'/>";
		post += "<input type='hidden' name='lc' value='US'/>";
		post += "</form>";
		post += "<script language='javascript' type='text/javascript'>document.paypalform.submit();</script></body></html>";
		return post;

	}

	// 将信息封装，发送到MOl支付平台
	// getHeartBeat
	public static String getMolHeartBeat()
	{
		// String API_URL = "http://molv3.molsolutions.com/api/login";
		// String MerchantID = "201011037357" ;
		String API_URL = PayFinalValue.MOL_API_URL;
		String MerchantID = PayFinalValue.MOL_MerchantID;
		String req = "MerchantID=" + MerchantID;
		StringBuffer res = new StringBuffer();
		try
		{
			URL u = new URL(API_URL + PayFinalValue.MOL_URL_GetHeartBeat + req);
			URLConnection uc = u.openConnection();
			uc.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			BufferedReader in = new BufferedReader(new InputStreamReader(
					uc.getInputStream()));
			String line = null;
			while ((line = in.readLine()) != null)
			{
				res.append(line);
			}
			in.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			System.out.println("getMolHeartBeat error");
		}
		return res.toString();
	}



	// Mycard 之billing
	public static String getMcBillingAuthCode(String sid, String orderID,
			String pmoney)
	{

		// String mc_url = "http://test.b2b.mycard520.com.tw";
		String mc_url = PayFinalValue.MyCard_URL;
		String ServiceId = sid;
		String PaymentAmount = pmoney;
		String TradeSeq = orderID;

		String req = ServiceId + "/" + TradeSeq + "/" + PaymentAmount;
		StringBuffer res = new StringBuffer();
		try
		{
			URL u = new URL(mc_url + PayFinalValue.MyCard_URL_getBillingAuth
					+ req);
			URLConnection uc = u.openConnection();
			uc.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			BufferedReader in = new BufferedReader(new InputStreamReader(
					uc.getInputStream()));
			String line = null;
			while ((line = in.readLine()) != null)
			{
				res.append(line);
			}
			in.close();
		}
		catch (IOException e)
		{
			System.out.println("getMcBillingAuthCode error");
		}
		return res.toString();
	}

	// Mycard 之会员扣点
	public static String getMcPointAuthCode(String orderID, String pmoney)
	{
		String mc_url = PayFinalValue.MyCard_URL;
		String FactoryId = PayFinalValue.MyCard_FactoryId;
		String FactoryServiceId = PayFinalValue.MyCard_ServiceId;
		String FactorySeq = orderID;
		String PointPayment = pmoney;
		String BonusPayment = "0";
		String FactoryReturnUrl = "http://www.putzone.com/putexas/mcp.do";
		String req = "FactoryId=" + FactoryId + "&FactoryServiceId="
				+ FactoryServiceId + "&FactorySeq=" + FactorySeq
				+ "&PointPayment=" + PointPayment + "&BonusPayment="
				+ BonusPayment + "&FactoryReturnUrl=" + FactoryReturnUrl;
		req = req.trim();
		StringBuffer res = new StringBuffer();
		try
		{
			URL u = new URL(mc_url + PayFinalValue.MyCard_URL_ServiceAuth + req);
			URLConnection uc = u.openConnection();
			/*
			 * uc.setDoOutput(true); uc.setRequestProperty("Content-Type",
			 * "application/x-www-form-urlencoded"); PrintWriter pw = new
			 * PrintWriter(uc.getOutputStream()); pw.println(req.trim());
			 * pw.flush(); pw.close();
			 */
			BufferedReader in = new BufferedReader(new InputStreamReader(
					uc.getInputStream()));
			String line = null;
			while ((line = in.readLine()) != null)
			{
				res.append(line);
			}
			in.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			System.out.println("payByMc_point error");
		}
		return res.toString();
	}

	// Mycard 之billing驗證MyCard交易
	public static String confirmBillingTrade(String authCode)
	{
		String mc_url = PayFinalValue.MyCard_URL;
		String req = "AuthCode=" + authCode;
		StringBuffer res = new StringBuffer();
		try
		{
			URL u = new URL(
					mc_url
							+ "/MyCardBillingRESTSrv/MyCardBillingRESTSrv.svc/TradeQuery?"
							+ req);
			URLConnection uc = u.openConnection();
			uc.setDoOutput(true);
			uc.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			/*
			 * PrintWriter pw = new PrintWriter(uc.getOutputStream());
			 * pw.println(req); pw.flush(); pw.close();
			 */
			BufferedReader in = new BufferedReader(new InputStreamReader(
					uc.getInputStream()));
			String line = null;
			while ((line = in.readLine()) != null)
			{
				res.append(line);
			}
			in.close();
		}
		catch (IOException e)
		{
			System.out.println("confirmBillingTrade error");
		}
		return res.toString();
	}

	// 成功後呼叫MyCard 請款之Web Service
	public static String confirmBillingPayment(String authCode, String CPCustId)
	{
		String mc_url = PayFinalValue.MyCard_URL;
		String req = "AuthCode=" + authCode + "&CPCustId=" + CPCustId;
		StringBuffer res = new StringBuffer();
		try
		{
			URL u = new URL(mc_url + PayFinalValue.MyCard_URL_PaymentConfirm
					+ req);
			URLConnection uc = u.openConnection();
			uc.setDoOutput(true);
			uc.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			/*
			 * PrintWriter pw = new PrintWriter(uc.getOutputStream());
			 * pw.println(req); pw.flush(); pw.close();
			 */
			BufferedReader in = new BufferedReader(new InputStreamReader(
					uc.getInputStream()));
			String line = null;
			while ((line = in.readLine()) != null)
			{
				res.append(line);
			}
			in.close();
		}
		catch (IOException e)
		{
			System.out.println("confirmBillingPayment error");
		}
		return res.toString();
	}

	// Mycard 之会员扣点交易确认
	public static String confirmTrade(String authCode, String otp)
	{
		String mc_url = PayFinalValue.MyCard_URL;
		String req = "AuthCode=" + authCode + "&OneTimePassword=" + otp;
		StringBuffer res = new StringBuffer();
		try
		{
			URL u = new URL(mc_url + PayFinalValue.MyCard_URL_MemberCLRender
					+ req);
			URLConnection uc = u.openConnection();
			uc.setDoOutput(true);
			uc.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			// PrintWriter pw = new PrintWriter(uc.getOutputStream());
			// pw.println(req);
			// pw.flush();
			// pw.close();
			BufferedReader in = new BufferedReader(new InputStreamReader(
					uc.getInputStream()));
			String line = null;
			while ((line = in.readLine()) != null)
			{
				res.append(line);
			}
			in.close();
		}
		catch (IOException e)
		{
			System.out.println("confirmTrade error");
		}
		return res.toString();
	}

	// Mycard 之会员储值in game
	public static String postMsgToMycardInGame(String cid, String psd,
			String orderID, String uid)
	{
		String mc_url = PayFinalValue.MyCard_URL_InGameRender;
		String MyCardCardId = cid;
		String MyCardPwd = psd;
		String GameFacID = PayFinalValue.MyCard_GameFacID;
		String GameUser = uid;
		String Game_No = orderID;
		String GameCard_ID = cid;
		String req = "MyCardCardId=" + MyCardCardId + "&MyCardPwd=" + MyCardPwd
				+ "&GameFacID=" + GameFacID + "&GameUser=" + GameUser
				+ "&Game_No=" + Game_No + "&GameCard_ID=" + GameCard_ID;
		StringBuffer res = new StringBuffer();
		try
		{
			URL u = new URL(mc_url);
			URLConnection uc = u.openConnection();
			uc.setDoOutput(true);
			uc.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			PrintWriter pw = new PrintWriter(uc.getOutputStream());
			pw.println(req);
			pw.flush();
			pw.close();
			BufferedReader in = new BufferedReader(new InputStreamReader(
					uc.getInputStream()));
			String line = null;
			while ((line = in.readLine()) != null)
			{
				res.append(line);
			}
			in.close();
		}
		catch (IOException e)
		{
			System.out.println("postMsgToMycardInGame error");
		}
		return res.toString();
	}

	// Mycard 之会员查询交易信息
	public static String TradeQuery(String authCode, String otp)
	{
		String mc_url = PayFinalValue.MyCard_URL;
		String req = "AuthCode=" + authCode + "&OneTimePassword=" + otp;
		StringBuffer res = new StringBuffer();
		try
		{
			URL u = new URL(mc_url + PayFinalValue.MyCard_URL_TradeQuery + req);
			URLConnection uc = u.openConnection();
			uc.setDoOutput(true);
			uc.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");

			BufferedReader in = new BufferedReader(new InputStreamReader(
					uc.getInputStream()));
			String line = null;
			while ((line = in.readLine()) != null)
			{
				res.append(line);
			}
			in.close();
		}
		catch (IOException e)
		{
			System.out.println("TradeQuery error");
		}
		return res.toString();
	}

	// Mycard 之会员扣点 postMsgToMycardPoint
	public static String postMsgToMycardPoint(String res)
	{
		String rmn = "ReturnMsgNo";
		String rts = "ReturnTradeSeq";
		String rac = "ReturnAuthCode";
		String url = PayFinalValue.MyCard_URL_Point;
		String postMsg = "";
		rmn = getValueByTag(res, rmn);
		if (rmn.equals("1"))
		{
			rts = PayUtils.getValueByTag(res, rts);
			rac = PayUtils.getValueByTag(res, rac);
			postMsg = "<html><body><form name='paypalform' method='get' action='"
					+ url + "'>";
			postMsg += "<input type='hidden' name='AuthCode' value='" + rac
					+ "'/>";
			postMsg += "</form>";
			postMsg += "<script language='javascript' type='text/javascript'>document.paypalform.submit();</script></body></html>";
		}
		return postMsg;

	}

	// Mycard 之billing
	public static String postMsgToMycardBilling(String AuthCode)
	{
		String url = PayFinalValue.MyCard_URL_Billing;
		String postMsg = "";
		postMsg = "<html><body><form name='paypalform' method='get' action='"
				+ url + "'>";
		postMsg += "<input type='hidden' name='AuthCode' value='" + AuthCode
				+ "'/>";
		postMsg += "</form>";
		postMsg += "<script language='javascript' type='text/javascript'>document.paypalform.submit();</script></body></html>";
		return postMsg;

	}

	// 解析字符串
	public static String[] getValueFromRes(String s)
	{
		s = s.substring(s.indexOf(">") + 1, s.indexOf("</string>")).trim();
		StringTokenizer st = new StringTokenizer(s, "|");
		String[] strArry = new String[st.countTokens()];
		int i = 0;
		while (st.hasMoreElements())
		{
			String nextElement = st.nextToken().trim();
			strArry[i] = nextElement;
			i++;
		}
		return strArry;

	}

	// Mycard 之会员扣点 getValueByTag
	public static String getValueByTag(String s, String tag)
	{
		String startTag = "<" + tag + ">";
		String endTag = "</" + tag + ">";
		String value;
		try
		{
			value = s.substring(s.indexOf(startTag) + startTag.length(),
					s.indexOf(endTag)).trim();
		}
		catch (Exception e)
		{
			value = "";
		}
		return value;
	}

	// 生成唯一的订单号
	public static synchronized String getUniqueOrderID()
	{
		return "p"
				+ (new SimpleDateFormat("yyyyMMddHHmmss")).format(new Date())
				+ PayUtils.strByRandomNum();
	}

	// 根据充值的金额，获取兑换的金币
	public static int getGoldByMoney(String money)
	{
		int gold = 0;
		HashMap<String, Integer> excmap = getExchangeMap();
		for (String key : excmap.keySet())
		{
			if (money.equals(key))
			{
				gold = excmap.get(key);
			}
		}
		return gold;
	}

	// 获取gold money 的兑换值
	public static HashMap<String, Integer> getExchangeMap()
	{
		if (null == excmap)
		{
			excmap = new HashMap<String, Integer>();
			excmap.put("1", 1);
			excmap.put("50", 10);
			excmap.put("100", 22);
			excmap.put("150", 33);
			excmap.put("300", 75);
			excmap.put("350", 88);
			excmap.put("369", 93);
			excmap.put("400", 100);
			excmap.put("450", 113);
			excmap.put("480", 122);
			excmap.put("500", 125);
			excmap.put("1000", 313);
			excmap.put("1150", 324);
			excmap.put("2000", 688);
			excmap.put("3000", 1032);
			excmap.put("4000", 1375);
		}
		return excmap;
	}

	/*
	 * 验证参数 是否正确
	 */
	public static List PramValueList()
	{
		if (null == pvlist)
		{
			pvlist = new ArrayList();
			pvlist.add(PayFinalValue.PV_PAY_GAMANIA_A);
			pvlist.add(PayFinalValue.PV_PAY_GAMANIA_P);
			pvlist.add(PayFinalValue.PV_PAY_MOL_Mol);
			pvlist.add(PayFinalValue.PV_PAY_MYCARD_Billing);
			pvlist.add(PayFinalValue.PV_PAY_MYCARD_InGame);
			pvlist.add(PayFinalValue.PV_PAY_MYCARD_Point);
			pvlist.add(PayFinalValue.PV_PAY_PAYPAL_Paypal);
		}
		return pvlist;
	}

	public static boolean volidateValue(String pmethod)
	{
		return PramValueList().contains(pmethod);
	}

	/**
	 * 用户购买的游戏币类别
	 * 
	 * @param args
	 */
	public static int getGtypeByAmt(String amt)
	{
		int gold = 0;
		if (amt.equals("2.99"))
		{
			gold = 18;
		}
		else if (amt.equals("5.99"))
		{
			gold = 42;
		}
		else if (amt.equals("15.99"))
		{
			gold = 128;
		}
		else if (amt.equals("29.99"))
		{
			gold = 270;
		}
		else if (amt.equals("59.99"))
		{
			gold = 600;
		}
		else if (amt.equals("99.99"))
		{
			gold = 1100;
		}
		return gold;
	}

	// 判断美金是否符合规定
	public static boolean checkAmtByDollar(String amt)
	{
		return getGtypeByAmt(amt) == 0;
	}

	public static int getGtypeByMyr(String amt)
	{
		int gold = 0;
		if (amt.equals("9"))
		{
			gold = 18;
		}
		else if (amt.equals("18"))
		{
			gold = 42;
		}
		else if (amt.equals("48"))
		{
			gold = 128;
		}
		else if (amt.equals("90"))
		{
			gold = 270;
		}
		else if (amt.equals("180"))
		{
			gold = 600;
		}
		else if (amt.equals("300"))
		{
			gold = 1100;
		}
		else if (amt.equals("1"))
		{
			gold = 1;
		}
		return gold;
	}

	// 判断马币是否符合规定
	public static boolean checkAmtByMyr(String amt)
	{
		return getGtypeByMyr(amt) == 0;
	}

	public static boolean checkPayValues(String orderID, String pmethod,
			String pmoney)
	{
		boolean flag = false;
		if (orderID.length() != 19)
		{
			flag = true;
		}
		else if (!PayUtils.volidateValue(pmethod))
		{
			flag = true;
		}
		else
		{
			if (pmethod.equals(PayFinalValue.PV_PAY_MOL_Mol))
			{
				flag = checkAmtByMyr(pmoney);
			}
			else if (pmethod.equals(PayFinalValue.PV_PAY_PAYPAL_Paypal))
			{
				flag = checkAmtByDollar(pmoney);
			}
			else if (pmethod.equals(PayFinalValue.PV_PAY_MYCARD_InGame))
			{
				flag = false;
			}
			else
			{
				flag = !checkPayMoney(pmoney);
			}
		}
		return flag;
	}

	/**
	 * 
	 * @return
	 */
	public static HashMap<String, String> getMcBilling()
	{
		if (null == mcBilling)
		{
			/*
			 * 亞太電信 MyCard點數 開心金幣10點 Mycard點數 SPS0052403 50 亞太電信 MyCard點數
			 * 開心金幣22點 Mycard點數 SPS0052544 100 亞太電信 MyCard點數 開心金幣33點 Mycard點數
			 * SPS0052694 150 亞太電信 MyCard點數 開心金幣75點 Mycard點數 SPS0052636 300 亞太電信
			 * MyCard點數 開心金幣88點 Mycard點數 SPS0052660 350 亞太電信 MyCard點數 開心金幣93點
			 * Mycard點數 SPS0052729 369 亞太電信 MyCard點數 開心金幣100點 Mycard點數
			 * SPS0052427 400 亞太電信 MyCard點數 開心金幣113點 Mycard點數 SPS0052471 450
			 * 亞太電信 MyCard點數 開心金幣122點 Mycard點數 SPS0052495 480 亞太電信 MyCard點數
			 * 開心金幣125點 Mycard點數 SPS0052519 500 亞太電信 MyCard點數 開心金幣313點 Mycard點數
			 * SPS0052568 1000 亞太電信 MyCard點數 開心金幣324點 Mycard點數 SPS0052591 1150
			 * 亞太電信 MyCard點數 開心金幣688點 Mycard點數 SPS0052612 2000 亞太電信 MyCard點數
			 * 開心金幣1032點 Mycard點數 SPS0052447 3000
			 */
			mcBilling = new HashMap<String, String>();
			mcBilling.put("50" + PayFinalValue.MYCARD_YTDX, "SPS0052403");
			mcBilling.put("100" + PayFinalValue.MYCARD_YTDX, "SPS0052544");
			mcBilling.put("150" + PayFinalValue.MYCARD_YTDX, "SPS0052694");
			mcBilling.put("300" + PayFinalValue.MYCARD_YTDX, "SPS0052636");
			mcBilling.put("350" + PayFinalValue.MYCARD_YTDX, "SPS0052660");
			mcBilling.put("369" + PayFinalValue.MYCARD_YTDX, "SPS0052729");
			mcBilling.put("400" + PayFinalValue.MYCARD_YTDX, "SPS0052427");
			mcBilling.put("450" + PayFinalValue.MYCARD_YTDX, "SPS0052471");
			mcBilling.put("480" + PayFinalValue.MYCARD_YTDX, "SPS0052495");
			mcBilling.put("500" + PayFinalValue.MYCARD_YTDX, "SPS0052519");
			mcBilling.put("1000" + PayFinalValue.MYCARD_YTDX, "SPS0052568");
			mcBilling.put("1150" + PayFinalValue.MYCARD_YTDX, "SPS0052591");
			mcBilling.put("2000" + PayFinalValue.MYCARD_YTDX, "SPS0052612");
			mcBilling.put("3000" + PayFinalValue.MYCARD_YTDX, "SPS0052447");

			/*
			 * 開心金幣10點 Mycard點數 SPS0052409 50 開心金幣22點 Mycard點數 SPS0052550 100
			 * 開心金幣33點 Mycard點數 SPS0052700 150 開心金幣75點 Mycard點數 SPS0052642 300
			 * 開心金幣88點 Mycard點數 SPS0052666 350 開心金幣93點 Mycard點數 SPS0052735 369
			 * 開心金幣100點 Mycard點數 SPS0052433 400 開心金幣113點 Mycard點數 SPS0052477 450
			 * 開心金幣122點 Mycard點數 SPS0052501 480 開心金幣125點 Mycard點數 SPS0052525 500
			 * 開心金幣313點 Mycard點數 SPS0052574 1000 開心金幣324點 Mycard點數 SPS0052597
			 * 1150 開心金幣688點 Mycard點數 SPS0052618 2000 開心金幣1032點 Mycard點數
			 * SPS0052453 3000
			 */
			mcBilling.put("50" + PayFinalValue.MYCARD_YCDX, "SPS0052409");
			mcBilling.put("100" + PayFinalValue.MYCARD_YCDX, "SPS0052550");
			mcBilling.put("150" + PayFinalValue.MYCARD_YCDX, "SPS0052700");
			mcBilling.put("300" + PayFinalValue.MYCARD_YCDX, "SPS0052642");
			mcBilling.put("350" + PayFinalValue.MYCARD_YCDX, "SPS0052666");
			mcBilling.put("369" + PayFinalValue.MYCARD_YCDX, "SPS0052735");
			mcBilling.put("400" + PayFinalValue.MYCARD_YCDX, "SPS0052433");
			mcBilling.put("450" + PayFinalValue.MYCARD_YCDX, "SPS0052477");
			mcBilling.put("480" + PayFinalValue.MYCARD_YCDX, "SPS0052501");
			mcBilling.put("500" + PayFinalValue.MYCARD_YCDX, "SPS0052525");
			mcBilling.put("1000" + PayFinalValue.MYCARD_YCDX, "SPS0052574");
			mcBilling.put("1150" + PayFinalValue.MYCARD_YCDX, "SPS0052597");
			mcBilling.put("2000" + PayFinalValue.MYCARD_YCDX, "SPS0052618");
			mcBilling.put("3000" + PayFinalValue.MYCARD_YCDX, "SPS0052453");

			/*
			 * 中華電信839 MyCard點數1000點 開心金幣313點 Mycard點數 SPS0052558 1000 中華電信839
			 * MyCard點數100點 開心金幣22點 Mycard點數 SPS0052534 100 中華電信839
			 * MyCard點數1150點 開心金幣324點 Mycard點數 SPS0052581 1150 中華電信839
			 * MyCard點數150點 開心金幣33點 Mycard點數 SPS0052684 150 中華電信839 MyCard點數300點
			 * 開心金幣75點 Mycard點數 SPS0052626 300 中華電信839 MyCard點數350點 開心金幣88點
			 * Mycard點數 SPS0052650 350 中華電信839 MyCard點數369點 開心金幣93點 Mycard點數
			 * SPS0052721 369 中華電信839 MyCard點數400點 開心金幣100點 Mycard點數 SPS0052417
			 * 400 中華電信839 MyCard點數450點 開心金幣113點 Mycard點數 SPS0052461 450 中華電信839
			 * MyCard點數480點 開心金幣122點 Mycard點數 SPS0052485 480 中華電信839
			 * MyCard點數500點 開心金幣125點 Mycard點數 SPS0052509 500 中華電信839 MyCard點數50點
			 * 開心金幣10點 Mycard點數 SPS0052393 50
			 */
			mcBilling.put("50" + PayFinalValue.MYCARD_ZHDX, "SPS0052393");
			mcBilling.put("100" + PayFinalValue.MYCARD_ZHDX, "SPS0052534");
			mcBilling.put("150" + PayFinalValue.MYCARD_ZHDX, "SPS0052684");
			mcBilling.put("300" + PayFinalValue.MYCARD_ZHDX, "SPS0052626");
			mcBilling.put("350" + PayFinalValue.MYCARD_ZHDX, "SPS0052650");
			mcBilling.put("369" + PayFinalValue.MYCARD_ZHDX, "SPS0052721");
			mcBilling.put("400" + PayFinalValue.MYCARD_ZHDX, "SPS0052417");
			mcBilling.put("450" + PayFinalValue.MYCARD_ZHDX, "SPS0052461");
			mcBilling.put("480" + PayFinalValue.MYCARD_ZHDX, "SPS0052485");
			mcBilling.put("500" + PayFinalValue.MYCARD_ZHDX, "SPS0052509");
			mcBilling.put("1000" + PayFinalValue.MYCARD_ZHDX, "SPS0052558");
			mcBilling.put("1150" + PayFinalValue.MYCARD_ZHDX, "SPS0052581");

			/*
			 * 台哥大三合一(含東信及泛亞) MyCard點數1000點 開心金幣313點 Mycard點數 SPS0052563 1000
			 * 台哥大三合一(含東信及泛亞) MyCard點數100點 開心金幣22點 Mycard點數 SPS0052539 100
			 * 台哥大三合一(含東信及泛亞) MyCard點數1150點 開心金幣324點 Mycard點數 SPS0052586 1150
			 * 台哥大三合一(含東信及泛亞) MyCard點數150點 開心金幣33點 Mycard點數 SPS0052689 150
			 * 台哥大三合一(含東信及泛亞) MyCard點數2000點 開心金幣688點 Mycard點數 SPS0052607 2000
			 * 台哥大三合一(含東信及泛亞) MyCard點數300點 開心金幣75點 Mycard點數 SPS0052631 300
			 * 台哥大三合一(含東信及泛亞) MyCard點數350點 開心金幣88點 Mycard點數 SPS0052655 350
			 * 台哥大三合一(含東信及泛亞) MyCard點數400點 開心金幣100點 Mycard點數 SPS0052422 400
			 * 台哥大三合一(含東信及泛亞) MyCard點數450點 開心金幣113點 Mycard點數 SPS0052466 450
			 * 台哥大三合一(含東信及泛亞) MyCard點數480點 開心金幣122點 Mycard點數 SPS0052490 480
			 * 台哥大三合一(含東信及泛亞) MyCard點數500點 開心金幣125點 Mycard點數 SPS0052514 500
			 * 台哥大三合一(含東信及泛亞) MyCard點數50點 開心金幣10點 Mycard點數 SPS0052398 50
			 */
			mcBilling.put("50" + PayFinalValue.MYCARD_TGD, "SPS0052398");
			mcBilling.put("100" + PayFinalValue.MYCARD_TGD, "SPS0052539");
			mcBilling.put("150" + PayFinalValue.MYCARD_TGD, "SPS0052689");
			mcBilling.put("300" + PayFinalValue.MYCARD_TGD, "SPS0052631");
			mcBilling.put("350" + PayFinalValue.MYCARD_TGD, "SPS0052655");
			mcBilling.put("400" + PayFinalValue.MYCARD_TGD, "SPS0052422");
			mcBilling.put("450" + PayFinalValue.MYCARD_TGD, "SPS0052466");
			mcBilling.put("480" + PayFinalValue.MYCARD_TGD, "SPS0052490");
			mcBilling.put("500" + PayFinalValue.MYCARD_TGD, "SPS0052514");
			mcBilling.put("1000" + PayFinalValue.MYCARD_TGD, "SPS0052563");
			mcBilling.put("1150" + PayFinalValue.MYCARD_TGD, "SPS0052586");
			mcBilling.put("2000" + PayFinalValue.MYCARD_TGD, "SPS0052607");

			// 中華電信市內電話輕鬆付 MyCard點數1000點 開心金幣313點 Mycard點數 SPS0052560 1000
			// 中華電信市內電話輕鬆付 MyCard點數100點 開心金幣22點 Mycard點數 SPS0052536 100
			// 中華電信市內電話輕鬆付 MyCard點數1150點 開心金幣324點 Mycard點數 SPS0052583 1150
			// 中華電信市內電話輕鬆付 MyCard點數150點 開心金幣33點 Mycard點數 SPS0052686 150
			// 中華電信市內電話輕鬆付 MyCard點數300點 開心金幣75點 Mycard點數 SPS0052628 300
			// 中華電信市內電話輕鬆付 MyCard點數350點 開心金幣88點 Mycard點數 SPS0052652 350
			// 中華電信市內電話輕鬆付 MyCard點數369點 開心金幣93點 Mycard點數 SPS0052722 369
			// 中華電信市內電話輕鬆付 MyCard點數400點 開心金幣100點 Mycard點數 SPS0052419 400
			// 中華電信市內電話輕鬆付 MyCard點數450點 開心金幣113點 Mycard點數 SPS0052463 450
			// 中華電信市內電話輕鬆付 MyCard點數480點 開心金幣122點 Mycard點數 SPS0052487 480
			// 中華電信市內電話輕鬆付 MyCard點數500點 開心金幣125點 Mycard點數 SPS0052511 500
			// 中華電信市內電話輕鬆付 MyCard點數50點 開心金幣10點 Mycard點數 SPS0052395 50
			mcBilling.put("50" + PayFinalValue.MYCARD_ZHSH, "SPS0052395");
			mcBilling.put("100" + PayFinalValue.MYCARD_ZHSH, "SPS0052536");
			mcBilling.put("150" + PayFinalValue.MYCARD_ZHSH, "SPS0052686");
			mcBilling.put("300" + PayFinalValue.MYCARD_ZHSH, "SPS0052628");
			mcBilling.put("350" + PayFinalValue.MYCARD_ZHSH, "SPS0052652");
			mcBilling.put("369" + PayFinalValue.MYCARD_ZHSH, "SPS0052722");
			mcBilling.put("400" + PayFinalValue.MYCARD_ZHSH, "SPS0052419");
			mcBilling.put("450" + PayFinalValue.MYCARD_ZHSH, "SPS0052463");
			mcBilling.put("480" + PayFinalValue.MYCARD_ZHSH, "SPS0052487");
			mcBilling.put("500" + PayFinalValue.MYCARD_ZHSH, "SPS0052511");
			mcBilling.put("1000" + PayFinalValue.MYCARD_ZHSH, "SPS0052560");
			mcBilling.put("1150" + PayFinalValue.MYCARD_ZHSH, "SPS0052583");

			/*
			 * 中國信託代收信用卡 MyCard點數 開心金幣93點 Mycard點數 SPS0052719 369 中國信託代收信用卡
			 * MyCard點數 開心金幣122點 Mycard點數 SPS0052483 480 中國信託代收信用卡 MyCard點數
			 * 開心金幣688點 Mycard點數 SPS0052603 2000 中國信託代收信用卡 MyCard點數 開心金幣1032點
			 * Mycard點數 SPS0052439 3000 中國信託代收信用卡 MyCard點數1000點 開心金幣313點
			 * Mycard點數 SPS0052556 1000 中國信託代收信用卡 MyCard點數100點 開心金幣22點 Mycard點數
			 * SPS0052531 100 中國信託代收信用卡 MyCard點數1150點 開心金幣324點 Mycard點數
			 * SPS0052579 1150 中國信託代收信用卡 MyCard點數150點 開心金幣33點 Mycard點數
			 * SPS0052681 150 中國信託代收信用卡 MyCard點數300點 開心金幣75點 Mycard點數 SPS0052624
			 * 300 中國信託代收信用卡 MyCard點數350點 開心金幣88點 Mycard點數 SPS0052648 350
			 * 中國信託代收信用卡 MyCard點數400點 開心金幣100點 Mycard點數 SPS0052415 400 中國信託代收信用卡
			 * MyCard點數450點 開心金幣113點 Mycard點數 SPS0052459 450 中國信託代收信用卡
			 * MyCard點數500點 開心金幣125點 Mycard點數 SPS0052507 500 中國信託代收信用卡
			 * MyCard點數50點 開心金幣10點 Mycard點數 SPS0052390 50
			 */
			mcBilling.put("50" + PayFinalValue.MYCARD_XYK_SD, "SPS0052390");
			mcBilling.put("100" + PayFinalValue.MYCARD_XYK_SD, "SPS0052531");
			mcBilling.put("150" + PayFinalValue.MYCARD_XYK_SD, "SPS0052681");
			mcBilling.put("300" + PayFinalValue.MYCARD_XYK_SD, "SPS0052624");
			mcBilling.put("350" + PayFinalValue.MYCARD_XYK_SD, "SPS0052648");
			mcBilling.put("369" + PayFinalValue.MYCARD_XYK_SD, "SPS0052719");
			mcBilling.put("400" + PayFinalValue.MYCARD_XYK_SD, "SPS0052415");
			mcBilling.put("450" + PayFinalValue.MYCARD_XYK_SD, "SPS0052459");
			mcBilling.put("480" + PayFinalValue.MYCARD_XYK_SD, "SPS0052483");
			mcBilling.put("500" + PayFinalValue.MYCARD_XYK_SD, "SPS0052507");
			mcBilling.put("1000" + PayFinalValue.MYCARD_XYK_SD, "SPS0052556");
			mcBilling.put("1150" + PayFinalValue.MYCARD_XYK_SD, "SPS0052579");
			mcBilling.put("2000" + PayFinalValue.MYCARD_XYK_SD, "SPS0052603");
			mcBilling.put("3000" + PayFinalValue.MYCARD_XYK_SD, "SPS0052439");

			/*
			 * 中國信託信用卡紅利兌換 MyCard點數100點 開心金幣22點 Mycard點數 SPS0052532 100
			 * 中國信託信用卡紅利兌換 MyCard點數150點 開心金幣33點 Mycard點數 SPS0052682 150
			 * 中國信託信用卡紅利兌換 MyCard點數50點 開心金幣10點 Mycard點數 SPS0052391 50
			 */
			mcBilling.put("50" + PayFinalValue.MYCARD_XYK_HLDH, "SPS0052391");
			mcBilling.put("100" + PayFinalValue.MYCARD_XYK_HLDH, "SPS0052532");
			mcBilling.put("150" + PayFinalValue.MYCARD_XYK_HLDH, "SPS0052682");

			/*
			 * 中華電信HiNet MyCard點數1000點 開心金幣313點 Mycard點數 SPS0052559 1000
			 * 中華電信HiNet MyCard點數100點 開心金幣22點 Mycard點數 SPS0052535 100 中華電信HiNet
			 * MyCard點數1150點 開心金幣324點 Mycard點數 SPS0052582 1150 中華電信HiNet
			 * MyCard點數150點 開心金幣33點 Mycard點數 SPS0052685 150 中華電信HiNet
			 * MyCard點數300點 開心金幣75點 Mycard點數 SPS0052627 300 中華電信HiNet
			 * MyCard點數350點 開心金幣88點 Mycard點數 SPS0052651 350 中華電信HiNet
			 * MyCard點數369點 開心金幣93點 Mycard點數 SPS0052884 369 中華電信HiNet
			 * MyCard點數400點 開心金幣100點 Mycard點數 SPS0052418 400 中華電信HiNet
			 * MyCard點數450點 開心金幣113點 Mycard點數 SPS0052462 450 中華電信HiNet
			 * MyCard點數480點 開心金幣122點 Mycard點數 SPS0052486 480 中華電信HiNet
			 * MyCard點數500點 開心金幣125點 Mycard點數 SPS0052510 500 中華電信HiNet
			 * MyCard點數50點 開心金幣10點 Mycard點數 SPS0052394 50
			 */

			mcBilling.put("50" + PayFinalValue.MYCARD_HINET, "SPS0052394");
			mcBilling.put("100" + PayFinalValue.MYCARD_HINET, "SPS0052535");
			mcBilling.put("150" + PayFinalValue.MYCARD_HINET, "SPS0052685");
			mcBilling.put("300" + PayFinalValue.MYCARD_HINET, "SPS0052627");
			mcBilling.put("350" + PayFinalValue.MYCARD_HINET, "SPS0052651");
			mcBilling.put("369" + PayFinalValue.MYCARD_HINET, "SPS0052884");
			mcBilling.put("400" + PayFinalValue.MYCARD_HINET, "SPS0052418");
			mcBilling.put("450" + PayFinalValue.MYCARD_HINET, "SPS0052462");
			mcBilling.put("480" + PayFinalValue.MYCARD_HINET, "SPS0052486");
			mcBilling.put("500" + PayFinalValue.MYCARD_HINET, "SPS0052510");
			mcBilling.put("1000" + PayFinalValue.MYCARD_HINET, "SPS0052559");
			mcBilling.put("1150" + PayFinalValue.MYCARD_HINET, "SPS0052582");

			/*
			 * Seednet MyCard點數1000點 開心金幣313點 Mycard點數 SPS0052551 1000 Seednet
			 * MyCard點數100點 開心金幣22點 Mycard點數 SPS0052526 100 Seednet
			 * MyCard點數2000點 開心金幣688點 Mycard點數 SPS0052598 2000 Seednet
			 * MyCard點數3000點 開心金幣1032點 Mycard點數 SPS0052434 3000 Seednet
			 * MyCard點數300點 開心金幣75點 Mycard點數 SPS0052619 300 Seednet MyCard點數350點
			 * 開心金幣88點 Mycard點數 SPS0052643 350 Seednet MyCard點數400點 開心金幣100點
			 * Mycard點數 SPS0052410 400 Seednet MyCard點數450點 開心金幣113點 Mycard點數
			 * SPS0052454 450 Seednet MyCard點數480點 開心金幣122點 Mycard點數 SPS0052478
			 * 480 Seednet MyCard點數500點 開心金幣125點 Mycard點數 SPS0052502 500 Seednet
			 * MyCard點數50點 開心金幣10點 Mycard點數 SPS0052385 50
			 */
			mcBilling.put("50" + PayFinalValue.MYCARD_SEEDNET, "SPS0052385");
			mcBilling.put("100" + PayFinalValue.MYCARD_SEEDNET, "SPS0052526");
			mcBilling.put("300" + PayFinalValue.MYCARD_SEEDNET, "SPS0052619");
			mcBilling.put("350" + PayFinalValue.MYCARD_SEEDNET, "SPS0052643");
			mcBilling.put("400" + PayFinalValue.MYCARD_SEEDNET, "SPS0052410");
			mcBilling.put("450" + PayFinalValue.MYCARD_SEEDNET, "SPS0052454");
			mcBilling.put("480" + PayFinalValue.MYCARD_SEEDNET, "SPS0052478");
			mcBilling.put("500" + PayFinalValue.MYCARD_SEEDNET, "SPS0052502");
			mcBilling.put("1000" + PayFinalValue.MYCARD_SEEDNET, "SPS0052551");
			mcBilling.put("2000" + PayFinalValue.MYCARD_SEEDNET, "SPS0052598");
			mcBilling.put("3000" + PayFinalValue.MYCARD_SEEDNET, "SPS0052434");

			/*
			 * So-Net MyCard point 100 開心金幣22點 Mycard點數 SPS0052527 100 So-Net
			 * MyCard point 1000 開心金幣313點 Mycard點數 SPS0052552 1000 So-Net MyCard
			 * point 1150 開心金幣324點 Mycard點數 SPS0052575 1150 So-Net MyCard point
			 * 150 開心金幣33點 Mycard點數 SPS0052677 150 So-Net MyCard point 2000
			 * 開心金幣688點 Mycard點數 SPS0052599 2000 So-Net MyCard point 300 開心金幣75點
			 * Mycard點數 SPS0052620 300 So-Net MyCard point 3000 開心金幣1032點
			 * Mycard點數 SPS0052435 3000 So-Net MyCard point 350 開心金幣88點 Mycard點數
			 * SPS0052644 350 So-Net MyCard point 369 開心金幣93點 Mycard點數
			 * SPS0052715 369 So-Net MyCard point 400 開心金幣100點 Mycard點數
			 * SPS0052411 400 So-Net MyCard point 450 開心金幣113點 Mycard點數
			 * SPS0052455 450 So-Net MyCard point 480 開心金幣122點 Mycard點數
			 * SPS0052479 480 So-Net MyCard point 50 開心金幣10點 Mycard點數 SPS0052386
			 * 50 So-Net MyCard point 500 開心金幣125點 Mycard點數 SPS0052503 500
			 */

			mcBilling.put("50" + PayFinalValue.MYCARD_SONET, "SPS0052386");
			mcBilling.put("100" + PayFinalValue.MYCARD_SONET, "SPS0052527");
			mcBilling.put("150" + PayFinalValue.MYCARD_SONET, "SPS0052677");
			mcBilling.put("300" + PayFinalValue.MYCARD_SONET, "SPS0052620");
			mcBilling.put("350" + PayFinalValue.MYCARD_SONET, "SPS0052644");
			mcBilling.put("369" + PayFinalValue.MYCARD_SONET, "SPS0052715");
			mcBilling.put("400" + PayFinalValue.MYCARD_SONET, "SPS0052411");
			mcBilling.put("450" + PayFinalValue.MYCARD_SONET, "SPS0052455");
			mcBilling.put("480" + PayFinalValue.MYCARD_SONET, "SPS0052479");
			mcBilling.put("500" + PayFinalValue.MYCARD_SONET, "SPS0052503");
			mcBilling.put("1000" + PayFinalValue.MYCARD_SONET, "SPS0052552");
			mcBilling.put("1150" + PayFinalValue.MYCARD_SONET, "SPS0052575");
			mcBilling.put("2000" + PayFinalValue.MYCARD_SONET, "SPS0052599");
			mcBilling.put("3000" + PayFinalValue.MYCARD_SONET, "SPS0052435");

			/*
			 * 中國信託WebATM MyCard點數 開心金幣93點 Mycard點數 SPS0052718 369 中國信託WebATM
			 * MyCard點數 開心金幣122點 Mycard點數 SPS0052482 480 中國信託WebATM
			 * MyCard點數1000點 開心金幣313點 Mycard點數 SPS0052555 1000 中國信託WebATM
			 * MyCard點數100點 開心金幣22點 Mycard點數 SPS0052530 100 中國信託WebATM
			 * MyCard點數1150點 開心金幣324點 Mycard點數 SPS0052578 1150 中國信託WebATM
			 * MyCard點數150點 開心金幣33點 Mycard點數 SPS0052680 150 中國信託WebATM
			 * MyCard點數2000點 開心金幣688點 Mycard點數 SPS0052602 2000 中國信託WebATM
			 * MyCard點數3000點 開心金幣1032點 Mycard點數 SPS0052438 3000 中國信託WebATM
			 * MyCard點數300點 開心金幣75點 Mycard點數 SPS0052623 300 中國信託WebATM
			 * MyCard點數350點 開心金幣88點 Mycard點數 SPS0052647 350 中國信託WebATM
			 * MyCard點數400點 開心金幣100點 Mycard點數 SPS0052414 400 中國信託WebATM
			 * MyCard點數450點 開心金幣113點 Mycard點數 SPS0052458 450 中國信託WebATM
			 * MyCard點數500點 開心金幣125點 Mycard點數 SPS0052506 500 中國信託WebATM
			 * MyCard點數50點 開心金幣10點 Mycard點數 SPS0052389 50
			 */
			mcBilling.put("50" + PayFinalValue.MYCARD_ZHXT, "SPS0052389");
			mcBilling.put("100" + PayFinalValue.MYCARD_ZHXT, "SPS0052530");
			mcBilling.put("150" + PayFinalValue.MYCARD_ZHXT, "SPS0052680");
			mcBilling.put("300" + PayFinalValue.MYCARD_ZHXT, "SPS0052623");
			mcBilling.put("350" + PayFinalValue.MYCARD_ZHXT, "SPS0052647");
			mcBilling.put("369" + PayFinalValue.MYCARD_ZHXT, "SPS0052718");
			mcBilling.put("400" + PayFinalValue.MYCARD_ZHXT, "SPS0052414");
			mcBilling.put("450" + PayFinalValue.MYCARD_ZHXT, "SPS0052458");
			mcBilling.put("480" + PayFinalValue.MYCARD_ZHXT, "SPS0052482");
			mcBilling.put("500" + PayFinalValue.MYCARD_ZHXT, "SPS0052506");
			mcBilling.put("1000" + PayFinalValue.MYCARD_ZHXT, "SPS0052555");
			mcBilling.put("1150" + PayFinalValue.MYCARD_ZHXT, "SPS0052578");
			mcBilling.put("2000" + PayFinalValue.MYCARD_ZHXT, "SPS0052602");
			mcBilling.put("3000" + PayFinalValue.MYCARD_ZHXT, "SPS0052438");

			/*
			 * 華南銀行WebATM MyCard點數 開心金幣93點 Mycard點數 SPS0052732 369 華南銀行WebATM
			 * MyCard點數 開心金幣122點 Mycard點數 SPS0052498 480 華南銀行WebATM
			 * MyCard點數1000點 開心金幣313點 Mycard點數 SPS0052571 1000 華南銀行WebATM
			 * MyCard點數100點 開心金幣22點 Mycard點數 SPS0052547 100 華南銀行WebATM
			 * MyCard點數1150點 開心金幣324點 Mycard點數 SPS0052594 1150 華南銀行WebATM
			 * MyCard點數150點 開心金幣33點 Mycard點數 SPS0052697 150 華南銀行WebATM
			 * MyCard點數2000點 開心金幣688點 Mycard點數 SPS0052615 2000 華南銀行WebATM
			 * MyCard點數3000點 開心金幣1032點 Mycard點數 SPS0052450 3000 華南銀行WebATM
			 * MyCard點數300點 開心金幣75點 Mycard點數 SPS0052639 300 華南銀行WebATM
			 * MyCard點數350點 開心金幣88點 Mycard點數 SPS0052663 350 華南銀行WebATM
			 * MyCard點數400點 開心金幣100點 Mycard點數 SPS0052430 400 華南銀行WebATM
			 * MyCard點數450點 開心金幣113點 Mycard點數 SPS0052474 450 華南銀行WebATM
			 * MyCard點數500點 開心金幣125點 Mycard點數 SPS0052522 500 華南銀行WebATM
			 * MyCard點數50點 開心金幣10點 Mycard點數 SPS0052406 50
			 */
			mcBilling.put("50" + PayFinalValue.MYCARD_HNYH, "SPS0052406");
			mcBilling.put("100" + PayFinalValue.MYCARD_HNYH, "SPS0052547");
			mcBilling.put("150" + PayFinalValue.MYCARD_HNYH, "SPS0052697");
			mcBilling.put("300" + PayFinalValue.MYCARD_HNYH, "SPS0052639");
			mcBilling.put("350" + PayFinalValue.MYCARD_HNYH, "SPS0052663");
			mcBilling.put("369" + PayFinalValue.MYCARD_HNYH, "SPS0052732");
			mcBilling.put("400" + PayFinalValue.MYCARD_HNYH, "SPS0052430");
			mcBilling.put("450" + PayFinalValue.MYCARD_HNYH, "SPS0052474");
			mcBilling.put("480" + PayFinalValue.MYCARD_HNYH, "SPS0052498");
			mcBilling.put("500" + PayFinalValue.MYCARD_HNYH, "SPS0052522");
			mcBilling.put("1000" + PayFinalValue.MYCARD_HNYH, "SPS0052571");
			mcBilling.put("1150" + PayFinalValue.MYCARD_HNYH, "SPS0052594");
			mcBilling.put("2000" + PayFinalValue.MYCARD_HNYH, "SPS0052615");
			mcBilling.put("3000" + PayFinalValue.MYCARD_HNYH, "SPS0052450");

			/*
			 * 中華郵政WebATM MyCard點數 開心金幣93點 Mycard點數 SPS0052720 369 中華郵政WebATM
			 * MyCard點數 開心金幣122點 Mycard點數 SPS0052484 480 中華郵政WebATM
			 * MyCard點數1000點 開心金幣313點 Mycard點數 SPS0052557 1000 中華郵政WebATM
			 * MyCard點數100點 開心金幣22點 Mycard點數 SPS0052533 100 中華郵政WebATM
			 * MyCard點數1150點 開心金幣324點 Mycard點數 SPS0052580 1150 中華郵政WebATM
			 * MyCard點數150點 開心金幣33點 Mycard點數 SPS0052683 150 中華郵政WebATM
			 * MyCard點數2000點 開心金幣688點 Mycard點數 SPS0052604 2000 中華郵政WebATM
			 * MyCard點數3000點 開心金幣1032點 Mycard點數 SPS0052440 3000 中華郵政WebATM
			 * MyCard點數300點 開心金幣75點 Mycard點數 SPS0052625 300 中華郵政WebATM
			 * MyCard點數350點 開心金幣88點 Mycard點數 SPS0052649 350 中華郵政WebATM
			 * MyCard點數400點 開心金幣100點 Mycard點數 SPS0052416 400 中華郵政WebATM
			 * MyCard點數450點 開心金幣113點 Mycard點數 SPS0052460 450 中華郵政WebATM
			 * MyCard點數500點 開心金幣125點 Mycard點數 SPS0052508 500 中華郵政WebATM
			 * MyCard點數50點 開心金幣10點 Mycard點數 SPS0052392 50
			 */
			mcBilling.put("50" + PayFinalValue.MYCARD_ZHYZ, "SPS0052392");
			mcBilling.put("100" + PayFinalValue.MYCARD_ZHYZ, "SPS0052533");
			mcBilling.put("150" + PayFinalValue.MYCARD_ZHYZ, "SPS0052683");
			mcBilling.put("300" + PayFinalValue.MYCARD_ZHYZ, "SPS0052625");
			mcBilling.put("350" + PayFinalValue.MYCARD_ZHYZ, "SPS0052649");
			mcBilling.put("369" + PayFinalValue.MYCARD_ZHYZ, "SPS0052720");
			mcBilling.put("400" + PayFinalValue.MYCARD_ZHYZ, "SPS0052416");
			mcBilling.put("450" + PayFinalValue.MYCARD_ZHYZ, "SPS0052460");
			mcBilling.put("480" + PayFinalValue.MYCARD_ZHYZ, "SPS0052484");
			mcBilling.put("500" + PayFinalValue.MYCARD_ZHYZ, "SPS0052508");
			mcBilling.put("1000" + PayFinalValue.MYCARD_ZHYZ, "SPS0052557");
			mcBilling.put("1150" + PayFinalValue.MYCARD_ZHYZ, "SPS0052580");
			mcBilling.put("2000" + PayFinalValue.MYCARD_ZHYZ, "SPS0052604");
			mcBilling.put("3000" + PayFinalValue.MYCARD_ZHYZ, "SPS0052440");

			/*
			 * 國泰世華 MyATM MyCard點數 開心金幣93點 Mycard點數 SPS0052730 369 國泰世華 MyATM
			 * MyCard點數 開心金幣122點 Mycard點數 SPS0052496 480 國泰世華 MyATM
			 * MyCard點數1000點 開心金幣313點 Mycard點數 SPS0052569 1000 國泰世華 MyATM
			 * MyCard點數100點 開心金幣22點 Mycard點數 SPS0052545 100 國泰世華 MyATM
			 * MyCard點數1150點 開心金幣324點 Mycard點數 SPS0052592 1150 國泰世華 MyATM
			 * MyCard點數150點 開心金幣33點 Mycard點數 SPS0052695 150 國泰世華 MyATM
			 * MyCard點數2000點 開心金幣688點 Mycard點數 SPS0052613 2000 國泰世華 MyATM
			 * MyCard點數3000點 開心金幣1032點 Mycard點數 SPS0052448 3000 國泰世華 MyATM
			 * MyCard點數300點 開心金幣75點 Mycard點數 SPS0052637 300 國泰世華 MyATM
			 * MyCard點數350點 開心金幣88點 Mycard點數 SPS0052661 350 國泰世華 MyATM
			 * MyCard點數400點 開心金幣100點 Mycard點數 SPS0052428 400 國泰世華 MyATM
			 * MyCard點數450點 開心金幣113點 Mycard點數 SPS0052472 450 國泰世華 MyATM
			 * MyCard點數500點 開心金幣125點 Mycard點數 SPS0052520 500 國泰世華 MyATM
			 * MyCard點數50點 開心金幣10點 Mycard點數 SPS0052404 50
			 */
			mcBilling.put("50" + PayFinalValue.MYCARD_GTSH, "SPS0052404");
			mcBilling.put("100" + PayFinalValue.MYCARD_GTSH, "SPS0052545");
			mcBilling.put("150" + PayFinalValue.MYCARD_GTSH, "SPS0052695");
			mcBilling.put("300" + PayFinalValue.MYCARD_GTSH, "SPS0052637");
			mcBilling.put("350" + PayFinalValue.MYCARD_GTSH, "SPS0052661");
			mcBilling.put("369" + PayFinalValue.MYCARD_GTSH, "SPS0052730");
			mcBilling.put("400" + PayFinalValue.MYCARD_GTSH, "SPS0052428");
			mcBilling.put("450" + PayFinalValue.MYCARD_GTSH, "SPS0052472");
			mcBilling.put("480" + PayFinalValue.MYCARD_GTSH, "SPS0052496");
			mcBilling.put("500" + PayFinalValue.MYCARD_GTSH, "SPS0052520");
			mcBilling.put("1000" + PayFinalValue.MYCARD_GTSH, "SPS0052569");
			mcBilling.put("1150" + PayFinalValue.MYCARD_GTSH, "SPS0052592");
			mcBilling.put("2000" + PayFinalValue.MYCARD_GTSH, "SPS0052613");
			mcBilling.put("3000" + PayFinalValue.MYCARD_GTSH, "SPS0052448");

			/*
			 * 台新銀行WebATM MyCard點數 開心金幣93點 Mycard點數 SPS0052725 369 台新銀行WebATM
			 * MyCard點數 開心金幣122點 Mycard點數 SPS0052491 480 台新銀行WebATM
			 * MyCard點數1000點 開心金幣313點 Mycard點數 SPS0052564 1000 台新銀行WebATM
			 * MyCard點數100點 開心金幣22點 Mycard點數 SPS0052540 100 台新銀行WebATM
			 * MyCard點數1150點 開心金幣324點 Mycard點數 SPS0052587 1150 台新銀行WebATM
			 * MyCard點數150點 開心金幣33點 Mycard點數 SPS0052690 150 台新銀行WebATM
			 * MyCard點數2000點 開心金幣688點 Mycard點數 SPS0052608 2000 台新銀行WebATM
			 * MyCard點數3000點 開心金幣1032點 Mycard點數 SPS0052443 3000 台新銀行WebATM
			 * MyCard點數300點 開心金幣75點 Mycard點數 SPS0052632 300 台新銀行WebATM
			 * MyCard點數350點 開心金幣88點 Mycard點數 SPS0052656 350 台新銀行WebATM
			 * MyCard點數400點 開心金幣100點 Mycard點數 SPS0052423 400 台新銀行WebATM
			 * MyCard點數450點 開心金幣113點 Mycard點數 SPS0052467 450 台新銀行WebATM
			 * MyCard點數500點 開心金幣125點 Mycard點數 SPS0052515 500 台新銀行WebATM
			 * MyCard點數50點 開心金幣10點 Mycard點數 SPS0052399 50
			 */

			mcBilling.put("50" + PayFinalValue.MYCARD_TXYH, "SPS0052399");
			mcBilling.put("100" + PayFinalValue.MYCARD_TXYH, "SPS0052540");
			mcBilling.put("150" + PayFinalValue.MYCARD_TXYH, "SPS0052690");
			mcBilling.put("300" + PayFinalValue.MYCARD_TXYH, "SPS0052632");
			mcBilling.put("350" + PayFinalValue.MYCARD_TXYH, "SPS0052656");
			mcBilling.put("369" + PayFinalValue.MYCARD_TXYH, "SPS0052725");
			mcBilling.put("400" + PayFinalValue.MYCARD_TXYH, "SPS0052423");
			mcBilling.put("450" + PayFinalValue.MYCARD_TXYH, "SPS0052467");
			mcBilling.put("480" + PayFinalValue.MYCARD_TXYH, "SPS0052491");
			mcBilling.put("500" + PayFinalValue.MYCARD_TXYH, "SPS0052515");
			mcBilling.put("1000" + PayFinalValue.MYCARD_TXYH, "SPS0052564");
			mcBilling.put("1150" + PayFinalValue.MYCARD_TXYH, "SPS0052587");
			mcBilling.put("2000" + PayFinalValue.MYCARD_TXYH, "SPS0052608");
			mcBilling.put("3000" + PayFinalValue.MYCARD_TXYH, "SPS0052443");

			/*
			 * 新光銀行WebATM MyCard點數 開心金幣93點 Mycard點數 SPS0052733 369 新光銀行WebATM
			 * MyCard點數 開心金幣122點 Mycard點數 SPS0052499 480 新光銀行WebATM
			 * MyCard點數1000點 開心金幣313點 Mycard點數 SPS0052572 1000 新光銀行WebATM
			 * MyCard點數100點 開心金幣22點 Mycard點數 SPS0052548 100 新光銀行WebATM
			 * MyCard點數1150點 開心金幣324點 Mycard點數 SPS0052595 1150 新光銀行WebATM
			 * MyCard點數150點 開心金幣33點 Mycard點數 SPS0052698 150 新光銀行WebATM
			 * MyCard點數2000點 開心金幣688點 Mycard點數 SPS0052616 2000 新光銀行WebATM
			 * MyCard點數3000點 開心金幣1032點 Mycard點數 SPS0052451 3000 新光銀行WebATM
			 * MyCard點數300點 開心金幣75點 Mycard點數 SPS0052640 300 新光銀行WebATM
			 * MyCard點數350點 開心金幣88點 Mycard點數 SPS0052664 350 新光銀行WebATM
			 * MyCard點數400點 開心金幣100點 Mycard點數 SPS0052431 400 新光銀行WebATM
			 * MyCard點數450點 開心金幣113點 Mycard點數 SPS0052475 450 新光銀行WebATM
			 * MyCard點數500點 開心金幣125點 Mycard點數 SPS0052523 500 新光銀行WebATM
			 * MyCard點數50點 開心金幣10點 Mycard點數 SPS0052407 50
			 */
			mcBilling.put("50" + PayFinalValue.MYCARD_XGYH, "SPS0052407");
			mcBilling.put("100" + PayFinalValue.MYCARD_XGYH, "SPS0052548");
			mcBilling.put("150" + PayFinalValue.MYCARD_XGYH, "SPS0052698");
			mcBilling.put("300" + PayFinalValue.MYCARD_XGYH, "SPS0052640");
			mcBilling.put("350" + PayFinalValue.MYCARD_XGYH, "SPS0052664");
			mcBilling.put("369" + PayFinalValue.MYCARD_XGYH, "SPS0052733");
			mcBilling.put("400" + PayFinalValue.MYCARD_XGYH, "SPS0052431");
			mcBilling.put("450" + PayFinalValue.MYCARD_XGYH, "SPS0052467");
			mcBilling.put("480" + PayFinalValue.MYCARD_XGYH, "SPS0052499");
			mcBilling.put("500" + PayFinalValue.MYCARD_XGYH, "SPS0052523");
			mcBilling.put("1000" + PayFinalValue.MYCARD_XGYH, "SPS0052572");
			mcBilling.put("1150" + PayFinalValue.MYCARD_XGYH, "SPS0052595");
			mcBilling.put("2000" + PayFinalValue.MYCARD_XGYH, "SPS0052616");
			mcBilling.put("3000" + PayFinalValue.MYCARD_XGYH, "SPS0052451");

			/*
			 * 台北富邦WebATM MyCard點數 開心金幣93點 Mycard點數 SPS0052724 369 台北富邦WebATM
			 * MyCard點數 開心金幣122點 Mycard點數 SPS0052489 480 台北富邦WebATM
			 * MyCard點數1000點 開心金幣313點 Mycard點數 SPS0052562 1000 台北富邦WebATM
			 * MyCard點數100點 開心金幣22點 Mycard點數 SPS0052538 100 台北富邦WebATM
			 * MyCard點數1150點 開心金幣324點 Mycard點數 SPS0052585 1150 台北富邦WebATM
			 * MyCard點數150點 開心金幣33點 Mycard點數 SPS0052688 150 台北富邦WebATM
			 * MyCard點數2000點 開心金幣688點 Mycard點數 SPS0052606 2000 台北富邦WebATM
			 * MyCard點數3000點 開心金幣1032點 Mycard點數 SPS0052442 3000 台北富邦WebATM
			 * MyCard點數300點 開心金幣75點 Mycard點數 SPS0052630 300 台北富邦WebATM
			 * MyCard點數350點 開心金幣88點 Mycard點數 SPS0052654 350 台北富邦WebATM
			 * MyCard點數400點 開心金幣100點 Mycard點數 SPS0052421 400 台北富邦WebATM
			 * MyCard點數450點 開心金幣113點 Mycard點數 SPS0052465 450 台北富邦WebATM
			 * MyCard點數500點 開心金幣125點 Mycard點數 SPS0052513 500 台北富邦WebATM
			 * MyCard點數50點 開心金幣10點 Mycard點數 SPS0052397 50
			 */

			mcBilling.put("50" + PayFinalValue.MYCARD_TBFB, "SPS0052397");
			mcBilling.put("100" + PayFinalValue.MYCARD_TBFB, "SPS0052538");
			mcBilling.put("150" + PayFinalValue.MYCARD_TBFB, "SPS0052688");
			mcBilling.put("300" + PayFinalValue.MYCARD_TBFB, "SPS0052630");
			mcBilling.put("350" + PayFinalValue.MYCARD_TBFB, "SPS0052654");
			mcBilling.put("369" + PayFinalValue.MYCARD_TBFB, "SPS0052724");
			mcBilling.put("400" + PayFinalValue.MYCARD_TBFB, "SPS0052421");
			mcBilling.put("450" + PayFinalValue.MYCARD_TBFB, "SPS0052465");
			mcBilling.put("480" + PayFinalValue.MYCARD_TBFB, "SPS0052489");
			mcBilling.put("500" + PayFinalValue.MYCARD_TBFB, "SPS0052513");
			mcBilling.put("1000" + PayFinalValue.MYCARD_TBFB, "SPS0052562");
			mcBilling.put("1150" + PayFinalValue.MYCARD_TBFB, "SPS0052585");
			mcBilling.put("2000" + PayFinalValue.MYCARD_TBFB, "SPS0052606");
			mcBilling.put("3000" + PayFinalValue.MYCARD_TBFB, "SPS0052442");

			/*
			 * 台灣銀行WebATM MyCard點數 開心金幣93點 Mycard點數 SPS0052726 369 台灣銀行WebATM
			 * MyCard點數 開心金幣122點 Mycard點數 SPS0052492 480 台灣銀行WebATM
			 * MyCard點數1000點 開心金幣313點 Mycard點數 SPS0052565 1000 台灣銀行WebATM
			 * MyCard點數100點 開心金幣22點 Mycard點數 SPS0052541 100 台灣銀行WebATM
			 * MyCard點數1150點 開心金幣324點 Mycard點數 SPS0052588 1150 台灣銀行WebATM
			 * MyCard點數150點 開心金幣33點 Mycard點數 SPS0052691 150 台灣銀行WebATM
			 * MyCard點數2000點 開心金幣688點 Mycard點數 SPS0052609 2000 台灣銀行WebATM
			 * MyCard點數3000點 開心金幣1032點 Mycard點數 SPS0052444 3000 台灣銀行WebATM
			 * MyCard點數300點 開心金幣75點 Mycard點數 SPS0052633 300 台灣銀行WebATM
			 * MyCard點數350點 開心金幣88點 Mycard點數 SPS0052657 350 台灣銀行WebATM
			 * MyCard點數400點 開心金幣100點 Mycard點數 SPS0052424 400 台灣銀行WebATM
			 * MyCard點數450點 開心金幣113點 Mycard點數 SPS0052468 450 台灣銀行WebATM
			 * MyCard點數500點 開心金幣125點 Mycard點數 SPS0052516 500 台灣銀行WebATM
			 * MyCard點數50點 開心金幣10點 Mycard點數 SPS0052400 50
			 */

			mcBilling.put("50" + PayFinalValue.MYCARD_TWYH, "SPS0052400");
			mcBilling.put("100" + PayFinalValue.MYCARD_TWYH, "SPS0052541");
			mcBilling.put("150" + PayFinalValue.MYCARD_TWYH, "SPS0052691");
			mcBilling.put("300" + PayFinalValue.MYCARD_TWYH, "SPS0052633");
			mcBilling.put("350" + PayFinalValue.MYCARD_TWYH, "SPS0052657");
			mcBilling.put("369" + PayFinalValue.MYCARD_TWYH, "SPS0052726");
			mcBilling.put("400" + PayFinalValue.MYCARD_TWYH, "SPS0052424");
			mcBilling.put("450" + PayFinalValue.MYCARD_TWYH, "SPS0052468");
			mcBilling.put("480" + PayFinalValue.MYCARD_TWYH, "SPS0052492");
			mcBilling.put("500" + PayFinalValue.MYCARD_TWYH, "SPS0052516");
			mcBilling.put("1000" + PayFinalValue.MYCARD_TWYH, "SPS0052565");
			mcBilling.put("1150" + PayFinalValue.MYCARD_TWYH, "SPS0052588");
			mcBilling.put("2000" + PayFinalValue.MYCARD_TWYH, "SPS0052609");
			mcBilling.put("3000" + PayFinalValue.MYCARD_TWYH, "SPS0052444");

			/*
			 * 上海銀行WebATM MyCard點數369 開心金幣93點 Mycard點數 SPS0052716 369 上海銀行WebATM
			 * MyCard點數480 開心金幣122點 Mycard點數 SPS0052480 480 上海銀行WebATM
			 * MyCard點數1000點 開心金幣313點 Mycard點數 SPS0052553 1000 上海銀行WebATM
			 * MyCard點數100點 開心金幣22點 Mycard點數 SPS0052528 100 上海銀行WebATM
			 * MyCard點數1150點 開心金幣324點 Mycard點數 SPS0052576 1150 上海銀行WebATM
			 * MyCard點數150點 開心金幣33點 Mycard點數 SPS0052678 150 上海銀行WebATM
			 * MyCard點數2000點 開心金幣688點 Mycard點數 SPS0052600 2000 上海銀行WebATM
			 * MyCard點數3000點 開心金幣1032點 Mycard點數 SPS0052436 3000 上海銀行WebATM
			 * MyCard點數300點 開心金幣75點 Mycard點數 SPS0052621 300 上海銀行WebATM
			 * MyCard點數350點 開心金幣88點 Mycard點數 SPS0052645 350 上海銀行WebATM
			 * MyCard點數400點 開心金幣100點 Mycard點數 SPS0052412 400 上海銀行WebATM
			 * MyCard點數450點 開心金幣113點 Mycard點數 SPS0052456 450 上海銀行WebATM
			 * MyCard點數500點 開心金幣125點 Mycard點數 SPS0052504 500 上海銀行WebATM
			 * MyCard點數50點 開心金幣10點 Mycard點數 SPS0052387 50
			 */

			mcBilling.put("50" + PayFinalValue.MYCARD_SHYH, "SPS0052387");
			mcBilling.put("100" + PayFinalValue.MYCARD_SHYH, "SPS0052528");
			mcBilling.put("150" + PayFinalValue.MYCARD_SHYH, "SPS0052678");
			mcBilling.put("300" + PayFinalValue.MYCARD_SHYH, "SPS0052621");
			mcBilling.put("350" + PayFinalValue.MYCARD_SHYH, "SPS0052645");
			mcBilling.put("369" + PayFinalValue.MYCARD_SHYH, "SPS0052716");
			mcBilling.put("400" + PayFinalValue.MYCARD_SHYH, "SPS0052412");
			mcBilling.put("450" + PayFinalValue.MYCARD_SHYH, "SPS0052456");
			mcBilling.put("480" + PayFinalValue.MYCARD_SHYH, "SPS0052480");
			mcBilling.put("500" + PayFinalValue.MYCARD_SHYH, "SPS0052504");
			mcBilling.put("1000" + PayFinalValue.MYCARD_SHYH, "SPS0052553");
			mcBilling.put("1150" + PayFinalValue.MYCARD_SHYH, "SPS0052576");
			mcBilling.put("2000" + PayFinalValue.MYCARD_SHYH, "SPS0052600");
			mcBilling.put("3000" + PayFinalValue.MYCARD_SHYH, "SPS0052436");

			/*
			 * 土地銀行WebATM MyCard點數 開心金幣93點 Mycard點數 SPS0052717 369 土地銀行WebATM
			 * MyCard點數 開心金幣122點 Mycard點數 SPS0052481 480 土地銀行WebATM
			 * MyCard點數1000點 開心金幣313點 Mycard點數 SPS0052554 1000 土地銀行WebATM
			 * MyCard點數100點 開心金幣22點 Mycard點數 SPS0052529 100 土地銀行WebATM
			 * MyCard點數1150點 開心金幣324點 Mycard點數 SPS0052577 1150 土地銀行WebATM
			 * MyCard點數150點 開心金幣33點 Mycard點數 SPS0052679 150 土地銀行WebATM
			 * MyCard點數2000點 開心金幣688點 Mycard點數 SPS0052601 2000 土地銀行WebATM
			 * MyCard點數3000點 開心金幣1032點 Mycard點數 SPS0052437 3000 土地銀行WebATM
			 * MyCard點數300點 開心金幣75點 Mycard點數 SPS0052622 300 土地銀行WebATM
			 * MyCard點數350點 開心金幣88點 Mycard點數 SPS0052646 350 土地銀行WebATM
			 * MyCard點數400點 開心金幣100點 Mycard點數 SPS0052413 400 土地銀行WebATM
			 * MyCard點數450點 開心金幣113點 Mycard點數 SPS0052457 450 土地銀行WebATM
			 * MyCard點數500點 開心金幣125點 Mycard點數 SPS0052505 500 土地銀行WebATM
			 * MyCard點數50點 開心金幣10點 Mycard點數 SPS0052388 50
			 */

			mcBilling.put("50" + PayFinalValue.MYCARD_TDYH, "SPS0052388");
			mcBilling.put("100" + PayFinalValue.MYCARD_TDYH, "SPS0052529");
			mcBilling.put("150" + PayFinalValue.MYCARD_TDYH, "SPS0052679");
			mcBilling.put("300" + PayFinalValue.MYCARD_TDYH, "SPS0052622");
			mcBilling.put("350" + PayFinalValue.MYCARD_TDYH, "SPS0052646");
			mcBilling.put("369" + PayFinalValue.MYCARD_TDYH, "SPS0052717");
			mcBilling.put("400" + PayFinalValue.MYCARD_TDYH, "SPS0052413");
			mcBilling.put("450" + PayFinalValue.MYCARD_TDYH, "SPS0052457");
			mcBilling.put("480" + PayFinalValue.MYCARD_TDYH, "SPS0052481");
			mcBilling.put("500" + PayFinalValue.MYCARD_TDYH, "SPS0052505");
			mcBilling.put("1000" + PayFinalValue.MYCARD_TDYH, "SPS0052554");
			mcBilling.put("1150" + PayFinalValue.MYCARD_TDYH, "SPS0052577");
			mcBilling.put("2000" + PayFinalValue.MYCARD_TDYH, "SPS0052601");
			mcBilling.put("3000" + PayFinalValue.MYCARD_TDYH, "SPS0052437");

			/*
			 * 第一銀行WebATM MyCard點數 開心金幣93點 Mycard點數 SPS0052731 369 第一銀行WebATM
			 * MyCard點數 開心金幣122點 Mycard點數 SPS0052497 480 第一銀行WebATM
			 * MyCard點數1000點 開心金幣313點 Mycard點數 SPS0052570 1000 第一銀行WebATM
			 * MyCard點數100點 開心金幣22點 Mycard點數 SPS0052546 100 第一銀行WebATM
			 * MyCard點數1150點 開心金幣324點 Mycard點數 SPS0052593 1150 第一銀行WebATM
			 * MyCard點數150點 開心金幣33點 Mycard點數 SPS0052696 150 第一銀行WebATM
			 * MyCard點數2000點 開心金幣688點 Mycard點數 SPS0052614 2000 第一銀行WebATM
			 * MyCard點數3000點 開心金幣1032點 Mycard點數 SPS0052449 3000 第一銀行WebATM
			 * MyCard點數300點 開心金幣75點 Mycard點數 SPS0052638 300 第一銀行WebATM
			 * MyCard點數350點 開心金幣88點 Mycard點數 SPS0052662 350 第一銀行WebATM
			 * MyCard點數400點 開心金幣100點 Mycard點數 SPS0052429 400 第一銀行WebATM
			 * MyCard點數450點 開心金幣113點 Mycard點數 SPS0052473 450 第一銀行WebATM
			 * MyCard點數500點 開心金幣125點 Mycard點數 SPS0052521 500 第一銀行WebATM
			 * MyCard點數50點 開心金幣10點 Mycard點數 SPS0052405 50
			 */

			mcBilling.put("50" + PayFinalValue.MYCARD_DYYH, "SPS0052405");
			mcBilling.put("100" + PayFinalValue.MYCARD_DYYH, "SPS0052546");
			mcBilling.put("150" + PayFinalValue.MYCARD_DYYH, "SPS0052696");
			mcBilling.put("300" + PayFinalValue.MYCARD_DYYH, "SPS0052638");
			mcBilling.put("350" + PayFinalValue.MYCARD_DYYH, "SPS0052662");
			mcBilling.put("369" + PayFinalValue.MYCARD_DYYH, "SPS0052731");
			mcBilling.put("400" + PayFinalValue.MYCARD_DYYH, "SPS0052429");
			mcBilling.put("450" + PayFinalValue.MYCARD_DYYH, "SPS0052473");
			mcBilling.put("480" + PayFinalValue.MYCARD_DYYH, "SPS0052497");
			mcBilling.put("500" + PayFinalValue.MYCARD_DYYH, "SPS0052521");
			mcBilling.put("1000" + PayFinalValue.MYCARD_DYYH, "SPS0052570");
			mcBilling.put("1150" + PayFinalValue.MYCARD_DYYH, "SPS0052593");
			mcBilling.put("2000" + PayFinalValue.MYCARD_DYYH, "SPS0052614");
			mcBilling.put("3000" + PayFinalValue.MYCARD_DYYH, "SPS0052449");
			/*
			 * 玉山銀行WebATM MyCard點數 開心金幣93點 Mycard點數 SPS0052727 369 玉山銀行WebATM
			 * MyCard點數 開心金幣122點 Mycard點數 SPS0052493 480 玉山銀行WebATM
			 * MyCard點數1000點 開心金幣313點 Mycard點數 SPS0052566 1000 玉山銀行WebATM
			 * MyCard點數100點 開心金幣22點 Mycard點數 SPS0052542 100 玉山銀行WebATM
			 * MyCard點數1150點 開心金幣324點 Mycard點數 SPS0052589 1150 玉山銀行WebATM
			 * MyCard點數150點 開心金幣33點 Mycard點數 SPS0052692 150 玉山銀行WebATM
			 * MyCard點數2000點 開心金幣688點 Mycard點數 SPS0052610 2000 玉山銀行WebATM
			 * MyCard點數3000點 開心金幣1032點 Mycard點數 SPS0052445 3000 玉山銀行WebATM
			 * MyCard點數300點 開心金幣75點 Mycard點數 SPS0052634 300 玉山銀行WebATM
			 * MyCard點數350點 開心金幣88點 Mycard點數 SPS0052658 350 玉山銀行WebATM
			 * MyCard點數400點 開心金幣100點 Mycard點數 SPS0052425 400 玉山銀行WebATM
			 * MyCard點數450點 開心金幣113點 Mycard點數 SPS0052469 450 玉山銀行WebATM
			 * MyCard點數500點 開心金幣125點 Mycard點數 SPS0052517 500 玉山銀行WebATM
			 * MyCard點數50點 開心金幣10點 Mycard點數 SPS0052401 50
			 */
			mcBilling.put("50" + PayFinalValue.MYCARD_YSYH, "SPS0052401");
			mcBilling.put("100" + PayFinalValue.MYCARD_YSYH, "SPS0052542");
			mcBilling.put("150" + PayFinalValue.MYCARD_YSYH, "SPS0052692");
			mcBilling.put("300" + PayFinalValue.MYCARD_YSYH, "SPS0052634");
			mcBilling.put("350" + PayFinalValue.MYCARD_YSYH, "SPS0052658");
			mcBilling.put("369" + PayFinalValue.MYCARD_YSYH, "SPS0052727");
			mcBilling.put("400" + PayFinalValue.MYCARD_YSYH, "SPS0052425");
			mcBilling.put("450" + PayFinalValue.MYCARD_YSYH, "SPS0052469");
			mcBilling.put("480" + PayFinalValue.MYCARD_YSYH, "SPS0052493");
			mcBilling.put("500" + PayFinalValue.MYCARD_YSYH, "SPS0052517");
			mcBilling.put("1000" + PayFinalValue.MYCARD_YSYH, "SPS0052566");
			mcBilling.put("1150" + PayFinalValue.MYCARD_YSYH, "SPS0052589");
			mcBilling.put("2000" + PayFinalValue.MYCARD_YSYH, "SPS0052610");
			mcBilling.put("3000" + PayFinalValue.MYCARD_YSYH, "SPS0052445");

			/*
			 * 兆豐銀行WebATM MyCard點數 開心金幣93點 Mycard點數 SPS0052728 369 兆豐銀行WebATM
			 * MyCard點數 開心金幣122點 Mycard點數 SPS0052494 480 兆豐銀行WebATM
			 * MyCard點數1000點 開心金幣313點 Mycard點數 SPS0052567 1000 兆豐銀行WebATM
			 * MyCard點數100點 開心金幣22點 Mycard點數 SPS0052543 100 兆豐銀行WebATM
			 * MyCard點數1150點 開心金幣324點 Mycard點數 SPS0052590 1150 兆豐銀行WebATM
			 * MyCard點數150點 開心金幣33點 Mycard點數 SPS0052693 150 兆豐銀行WebATM
			 * MyCard點數2000點 開心金幣688點 Mycard點數 SPS0052611 2000 兆豐銀行WebATM
			 * MyCard點數3000點 開心金幣1032點 Mycard點數 SPS0052446 3000 兆豐銀行WebATM
			 * MyCard點數300點 開心金幣75點 Mycard點數 SPS0052635 300 兆豐銀行WebATM
			 * MyCard點數350點 開心金幣88點 Mycard點數 SPS0052659 350 兆豐銀行WebATM
			 * MyCard點數400點 開心金幣100點 Mycard點數 SPS0052426 400 兆豐銀行WebATM
			 * MyCard點數450點 開心金幣113點 Mycard點數 SPS0052470 450 兆豐銀行WebATM
			 * MyCard點數500點 開心金幣125點 Mycard點數 SPS0052518 500 兆豐銀行WebATM
			 * MyCard點數50點 開心金幣10點 Mycard點數 SPS0052402 50
			 */
			mcBilling.put("50" + PayFinalValue.MYCARD_ZFYH, "SPS0052402");
			mcBilling.put("100" + PayFinalValue.MYCARD_ZFYH, "SPS0052543");
			mcBilling.put("150" + PayFinalValue.MYCARD_ZFYH, "SPS0052693");
			mcBilling.put("300" + PayFinalValue.MYCARD_ZFYH, "SPS0052635");
			mcBilling.put("350" + PayFinalValue.MYCARD_ZFYH, "SPS0052659");
			mcBilling.put("369" + PayFinalValue.MYCARD_ZFYH, "SPS0052728");
			mcBilling.put("400" + PayFinalValue.MYCARD_ZFYH, "SPS0052426");
			mcBilling.put("450" + PayFinalValue.MYCARD_ZFYH, "SPS0052470");
			mcBilling.put("480" + PayFinalValue.MYCARD_ZFYH, "SPS0052494");
			mcBilling.put("500" + PayFinalValue.MYCARD_ZFYH, "SPS0052518");
			mcBilling.put("1000" + PayFinalValue.MYCARD_ZFYH, "SPS0052567");
			mcBilling.put("1150" + PayFinalValue.MYCARD_ZFYH, "SPS0052590");
			mcBilling.put("2000" + PayFinalValue.MYCARD_ZFYH, "SPS0052611");
			mcBilling.put("3000" + PayFinalValue.MYCARD_ZFYH, "SPS0052446");

			/*
			 * 彰化銀行WebATM MyCard點數 開心金幣93點 Mycard點數 SPS0052734 369 彰化銀行WebATM
			 * MyCard點數 開心金幣122點 Mycard點數 SPS0052500 480 彰化銀行WebATM
			 * MyCard點數1000點 開心金幣313點 Mycard點數 SPS0052573 1000 彰化銀行WebATM
			 * MyCard點數100點 開心金幣22點 Mycard點數 SPS0052549 100 彰化銀行WebATM
			 * MyCard點數1150點 開心金幣324點 Mycard點數 SPS0052596 1150 彰化銀行WebATM
			 * MyCard點數150點 開心金幣33點 Mycard點數 SPS0052699 150 彰化銀行WebATM
			 * MyCard點數2000點 開心金幣688點 Mycard點數 SPS0052617 2000 彰化銀行WebATM
			 * MyCard點數3000點 開心金幣1032點 Mycard點數 SPS0052452 3000 彰化銀行WebATM
			 * MyCard點數300點 開心金幣75點 Mycard點數 SPS0052641 300 彰化銀行WebATM
			 * MyCard點數350點 開心金幣88點 Mycard點數 SPS0052665 350 彰化銀行WebATM
			 * MyCard點數400點 開心金幣100點 Mycard點數 SPS0052432 400 彰化銀行WebATM
			 * MyCard點數450點 開心金幣113點 Mycard點數 SPS0052476 450 彰化銀行WebATM
			 * MyCard點數500點 開心金幣125點 Mycard點數 SPS0052524 500 彰化銀行WebATM
			 * MyCard點數50點 開心金幣10點 Mycard點數 SPS0052408 50
			 */

			mcBilling.put("50" + PayFinalValue.MYCARD_ZHYH, "SPS0052408");
			mcBilling.put("100" + PayFinalValue.MYCARD_ZHYH, "SPS0052549");
			mcBilling.put("150" + PayFinalValue.MYCARD_ZHYH, "SPS0052699");
			mcBilling.put("300" + PayFinalValue.MYCARD_ZHYH, "SPS0052641");
			mcBilling.put("350" + PayFinalValue.MYCARD_ZHYH, "SPS0052665");
			mcBilling.put("369" + PayFinalValue.MYCARD_ZHYH, "SPS0052734");
			mcBilling.put("400" + PayFinalValue.MYCARD_ZHYH, "SPS0052432");
			mcBilling.put("450" + PayFinalValue.MYCARD_ZHYH, "SPS0052476");
			mcBilling.put("480" + PayFinalValue.MYCARD_ZHYH, "SPS0052500");
			mcBilling.put("500" + PayFinalValue.MYCARD_ZHYH, "SPS0052524");
			mcBilling.put("1000" + PayFinalValue.MYCARD_ZHYH, "SPS0052573");
			mcBilling.put("1150" + PayFinalValue.MYCARD_ZHYH, "SPS0052596");
			mcBilling.put("2000" + PayFinalValue.MYCARD_ZHYH, "SPS0052617");
			mcBilling.put("3000" + PayFinalValue.MYCARD_ZHYH, "SPS0052452");

			/*
			 * 支付寶 MyCard點數 開心金幣93點 Mycard點數 SPS0052723 369 支付寶 MyCard點數
			 * 開心金幣122點 Mycard點數 SPS0052488 480 支付寶 MyCard點數1000 點 開心金幣313點
			 * Mycard點數 SPS0052561 1000 支付寶 MyCard點數100點 開心金幣22點 Mycard點數
			 * SPS0052537 100 支付寶 MyCard點數1150點 開心金幣324點 Mycard點數 SPS0052584
			 * 1150 支付寶 MyCard點數150點 開心金幣33點 Mycard點數 SPS0052687 150 支付寶
			 * MyCard點數2000點 開心金幣688點 Mycard點數 SPS0052605 2000 支付寶 MyCard點數3000點
			 * 開心金幣1032點 Mycard點數 SPS0052441 3000 支付寶 MyCard點數300點 開心金幣75點
			 * Mycard點數 SPS0052629 300 支付寶 MyCard點數350點 開心金幣88點 Mycard點數
			 * SPS0052653 350 支付寶 MyCard點數400點 開心金幣100點 Mycard點數 SPS0052420 400
			 * 支付寶 MyCard點數450點 開心金幣113點 Mycard點數 SPS0052464 450 支付寶
			 * MyCard點數500點 開心金幣125點 Mycard點數 SPS0052512 500 支付寶 MyCard點數50點
			 * 開心金幣10點 Mycard點數 SPS0052396 50
			 */

			mcBilling.put("50" + PayFinalValue.MYCARD_ZFB, "SPS0052396");
			mcBilling.put("100" + PayFinalValue.MYCARD_ZFB, "SPS0052537");
			mcBilling.put("150" + PayFinalValue.MYCARD_ZFB, "SPS0052687");
			mcBilling.put("300" + PayFinalValue.MYCARD_ZFB, "SPS0052629");
			mcBilling.put("350" + PayFinalValue.MYCARD_ZFB, "SPS0052653");
			mcBilling.put("369" + PayFinalValue.MYCARD_ZFB, "SPS0052723");
			mcBilling.put("400" + PayFinalValue.MYCARD_ZFB, "SPS0052420");
			mcBilling.put("450" + PayFinalValue.MYCARD_ZFB, "SPS0052464");
			mcBilling.put("480" + PayFinalValue.MYCARD_ZFB, "SPS0052488");
			mcBilling.put("500" + PayFinalValue.MYCARD_ZFB, "SPS0052512");
			mcBilling.put("1000" + PayFinalValue.MYCARD_ZFB, "SPS0052561");
			mcBilling.put("1150" + PayFinalValue.MYCARD_ZFB, "SPS0052584");
			mcBilling.put("2000" + PayFinalValue.MYCARD_ZFB, "SPS0052605");
			mcBilling.put("3000" + PayFinalValue.MYCARD_ZFB, "SPS0052441");
		}
		return mcBilling;
	}

	// 判断储serviceId是否满足条件
	public static String getServiceId(String skey)
	{
		return getMcBilling().containsKey(skey) ? getMcBilling().get(skey) : "";
	}

	public static void main(String[] args)
	{
		String sid = "SPS0052723";
		String orderID = "dfa11111dsf";
		String pmoney = "369";
		// System.out.println(volidateValue("point"));
		// getMcBillingAuthCode(sid ,orderID,pmoney);
		getMcPointAuthCode("dfddfdffas22", "369");
		// getMcPointAuthCode("dfdfas22","50");
		// String src =
		// "<?xml version=\"1.0\" encoding=\"utf-8\"?><HeartBeatResult xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns=\"http://tempuri.org/\"><ResCode>600</ResCode><MerchantID>201011037357</MerchantID><HB>00000000-0000-0000-0000-000000000000</HB></HeartBeatResult>";
		/*
		 * String tag = "HB" ; String src = getMolHeartBeat();
		 * System.out.println(getValueByTag(src,tag));
		 */
		/*
		 * String uid = "aaaa" ; String ac =
		 * "ZeTd0r3yTDMS+eTImeRJ1kJX0F5rLgy4YiR16KZkMCtFnqFjDwCFuw==";
		 * confirmBillingPayment(ac,uid);
		 */
		/*
		 * String ac = "" ; // String s = PayUtils.confirmBillingTrade(ac);
		 * String s =
		 * "<string xmlns=\"http://schemas.microsoft.com/2003/10/Serialization/\">1|無此遊戲廠商登入IP|3</string>"
		 * ; if(!s.equals("")){ String queryNum =
		 * s.substring(s.indexOf(">")+1,s.indexOf("|")).trim() ; String
		 * resultTrade =
		 * s.substring(s.lastIndexOf("|")+1,s.lastIndexOf("<")).trim();
		 * System.out.println("MyCard==confirm"+queryNum);
		 * System.out.println("MyCard==confirm"+resultTrade);
		 * if(queryNum.equals("1")&&resultTrade.equals("3")){
		 * System.out.println("ok"); } }
		 */
		// System.out.println(getGoldByMoney("1"));
		/*
		 * String pmethod = "paypal"; String pmoney = "29.99"; String orderID =
		 * "p201010121744426234"; // boolean f = checkPayMoney("100"); // String
		 * strCPOrderID = (new SimpleDateFormat("yyyyMMddHHmmss")).format(new
		 * Date());
		 * System.out.println((!pmethod.equals("p")&&!pmethod.equals("a"
		 * )&&!pmethod
		 * .equals("paypal"))||(!PayUtils.checkPayMoney(pmoney)&&PayUtils
		 * .checkAmt(pmoney))||orderID.equals(""));
		 */
	}
}
