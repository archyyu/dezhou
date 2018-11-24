package com.yl.thread;

import com.yl.Global.ConstList;
import com.yl.Global.UserInfoMemoryCache;
import com.yl.Global.UserModule;
import com.yl.backlet.BackletKit;
import com.yl.container.ActionscriptObject;
import com.yl.container.SFSObjectSerializer;
import com.yl.container.TimerMessageQuene;
import com.yl.container.User;
import com.yl.httpLogic.ConnectInstance;

import com.yl.ndb.DBServer;
import com.yl.room.base.IRoom;

import com.yl.util.Utils;
import com.yl.util.XmlReaderUtils;
import com.yl.vo.Prop;
import com.yl.vo.UserInfo;

import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStreamReader;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

public class AsynchronousModule implements Runnable
{

	String password = "";
	String email = "no@email.com";

	String msg = "";
	IRoom r = null;
	User u = null;
	/**
	 * 私聊信息的接收方ID
	 */
	String OtherUid = "-1";

	String Cmd = "none";
	String MessageType = "-1";
	String channelId = "";

	String userId = "";
	String consumeid = "";
	String djidStr = "";

	HashMap<String, String> PublicTestMap = new HashMap<String, String>();
	/**
	 * type 线程类型
	 * 
	 */

	int djid = 0;
	/**
	 * 发送信息的范围。 0: 向每个私人发送私聊信息 1：向房间的每个人发送大厅信息。 2: 向世界发送公共信息，包括系统管理员和使用超级大喇叭的玩家。
	 */
	byte scopeType = 0;
	String handleName = "";
	ActionscriptObject parms = new ActionscriptObject();

	public Thread t;
	private int type = 0;

//	public static DbManager db;
//	static
//	{
//
//		db = ConnectionPoolFactory.getConnectionPool("DbManager");
//		db.setConnect(ConstList.DbUrl, ConstList.DbUsrNm, ConstList.DbPasswd);
//	}


	// 发送房间消息
	public AsynchronousModule(String msg, IRoom r, User u, String Cmd,
			String MessageType, String handleName)
	{
		this.type = 1;
		this.msg = msg;
		this.r = r;
		this.u = u;
		this.Cmd = Cmd;
		this.scopeType = 1;
		this.handleName = handleName;
		this.MessageType = MessageType;
		t = new Thread(this);
	}

	// 发送世界消息
	public AsynchronousModule(String msg, User u, String Cmd,
			String MessageType, String handleName)
	{
		this.type = 1;
		this.msg = msg;
		this.u = u;
		this.Cmd = Cmd;
		this.scopeType = 2;
		this.MessageType = MessageType;
		this.handleName = handleName;
		t = new Thread(this);
	}

	// 发送世界消息
	public AsynchronousModule(User u, String Cmd, ActionscriptObject parms,
			String handleName, String channelId, String userId,
			String consumeid, String djid)
	{
		this.type = 6;
		this.u = u;
		this.Cmd = Cmd;
		this.scopeType = -1;
		this.parms = parms;
		this.handleName = handleName;
		t = new Thread(this);
		this.channelId = channelId;
		this.userId = userId;
		this.consumeid = consumeid;
		this.djidStr = djid;

	}

	public AsynchronousModule(String password, String email, String handleName)
	{
		this.password = password;
		this.email = email;
		this.type = 0;
		this.handleName = handleName;
		t = new Thread(this);
	}

	public AsynchronousModule(int type, int djid, String handleName)
	{
		this.type = type;
		this.djid = djid;
		this.handleName = handleName;
	}

	/**
	 * 公测号的处理线程
	 */
	public AsynchronousModule(int type, String Cmd,
			HashMap<String, String> parms, String handleName)
	{
		this.type = 7;
		this.PublicTestMap = parms;
		this.handleName = handleName;
		t = new Thread(this);
	}

	public void run()
	{
		if (type == 0)
		{
			System.out.println("邮件发送中......");
			updateUserPassword(password, email);
		}
		else if (type == 7)// 公测
		{
			System.out.println("正在处理游戏平台公测");

//			int dealNum = Integer.parseInt(PublicTestMap.get("dnum"));
//			int dealtype = Integer.parseInt(PublicTestMap.get("dtype"));
//			String beginTime = PublicTestMap.get("ctime") + " 00:00:00";
//			String endTime = PublicTestMap.get("ctime") + " 23:59:59";
//			int daojuId = Integer.parseInt(PublicTestMap.get("djid"));
//			int money = Integer.parseInt(PublicTestMap.get("money"));
//			String ifRandDj = PublicTestMap.get("ifRandDj");
//			String ifRandmoney = PublicTestMap.get("ifRandmoney");
//
//			int userGrade = 1;
//			try
//			{
//				userGrade = Integer.parseInt(PublicTestMap.get("userGrade"));
//			}
//			catch (Exception ex)
//			{
//				userGrade = 1;
//			}
//
//			HashMap<Integer, HashMap<String, String>> userList;
//			if (dealtype == 0)
//				userList = PlayerManager.getPublicTestUserList(dealNum,
//						beginTime, endTime, 1, userGrade);
//			else
//				userList = PlayerManager.getPublicTestUserList(dealNum,
//						beginTime, endTime, 0, userGrade);
//
//			switch (dealtype)
//			{
//			case 0: // 0：充值；1：查询余额；2：购买道具；3：查询消费记录。
//
//				for (Integer key : userList.keySet())
//				{
//					HashMap<String, String> userInfo = userList.get(key);
//					User user = UserModule.getInstance().getUserByUserId(Integer.parseInt(userInfo.get("uid")));
//
//					if (ifRandmoney.equals("yes") || money <= 0 || money > 99)// 随机充值
//					{
//						Random random = new Random();
//						int chargeMoneyLimit = 30 - Math.abs(random.nextInt()) % 25;
//						money = 30 - chargeMoneyLimit;
//					}
//					Random random = new Random();
//
//					if (money > 15 && Math.abs(random.nextInt()) % 30 > 10)
//						money = 10 - (Math.abs(random.nextInt()) % 8);
//
//					int chargeNum = money * 100;
//
//					if (user == null)
//					{
//						HashMap<String, String> userinfoList = PlayerManager
//								.AutoRegister("httphander",
//										userInfo.get("muid"),
//										userInfo.get("sessionkey"));
//						PlayerManager.UserLogin("", "", false,
//								 userInfo.get("muid"),
//								userInfo.get("sessionkey"), 1, false);
//						user = UserModule.getInstance().getUserByUserId(Integer.parseInt(userInfo.get("uid")));
//					}
//					if (user != null)
//					{
//						RequestHttp rh = new RequestHttp(
//								"http://gmp.i139.cn/bizcontrol/ChargeUp", user,
//								"httphander");
//						rh.chargeNumCode = ChargeUtil.getChargeCode(chargeNum);
//						rh.key = userInfo.get("sessionkey");
//						rh.mobile = "";
//						rh.userId = userInfo.get("muid");
//						Thread rhThread = new Thread(rh);
//						rhThread.start();
//					}
//
//				}
//				break;
//			case 1:
//				break;
//			case 2:
//
//				for (Integer key : userList.keySet())
//				{
//					HashMap<String, String> userInfo = userList.get(key);
//					User user = UserModule.getInstance().getUserByUserId(Integer.parseInt(userInfo.get("uid")));
//
//					if (ifRandDj.equals("yes") || daojuId <= 1 || daojuId > 130)// 随机买道具
//					{
//						Random random = new Random();
//						daojuId = 131 - Math.abs(random.nextInt()) % 130;
//					}
//
//					if (user == null)
//					{
//						
//						HashMap<String, String> userinfoList = PlayerManager
//								.AutoRegister("httphander",
//										userInfo.get("muid"),
//										userInfo.get("sessionkey"));
//						
//						PlayerManager.UserLogin("", "", false,
//								userInfo.get("muid"),
//								userInfo.get("sessionkey"), 1, false);
//						
//						user = UserModule.getInstance().getUserByUserId(Integer.parseInt(userInfo.get("uid")));
//					}
//					
//					if (user != null)
//					{
//						if (daojuId > 0 && daojuId <= 131
//								&& UserModule.getInstance().isValidDaoju(daojuId))
//						{
//							int needGold = UserModule.getInstance().GetConsumeGold(daojuId);
//							UserInfo ui = UserInfoMemoryCache.getUserInfo(userInfo.get("uid").toString());
//							
//							int goldbefore = ui.getGold();
//							
//							RequestHttp rh = new RequestHttp(
//									"http://gmp.i139.cn/bizcontrol/BuyGameTool",
//									user, "httphander");
//							rh.consumeid = UserModule.getInstance().GetConsumeId(daojuId);
//							rh.key = userInfo.get("sessionkey");
//							rh.mobile = "";
//							rh.userId = userInfo.get("muid");
//							rh.djid = daojuId + "";
//							
//							Thread rhThread = new Thread(rh);
//							rhThread.start();
//						}
//					}
//
//				}
//
//				break;
//			case 3:
//				break;
//			default:
//				break;
//			}
//
//			dealPublicMessage(msg, u, Cmd, MessageType,
//					scopeType);
		}

		else if (type == 1)// 敏感词过滤
		{
			dealPublicMessage(msg, u, Cmd, MessageType,
					scopeType);
		}
		else if (type == 4)
		{
			useExChangeCard();
		}
		else if (type == 5)
		{
			useFacebag();
		}
		else if (type == 6)
		{
			if (Cmd.equals("chargeUp")) // 充值
			{
				String balanceStr = (String) (String) parms.get("balance");
				if (balanceStr == null || balanceStr.length() == 0)
					balanceStr = "-1";
				int balanceTotal = (int) (Double.parseDouble(balanceStr));
				useChargeUp(balanceTotal, u);
			}
			else if (Cmd.equals("BuyGameTool"))// 购买道具
			{
				String balanceStr = (String) parms.get("balance");
				if (balanceStr == null || balanceStr.length() == 0)
					balanceStr = "-1";
				int balanceTotal = (int) (Double.parseDouble(balanceStr));

				String pointStr = (String) parms.get("point");
				if (pointStr == null || pointStr.length() == 0)
					pointStr = "-1";
				int point = (int) (Double.parseDouble(pointStr));

				String djidStr = (String) parms.get("djid");
				if (djidStr == null || djidStr.length() == 0)
					djidStr = "-1";
				djid = (int) (Double.parseDouble(djidStr));
				useBuyGameTool(balanceTotal, point, djid, u);
			}
			else if (Cmd.equals("QueryBalance"))// 查询余额
			{
				GetUserQueryBalance(u);
			}
			else if (Cmd.equals("QueryCharge"))// 查询充值明细
			{
				GetUserQueryCharge(u);
			}
			else if (Cmd.equals("QueryConsume"))// 查询充值明细
			{
				GetUserQueryConsume(u);
			}

		}
	}

	/**
	 * <response> <msgType>QueryConsumeRecordResp</msgType>
	 * <queryType>1…13</queryType> <hRet>1</hRet> <status>1102</status>
	 * <userIdType>1</userIdType> <userLabel>13800138000</userLabel>
	 * <recordList> <recordSchema>
	 * <!--消费类型：A-充值B-套餐O-开户C1-客户端单机C2-客户端网游道具C3-WAP网游道具C4-WAP单机-->
	 * <recordType>消费类型</recordType> <cpId>合作方ID</cpId> <cpName>合作方名称</cpName>
	 * <channelId>渠道ID</channelId> <channelName>渠道名称</channelName>
	 * <cpServiceId>业务ID或充值代码ID</cpServiceId>
	 * <cpServiceName>业务名称或充值名称</cpServiceName> <packageId>套餐ID</packageId>
	 * <packageName>套餐名称</packageName> <toolsId>道具ID</toolsId>
	 * <toolsName>道具名称</toolsName> <date>消费时间(格式20070618 09:58)</date>
	 * <payType>计费方式(1-点数 2-话费)</payType> <payValue>支付点数或话费分</payValue>
	 * </recordSchema> </recordList> </response>
	 */
	public void GetUserQueryConsume(User user)
	{
		long curtime = System.currentTimeMillis();
		ActionscriptObject response = new ActionscriptObject();

		response.put("uid", user.getUid());
		response.put("ts", curtime + "");
		response.put("_cmd", "QueryConsume");

		if (((String) parms.get("status")).equals("1102"))
			response.put("st", "ok");
		else
			response.put("st", "fail");
		response.put("code", (String) parms.get("status"));

		response.put("detail", (ActionscriptObject) parms.get("detail"));
		ConnectInstance NextConnect = new ConnectInstance();
		NextConnect.roomKey = "admin";
		NextConnect.aObj = response;
		NextConnect.CreateTmeStampe = curtime;
		NextConnect.priority = 9;
		NextConnect.expireTime = ConstList.level_9_ExpireTime;
		NextConnect.status = 0;
		NextConnect._cmd = "QueryConsume";

		++TimerMessageQuene.totalLinkid;
		NextConnect.linkid = TimerMessageQuene.totalLinkid;
		echoResponseString(response);
		try
		{
			user.sendBbsMessage(NextConnect);
//			PlayerManager.updateMessageQuene2Xml(user, "bbs");
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	public void echoResponseString(ActionscriptObject response)
	{
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");

		byte[] xmlByteA = SFSObjectSerializer.obj2xml(response, 0, "", sb);
		System.out.println(new String(xmlByteA));
	}

	/**
	 * <?xml version="1.0" encoding="UTF-8"?> <response>
	 * <msgType>QueryChargeResp</msgType> <hRet>0</hRet> <status>1200</status>
	 * <userIdType>1</userIdType> <userLabel>13888888888</userLabel>
	 * <startSequence>1</startSequence> <recordCount>20</recordCount>
	 * <chargeList> <consumerSchema> <date>String</date>
	 * <consumerType>A</consumerType> <point>String</point> </consumerSchema>
	 * <consumerSchema> <date>String</date> <consumerType>A</consumerType>
	 * <point>String</point> </consumerSchema> <consumerSchema>
	 * <date>String</date> <consumerType>A</consumerType> <point>String</point>
	 * </consumerSchema> </chargeList> </response>
	 **/

	public void GetUserQueryCharge(User user)
	{
		long curtime = System.currentTimeMillis();
		ActionscriptObject response = new ActionscriptObject();

		response.put("uid", user.getUid());
		response.put("ts", curtime + "");
		response.put("_cmd", "QueryCharge");

		if (((String) parms.get("status")).equals("1104"))
		{
			response.put("st", "ok");
		}
		else
		{
			response.put("st", "fail");
		}
		
		response.put("code", (String) parms.get("status"));
		response.put("detail", (ActionscriptObject) parms.get("detail"));
		ConnectInstance NextConnect = new ConnectInstance();
		NextConnect.roomKey = "admin";
		NextConnect.aObj = response;
		NextConnect.CreateTmeStampe = curtime;
		NextConnect.priority = 9;
		NextConnect.expireTime = ConstList.level_9_ExpireTime;
		NextConnect.status = 0;
		NextConnect._cmd = "QueryCharge";
		echoResponseString(response);
		++TimerMessageQuene.totalLinkid;
		NextConnect.linkid = TimerMessageQuene.totalLinkid;

		try
		{
			user.sendBbsMessage(NextConnect);
//			PlayerManager.updateMessageQuene2Xml(user, "bbs");
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	/**
	 * <?xml version="1.0" encoding="UTF-8"?> <response> <status>1105</status>
	 * <msgType>QueryBalanceResp</msgType> <hRet>0</hRet>
	 * <userIdType>1</userIdType> <userLabel>13667202367</userLabel>
	 * <point>59900.0</point> </response>
	 **/

	public void GetUserQueryBalance(User user)
	{
		long curtime = System.currentTimeMillis();
		ActionscriptObject response = new ActionscriptObject();

		response.put("uid", user.getUid());
		response.put("ts", curtime + "");
		response.put("_cmd", "QueryBalance");

		if (((String) parms.get("status")).equals("1105"))
		{
			response.put("st", "ok");
			response.put("gold", (String) parms.get("point"));
			response.put("code", (String) parms.get("status"));
		}
		else
		{
			response.put("st", "fail");
			response.put("gold", "-1");
			response.put("code", (String) parms.get("status"));
		}
		ConnectInstance NextConnect = new ConnectInstance();
		NextConnect.roomKey = "admin";
		NextConnect.aObj = response;
		NextConnect.CreateTmeStampe = curtime;
		NextConnect.priority = 9;
		NextConnect.expireTime = ConstList.level_9_ExpireTime;
		NextConnect.status = 0;
		NextConnect._cmd = "QueryBalance";
		echoResponseString(response);
		++TimerMessageQuene.totalLinkid;
		NextConnect.linkid = TimerMessageQuene.totalLinkid;

		try
		{
			user.sendBbsMessage(NextConnect);
//			PlayerManager.updateMessageQuene2Xml(user, "bbs");
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	/**
	 * <?xml version="1.0" encoding="UTF-8"?> <response> <status>1800</status>
	 * <msgType>BuyGameToolResp</msgType> <balance>35397.0</balance>
	 * <hRet>0</hRet> <point>2000</point> </response>
	 */
	
	private static SqlSessionFactory sqlMapper = DBServer.getInstance().getSqlMapper();
	
	public void useBuyGameTool(int balanceTotal, int point, int djid, User user)
	{

		String uid = user.getUid();
		UserInfo ui = UserInfoMemoryCache.getUserInfo(uid);
		int goldbefore = ui.getGold();
		int gold = balanceTotal;
		long curtime = System.currentTimeMillis();
		
		
		if (((String) parms.get("status")) != null
				&& (((String) parms.get("status")).trim().equals("1800")
						|| ((String) parms.get("status")).trim().equals("1802") || ((String) parms
							.get("status")).trim().equals("1310")))
		{
			System.out.println("进入道具购买成功处理过程channelId=" + channelId);
			if (channelId.equals("-1"))
			{
				ui.setGold(gold);
				ui.setLastUpdateTime(curtime);

//				String sql = (String) SqlQueryList.SqlList.get("BuyGameTool");
//				sql = sql.replace("$gold", "" + gold);
//				sql = sql.replace("$uid", uid + "");
//				ConstList.config.logger.warn("chargeUp sql=" + sql);
//				db.executeCommand(sql);
				
				SqlSession session = sqlMapper.openSession();
				
				int updateUser = session.update("ndb.BuyGameTool", ui);
				
				session.commit();
				session.close();

				ActionscriptObject response = new ActionscriptObject();
				response.put("bg", goldbefore + "");
				response.put("ag", gold + "");
				response.put("uid", uid);
				response.put("ts", curtime + "");
				response.put("st", "ok");
				response.put("code", (String) parms.get("status"));
				response.put("_cmd", "BuyGameTool");

				ConnectInstance NextConnect = new ConnectInstance();
				NextConnect.roomKey = "admin";
				NextConnect.aObj = response;
				NextConnect.CreateTmeStampe = curtime;
				NextConnect.priority = 9;
				NextConnect.expireTime = ConstList.level_9_ExpireTime;
				NextConnect.status = 0;
				NextConnect._cmd = "BuyGameTool";
				String cntString = sendExtraDjAndMoney(djid, user);

				if (cntString == null || cntString.equals(""))
					cntString = "-1";
				response.put("cnt", cntString);
				++TimerMessageQuene.totalLinkid;
				NextConnect.linkid = TimerMessageQuene.totalLinkid;

				ActionscriptObject requestObj = new ActionscriptObject();
				requestObj.put("uid", user.getUid());
				requestObj.put("fuid", user.getUid());
				requestObj.putNumber("dt", djid);
				requestObj.putNumber("num", 1);
				//TODO
				ActionscriptObject buyDjResponse = null;
				if (buyDjResponse != null)
				{
					StringBuffer sb = new StringBuffer();
					sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
					System.out.println(new String(SFSObjectSerializer.obj2xml(
							buyDjResponse, 0, "", sb)));
				}
				echoResponseString(response);

				try
				{
					user.sendBbsMessage(NextConnect);
//					PlayerManager.updateMessageQuene2Xml(user, "bbs");
				}
				catch (Exception ex)
				{
					ex.printStackTrace();
				}
			}
			else
			{
				HashMap<String, String> parmsMap = new HashMap<String, String>();
				parmsMap.put("channelId", channelId);
				parmsMap.put("userId", userId);
				parmsMap.put("code", consumeid);
				parmsMap.put("djid", djidStr);
				parmsMap.put("status", (String) parms.get("status"));
				parmsMap.put("point", point + "");
				parmsMap.put("hRet", (String) parms.get("hRet") + "");

				sendPost("POST", parmsMap);
			}
		}
		else
		{
			System.out.println("进入道具购买失败处理过程channelId=" + channelId);
			if (channelId.equals("-1"))
			{
				ActionscriptObject response = new ActionscriptObject();
				response.put("bg", goldbefore + "");
				response.put("uid", uid);
				response.put("ts", curtime + "");
				response.put("st", "fail");
				response.put("code", (String) parms.get("status"));
				if (gold == goldbefore)
					response.put("cnt", "很抱歉，道具购买失败！原因：该充值代码不在联调范围或消费代码无效。");
				else
					response.put("cnt", "很抱歉，道具购买失败！");

				response.put("_cmd", "BuyGameTool");
				ConnectInstance NextConnect = new ConnectInstance();
				NextConnect.roomKey = "admin";
				NextConnect.aObj = response;
				NextConnect.CreateTmeStampe = curtime;
				NextConnect.priority = 9;
				NextConnect.expireTime = ConstList.level_9_ExpireTime;
				NextConnect.status = 0;
				NextConnect._cmd = "BuyGameTool";

				++TimerMessageQuene.totalLinkid;
				echoResponseString(response);
				NextConnect.linkid = TimerMessageQuene.totalLinkid;

				try
				{
					user.sendBbsMessage(NextConnect);
				}
				catch (Exception ex)
				{
					ex.printStackTrace();
				}
			}
			else
			{
				HashMap<String, String> parmsMap = new HashMap<String, String>();
				parmsMap.put("channelId", channelId);
				parmsMap.put("userId", userId);
				parmsMap.put("code", consumeid);
				parmsMap.put("djid", djidStr);
				parmsMap.put("status", (String) parms.get("status"));
				parmsMap.put("point", point + "");
				parmsMap.put("hRet", (String) parms.get("hRet") + "");
				
				sendPost("POST", parmsMap);
			}

		}

	}

	public String sendPost(String MethodType, HashMap<String, String> parms)
	{
		Map<String, List<String>> hearList = new HashMap<String, List<String>>();
		String postString = "";
		String RespnseBody = "";
		HttpURLConnection conn = null;
		OutputStreamWriter osw = null;
		char c = '\n';
		try
		{
			if (parms != null && parms.size() > 0)
			{
				for (String key : parms.keySet())
				{
					if (!key.trim().equals("parm"))
						postString += key + "=" + parms.get(key) + "&";
				}
				postString += "isrobot=1";
			}
			ConstList.config.logger.warn("postString=" + postString);

			String tip = "postString=" + postString;
			ConstList.config.logger.warn(tip);

			String requestUrl = "http://218.246.34.81:8010/mgame360/jpcomics/resreq.php?"
					+ postString;
			URL url = new URL(requestUrl);
			ConstList.config.logger.error("requestUrl=" + requestUrl);

			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod(MethodType);
			conn.connect();
			osw = new OutputStreamWriter(conn.getOutputStream());

			osw.write(postString);
			osw.write(c);
			osw.flush();
			osw.close();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));

			String lines;
			while ((lines = reader.readLine()) != null)
			{
				RespnseBody += lines + "\r\n";
			}
			reader.close();

			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK)
			{
				System.out.println("connect failed!");
				tip = "postString=" + postString;
				ConstList.config.logger.error(tip);

			}
			else
			{
				hearList = conn.getHeaderFields();
			}
		}
		catch (IOException e)
		{
			String tip = "postString=" + postString;
			ConstList.config.logger.error(tip);
			e.printStackTrace();
		}
		finally
		{
			if (osw != null)
				try
				{
					osw.close();
				}
				catch (IOException e1)
				{
					String tip = "postString=" + postString;
					ConstList.config.logger.error(tip);

					e1.printStackTrace(System.out);
				}
			if (conn != null)
				conn.disconnect();
		}
		return RespnseBody;
	}

	public String sendExtraDjAndMoney(int djid, User user)
	{
		String rtnString = "";
		String uid = user.getUid();
		UserInfo ui = UserInfoMemoryCache.getUserInfo(uid);
		int sendList[] = new int[0];
		HashMap<String, Prop> propMap = UserModule.getInstance().getPropMap();
		int premoney = ui.getAMoney();

		switch (djid)
		{
		/**
		 * 一次性获得2000筹码，VIP标志（有效期使用日起1天）， 赠送踢人卡1张，低保卡1张，VIP表情（有效期使用日起1天），
		 * 会员专场通行证（有效期使用日起1天）
		 */
		case 108:
			ui.setAmoney(ui.getAMoney() + 2000);
			sendList = new int[]
			{ 57, 117, 118 };
			rtnString = "一次性获得2000筹码，VIP标志（有效期使用日起1天）， 赠送踢人卡1张，低保卡1张，1天VIP表情";

			break;
		/**
		 * 
		 * 一次性获得5000筹码，VIP标志（有效期使用日起7天）， 赠送踢人卡2张，低保卡2张，VIP表情（有效期使用日起7天），
		 * 会员专场通行证（有效期使用日起7天）
		 */
		case 109:
			ui.setAmoney(ui.getAMoney() + 5000);
			sendList = new int[]
			{ 57, 57, 117, 117, 119, 113 };
			rtnString = "一次性获得5000筹码，VIP标志（有效期使用日起7天）， 赠送踢人卡2张，低保卡2张，7天VIP表情";

			break;
		/**
		 * 
		 * 一次性获得100000筹码，VIP标志（有效期使用日起30天）， 赠送踢人卡3张，低保卡3张，黑桃钻，VIP表情（有效期使用日起30天），
		 * 会员专场通行证（有效期使用日起30天）
		 */
		case 110:
			ui.setAmoney(ui.getAMoney() + 100000);
			sendList = new int[]
			{ 57, 57, 57, 117, 117, 117, 120, 113 };
			rtnString = "一次性获得100000筹码，30天VIP标志，另赠送踢人卡3张，低保卡3张，黑桃钻1颗，30天VIP表情";

			break;
		/**
		 * 一次性获得800000筹码，金卡VIP标志（有效期使用日起30天），
		 * 赠送踢人卡4张，低保卡4张，红心钻，VIP表情（有效期使用日起30天）， 会员专场通行证（有效期使用日起30天）
		 */
		case 111:
			ui.setAmoney(ui.getAMoney() + 800000);
			sendList = new int[]
			{ 57, 57, 57, 57, 117, 117, 117, 117, 120, 114 };
			rtnString = "一一次性获得800000筹码，30天金卡VIP标志，另 赠送踢人卡4张，低保卡4张，红心钻1颗，30天VIP表情";
			break;
		/**
		 * 一次性获得6000000筹码，白金VIP标志（有效期使用日起30天），
		 * 赠送踢人卡5张，低保卡5张，梅花钻，VIP表情（有效期使用日起30天）， 会员专场通行证（有效期使用日起30天）
		 */
		case 112:
			ui.setAmoney(ui.getAMoney() + 6000000);
			sendList = new int[]
			{ 57, 57, 57, 57, 57, 117, 117, 117, 117, 117, 120, 115 };
			rtnString = "一次性获得6000000筹码，30天VIP标志，另赠送踢人卡5张，低保卡5张，梅花钻1颗，30天VIP表情";
			break;
		}

		for (int i = 0; i < sendList.length; i++)
		{
			Prop prop = propMap.get(sendList[i] + "");
//			DaoJuService djs = new DaoJuServiceImp(new DaoJuDaoImp(db));
//			djs.buyDJ(ui, ui, prop, 1);
		}
		int nextmoney = ui.getAMoney();
		System.out.println("之前的余额是" + premoney + ",购买之后的余额是" + nextmoney + ","
				+ rtnString);
		return rtnString;
	}

	/**
	 * <?xml version="1.0" encoding="UTF-8"?> <response> <status>1100</status>
	 * <msgType>ChargeUpResp</msgType> <balance>6000.0</balance> <hRet>0</hRet>
	 * </response>
	 */
	public void useChargeUp(int balanceTotal, User user)
	{
		String uid = user.getUid();
		UserInfo ui = UserInfoMemoryCache.getUserInfo(uid);
		int goldbefore = ui.getGold();
		int gold = balanceTotal;
		int chargeNum = gold - goldbefore;
		long curtime = System.currentTimeMillis();
		if ((String) parms.get("status") != null
				&& ((String) parms.get("status")).trim().equals("1100"))
		{
			System.out.println("进入充值成功处理过程");
			ui.setGold(gold);
			ui.setLastUpdateTime(curtime);
//			PlayerManager.writeUserInfo2XmlFile(ui, handleName);

//			String sql = (String) SqlQueryList.SqlList.get("chargeUp");
//			sql = sql.replace("$gold", "" + gold);
//			sql = sql.replace("$uid", uid + "");
//			System.out.println("chargeUp sql=" + sql);
//			db.executeCommand(sql);
			
			SqlSession session = sqlMapper.openSession();
			
			int updateUser = session.update("ndb.chargeUp", ui);
			
			session.commit();
			session.close();
			
			ActionscriptObject response = new ActionscriptObject();

			response.put("bg", goldbefore + "");
			response.put("ag", gold + "");
			response.put("uid", uid);
			response.put("code", (String) parms.get("status"));
			response.put("ts", curtime + "");
			response.put("st", "ok");
			response.put("_cmd", "chargeUp");

			ConnectInstance NextConnect = new ConnectInstance();
			NextConnect.roomKey = "admin";
			NextConnect.aObj = response;
			NextConnect.CreateTmeStampe = curtime;
			NextConnect.priority = 9;
			NextConnect.expireTime = ConstList.level_9_ExpireTime;
			NextConnect.status = 0;
			NextConnect._cmd = "chargeUp";
			response.put("cnt", "用户充值成功恭喜您，您已成功充值" + (chargeNum / 100.0)
					+ " 元，您的帐户将增加" + chargeNum + "点");
			echoResponseString(response);
			++TimerMessageQuene.totalLinkid;
			NextConnect.linkid = TimerMessageQuene.totalLinkid;

			try
			{
				user.sendBbsMessage(NextConnect);
//				PlayerManager.updateMessageQuene2Xml(user, "bbs");
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
		else
		{
			System.out.println("进入充值失败处理过程");
			ActionscriptObject response = new ActionscriptObject();

			response.put("bg", goldbefore + "");
			response.put("uid", uid);
			response.put("ts", curtime + "");
			response.put("st", "fail");
			response.put("cnt", "很抱歉，充值失败！");
			response.put("_cmd", "chargeUp");
			response.put("code", (String) parms.get("status"));

			ConnectInstance NextConnect = new ConnectInstance();
			NextConnect.roomKey = "admin";
			NextConnect.aObj = response;
			NextConnect.CreateTmeStampe = curtime;
			NextConnect.priority = 9;
			NextConnect.expireTime = ConstList.level_9_ExpireTime;
			NextConnect.status = 0;
			NextConnect._cmd = "chargeUp";

			++TimerMessageQuene.totalLinkid;
			NextConnect.linkid = TimerMessageQuene.totalLinkid;

			try
			{
				user.sendBbsMessage(NextConnect);
//				PlayerManager.updateMessageQuene2Xml(user, "bbs");
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}

		}
	}

	public void useExChangeCard()
	{
//		String sql = (String) SqlQueryList.SqlList.get("deleteExcahgeCard");
//		sql = sql.replace("$id", djid + "");
//		db.executeCommand(sql);
		SqlSession session = sqlMapper.openSession();
		
		int updateUser = session.delete("ndb.deleteExcahgeCard");
		
		session.commit();
		session.close();
	}

	public void useFacebag()
	{
//		String sql = (String) SqlQueryList.SqlList.get("usefacebag");
//		sql = sql.replace("$id", djid + "");
//		db.executeCommand(sql);
		SqlSession session = sqlMapper.openSession();
		
		int updateUser = session.update("ndb.usefacebag");
		
		session.commit();
		session.close();
	}

//	public void GetPublicTestUserList()
//	{
//		String sql = (String) SqlQueryList.SqlList.get("getPublicTestUserInfo");
//		sql = sql.replace("$id", djid + "");
//		db.executeCommand(sql);
//	}

	/*
	 * action:响应用户的动作。修改用户密码。 关键字：action，updateUserPassword。
	 */
	public byte[] updateUserPassword(String password, String email)
	{
		ConstList.config.logger.warn("email=" + email);
//		String sql = (String) SqlQueryList.SqlList.get("updateUserPassword");
		String[] userID =
		{ "uid", "name" };
		String EchoUserInfo = "";
		
		SqlSession session = sqlMapper.openSession();
		
		UserInfo userInfo = new UserInfo();
		userInfo.setPassWord(password);
		userInfo.setEmail(email);
		
		int updateUser = session.update("ndb.updateUserPassword", userInfo);
		
		session.commit();
		session.close();
		
		if (isvalidEmail(email, userID))
		{

		}
		else
		{

			EchoUserInfo = BackletKit.errorXml(
					"InvalidEmailAddress");
		}
		return EchoUserInfo.getBytes();
	}

	/**
	 * boolean isvalidEmail(String email,String[] userinfo) action：判断邮箱是否有效。
	 */
	public static boolean isvalidEmail(String email, String[] userinfo)
	{

		SqlSession session = sqlMapper.openSession();
		
		List<UserInfo> selectEmailList = (List<UserInfo>) session.selectOne("ndb.userValidEmail", email);
		
		session.commit();
		session.close();
		try
		{
			if(selectEmailList != null && selectEmailList.size() > 0)
			{
				userinfo[0] = selectEmailList.get(0).getUid();
				userinfo[1] = selectEmailList.get(0).getName();
				return true;
			}
			return false;
		}
		catch (Exception ex)
		{
			return false;
		}

	}

	public boolean dealPublicTestRequest()
	{
		return false;
	}

	/**
	 * 向每一个特定的范围发送公告信息。
	 */
	public boolean dealPublicMessage(String msg, User u, String Cmd,
			String MessageType, byte scopeType)
	{
		User[] ul = null;
		// 处理私人信息
		if (scopeType == 0 && OtherUid != null && OtherUid.equals("")
				&& Utils.isNumeric(OtherUid)
				&& Integer.parseInt(OtherUid) > 10000)
		{
			ul = new User[1];
			ul[0] = UserModule.getInstance().getUserByUserId(Integer.parseInt(OtherUid));
		}
		// 处理房间公共信息
		if (scopeType == 1 && r != null)
		{
			ul = r.getAllUsers();
		}
		// 世界信息
		else if (scopeType == 2)
		{
			ul = UserModule.getInstance().userToArray();
		}
		if (!Utils.isNumeric(MessageType)
				|| (Utils.isNumeric(MessageType) && (Integer
						.parseInt(MessageType) < 0 || Integer
						.parseInt(MessageType) > 5)))
			return false;
		long timeStamp = System.currentTimeMillis();
		try
		{
			ConnectInstance[] NextConnect = new ConnectInstance[ul.length];
			ActionscriptObject response = new ActionscriptObject();

			if (ConstList.blackWordList == null)
				ConstList.blackWordList = XmlReaderUtils
						.retXmlReaderByBW(ConstList.BWConfigFileName);
			if (msg == null)
				return false;
			boolean ifValid = false;
			ifValid = isValidMessage(msg);
			if (!ifValid && Integer.parseInt(u.getUid()) > 10000)
				msg = "【" + u.getName() + "】的发言可能包含敏感词，被自动屏蔽了。";
			else if (!ifValid && isBeAuthrorizedUser(u))
				msg = badWordReplace(msg);

			response.put("uid", u.getUid());
			// edit shen 2014-9-2 begin
			response.put("un", u.getName());
			// edit shen 2014-9-2 end
			response.put("msg", msg);

			if (u.getUid().equals("10000"))
			{
				MessageType = "3";
				response.put("mt", "3");
			}
			else if (!u.getUid().equals("10000") && MessageType.equals("3"))
			{
				MessageType = "2";
				response.put("mt", "2");
			}
			else
				response.put("mt", MessageType);

			response.put("cmd", Cmd);
			response.put("ts", "" + timeStamp);
			if (!ifValid)
				response.put("ib", "yes");
			else
				response.put("ib", "no");
			if (ul != null)
				for (int i = 0; i < ul.length; i++)
				{
					NextConnect[i] = new ConnectInstance();
					NextConnect[i].status = 0;
					if (r != null)
						NextConnect[i].roomKey = r.getName();
					else
						NextConnect[i].roomKey = "admin";

					if (scopeType != 2)
					{
						NextConnect[i].priority = (byte) 9;
						NextConnect[i].expireTime = ConstList.level_9_ExpireTime;
					}
					else
					{
						NextConnect[i].priority = (byte) 3;
						NextConnect[i].expireTime = ConstList.level_3_ExpireTime;
					}

					NextConnect[i].CreateTmeStampe = timeStamp;
					NextConnect[i].aObj = response;
					NextConnect[i]._cmd = Cmd;
					++TimerMessageQuene.totalLinkid;
					NextConnect[i].linkid = TimerMessageQuene.totalLinkid;
					try
					{
						ul[i].sendBbsMessage(NextConnect[i]);
//						PlayerManager.updateMessageQuene2Xml(ul[i], "bbs");
					}
					catch (Exception ex)
					{
						ex.printStackTrace();
					}
				}
			return true;
		}
		catch (Exception ex)
		{
			return false;
		}

	}

	public String badWordReplace(String badString)
	{
		for (int i = 0; i < ConstList.blackWordList.size(); i++)
		{
			if (badString.indexOf(ConstList.blackWordList.get(new Integer(i))) >= 0)
			{
				badString = badString.replaceAll(
						ConstList.blackWordList.get(new Integer(i)), "***");
			}
		}
		return badString;
	}

	public boolean isValidMessage(String Message)
	{
		boolean ifvalid = true;

		for (int i = 0; i < ConstList.blackWordList.size(); i++)
		{
			if (Message.indexOf(ConstList.blackWordList.get(new Integer(i))) >= 0)
			{
				ifvalid = false;
				ConstList.config.logger.warn(Message
						+ " is a bad String,the bad id is " + i
						+ ",the bad word is "
						+ ConstList.blackWordList.get(new Integer(i)));
				break;
			}
		}

		return ifvalid;
	}

	public boolean isBeAuthrorizedUser(User u)
	{
		if (u.getUid().equals("10000"))
			return true;
		else
			return false;

	}

}
