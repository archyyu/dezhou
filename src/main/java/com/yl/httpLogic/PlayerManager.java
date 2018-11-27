package com.yl.httpLogic;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.yl.ndb.DBServer;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.Logger;

import com.yl.Global.ConstList;
import com.yl.Global.UserInfoMemoryCache;
import com.yl.Global.UserModule;
import com.yl.backlet.BackletKit;
import com.yl.container.ActionscriptObject;
import com.yl.container.MD5;
import com.yl.container.SFSObjectSerializer;
import com.yl.container.User;
import com.yl.util.PayFinalValue;
import com.yl.util.Utils;
import com.yl.entity.UserInfo;

/**
 * 用户实体对象共分为3类： user_info 用户的属性信息，为用户注册的信息汇总。 user 用户之间互动的信息。 player 用户玩德州扑克的信息。
 */
public class PlayerManager
{

	/*******************************************************************************
	 * 操作UserInfoMemoryCache.userinfo单个元素的方法相关。 从数据库中获得单个用户的属性信息userinfo。
	 * 关键字：uid_info.xml
	 */
	
	private static SqlSessionFactory	sqlMapper	= DBServer.getInstance().getSqlMapper();
	protected static Logger log = Logger.getLogger(PlayerManager.class);
	
	@SuppressWarnings("unchecked")
	public static UserInfo NewUserInfoFromDb(String uid)
	{
		SqlSession session = sqlMapper.openSession();
		List<Object> getUserInfo = null;
		try
		{
			getUserInfo =   session.selectList("ndb.getUserInfo", uid);
			session.commit();
		}
		catch (Exception e)
		{
			log.error("mysql error", e);
		}
		finally
		{
			session.close();	
		}
		

		if(getUserInfo != null && getUserInfo.size() > 0)
		{
			UserInfo usInfo = getUserInfoData((UserInfo) getUserInfo.get(0));
			return usInfo;
		}
		else 
		{
			log.warn(" 错误！userInfo=" );
			return null;
		}
	}

	public static UserInfo getUserInfoData(UserInfo usInfo)
	{
		UserInfo userInfo = usInfo;
		
		String ifrewards = String.valueOf(usInfo.getIfRewards());
		if (ifrewards != null && ifrewards.length() > 4)
		{
			userInfo.lastAdwardsTime = ifrewards;
		}
		else
		{
			userInfo.lastAdwardsTime = String.valueOf(System.currentTimeMillis());
		}

		String todayStr = Utils.getDateToStr(0);
		userInfo.setDateStr(todayStr);
		UserInfoMemoryCache.addUserInfo(userInfo);
		
		return userInfo;
	}
	
	

	public static ActionscriptObject UpdateUserInfo(UserInfo userInfo, ActionscriptObject passwordInfo)
	{	
		ActionscriptObject UpdateStatus = new ActionscriptObject();
		
		String name = "";
		try 
		{
			name = URLDecoder.decode(userInfo.getName(),"UTF-8");
		}
		catch (UnsupportedEncodingException e) 
		{
			e.printStackTrace();
		}
		
		if (passwordInfo.size() != 2)
		{
			userInfo.setName(name);
			userInfo.setSaveUpdate(true);
			UpdateStatus.put("status", "updateUserInfoOk");
			UpdateStatus.put("cnt", "用户资料修改成功！");
			UpdateStatus.put("code", "1");
		}
		else
		{
			int rcount = 0;
			userInfo.setSaveUpdate(false);
			String psd = MD5.instance().getHash(passwordInfo.getString("op"));
			log.warn(" userInfo MD5 getPassWord= " + userInfo.getPassWord() 
				+ " MD5 oldPsd="  + psd);
			
			if(userInfo.getPassWord().equals(psd))
			{
				log.warn(" userInfo getPassWord= " + passwordInfo.getString("op"));				
				rcount = 1;
			}
					
			if(rcount > 0)
			{
				String newPassword = MD5.instance().getHash(passwordInfo.getString("np"));
				userInfo.setPassWord(newPassword);	
				userInfo.setSaveUpdate(true);			
				log.warn(" updateInfo MD5 userPassword=" + userInfo.getPassWord()
					+ "   userPassword=" + passwordInfo.getString("np"));			
				UpdateStatus.put("status", "updateUserInfoOkIncludePassword");
				UpdateStatus.put("cnt", "用户密码修改成功！");
				UpdateStatus.put("code", "1");			
			}
			else
			{
				UpdateStatus.put("status", "PasswordIsNoMatch");
				UpdateStatus.put("code", "0");
				UpdateStatus.put("cnt", "用户密码不匹配，密码修改失败！");
				userInfo.setSaveUpdate(false);
			}
		}

		return UpdateStatus;
		
	}

	public static boolean ifRegistered(String userName, String uid)
	{
		int size = 0;
		if (uid.equals("") || uid == null)
		{
			SqlSession sessionIfRegistered= sqlMapper.openSession();
			size = (Integer) sessionIfRegistered.selectOne("ndb.ifRegistered", userName);
			sessionIfRegistered.commit();
			sessionIfRegistered.close();
		}
		else
		{
			SqlSession sessionIfRepeatName = sqlMapper.openSession();
			size = (Integer) sessionIfRepeatName.selectOne("ndb.ifRepeatName", Integer.parseInt( uid ));
			sessionIfRepeatName.commit();
			sessionIfRepeatName.close();
		}

		if(size > 0)
		{
			return true;
		}
		return false;
	}

	
	
	@SuppressWarnings("unchecked")
	public static ActionscriptObject RankList()
	{
		ActionscriptObject rankList = new ActionscriptObject();

		SqlSession sessionRank = sqlMapper.openSession();
		List<Object> userInfoRankList = null;
		try
		{
			userInfoRankList =  sessionRank.selectList("ndb.rank");
			sessionRank.commit();
		}
		catch (Exception t)
		{
			log.error("mysql error", t);
		}
		finally
		{
			sessionRank.close();
		}
		
		if(userInfoRankList != null && userInfoRankList.size() > 0)
		{			
			int j = userInfoRankList.size();
			
			for (int i = 0; i < j; i++)
			{
				ActionscriptObject oneUser = new ActionscriptObject();
				
				oneUser.put("pic", (((UserInfo)userInfoRankList.get(i)).getPic()));
				oneUser.put("name", (((UserInfo)userInfoRankList.get(i)).getName()));
				oneUser.put("uid", (((UserInfo)userInfoRankList.get(i)).getUid()));
				oneUser.put("level", (((UserInfo)userInfoRankList.get(i)).getLevel()));
				oneUser.put("allmoney", (((UserInfo)userInfoRankList.get(i)).getAMoney()));
				
				rankList.put(i, oneUser);			
			}
		}
		rankList.put("ts", "" + System.currentTimeMillis());
		rankList.put("cmd", "rank");
		return rankList;
	}

	/**
	 * message2RoomUser(Room r,String triggerName,String messageCntOther,String
	 * messageCntSelf) 向房间中的成员发送信息，将信息写到成员的userid_status.xml文件中。
	 */

	/**
	 * byte[] UserLogin(String userName,String Password) action,用户登陆。
	 * 关键字：action，UserLogin
	 */
	@SuppressWarnings("unchecked")
	public static byte[] UserLogin(String userName, String Password, boolean isauto,
			String mobileUserId, String key, int loginType, boolean needResetPwd)
	{
		int uid = -1;

		if (mobileUserId == null || mobileUserId.equals(""))
		{
			mobileUserId = "-1";
		}
	
		List<Object> userInfoList = null;
		
		if (loginType == 0)
		{

			SqlSession sessionUserLogin = sqlMapper.openSession();
			
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("password", Password);
			map.put("uid", Integer.parseInt(userName));
			
			try
			{
				userInfoList = sessionUserLogin.selectList("ndb.userLogin", map);
				sessionUserLogin.commit();
			}
			catch (Exception t)
			{
				log.error("error", t);
			}
			finally
			{
				sessionUserLogin.close();			
			}
		}
		else if (loginType == 1)
		{
			SqlSession sessionUserLoginMobile = sqlMapper.openSession();
			try
			{
				userInfoList = sessionUserLoginMobile.selectList("ndb.userLoginMobile", mobileUserId);
				sessionUserLoginMobile.commit();
			}
			catch (Exception t)
			{
				log.error("error", t);
			}
			finally
			{
				sessionUserLoginMobile.close();		
			}
		}
		else
		{
			return BackletKit.errorXml("ErrorUserNameLogined").getBytes();
		}

		if(userInfoList != null && userInfoList.size() > 0)
		{
			uid = Integer.parseInt(((UserInfo)userInfoList.get(0)).getUid());
			log.warn("正常登陆查询数据库uid=" + uid);
		}

		if (uid > 0)// 正常登陆
		{
			try
			{
				UserInfo uinfo = UserInfoMemoryCache.getUserInfo(uid + "");
				if (uinfo == null)
				{
					uinfo = NewUserInfoFromDb(String.valueOf(uid));
				}

				if (uinfo.getRmoney() > 0)
				{
					uinfo.addAmoney(uinfo.getRmoney());
					uinfo.clearRoomMoney();
				}
				uinfo.setLastUpdateTime(System.currentTimeMillis());
				uinfo.setLoginNum(uinfo.getLoginNum() + 1);

				String s = setNowStandardTimeString();
				uinfo.setLogintime(s);
				uinfo.setOline(true);
				UserInfoMemoryCache.addUserInfo(uinfo);
				
				User user = new User(uinfo.getName());
				user.setUid(String.valueOf(uid));
				
				UserModule.getInstance().addUser(user);
				
				if (key == null || key.length() < 10)
					key = setDefaultUserKeyWithNullUserId(user.getUid());

				user.setUid(String.valueOf(uid));
				user.bbx_userkey = key;

				ActionscriptObject response = getUinfo(uinfo, true);
				if (isauto)
				{
					response.put("isauto", "yes");
					response.put("active", "no");
					response.put("password", Password);
				}
				if (loginType == 1)
				{
					response.put("mu", mobileUserId);
				}
				response.put("key", key);
				response = getExtraObject(response, uinfo, user);
				if (needResetPwd)
				{
					response.put("password", resetPasswd(uinfo));
				}
				uinfo.setPropmap(refreshUserShowDj(uinfo, user));
				
				StringBuffer sb = new StringBuffer();
				sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
				
				log.debug("  response==== " + uinfo.getAchList());
				return SFSObjectSerializer.obj2xml(response, 0, "", sb);
			}
			catch (Exception ex)
			{
				return BackletKit.errorXml("Loginedfail").getBytes();
			}
		}
		else
		{
			return BackletKit.errorXml("ErrorUserNameOrPassword").getBytes();
		}

	}


	/*****************************************************************************************
	 * action:响应用户的动作。响应用户的注册动作。 关键字：action，Register
	 */
	@SuppressWarnings("deprecation")
	public static byte[] Register(String userName, String password,
			String email, String gendar, String birthday, String UserId, String key)
	{
		int rcount = -1;
		int userId = 0;
		String EchoUserInfo = "";
		boolean ifSuccess = true;

		if (userName.equals("") || userName == null)
		{
			return BackletKit.errorXml("userNameIsRequired").getBytes();
		}
		else if (password.equals("") || password == null)
		{
			return BackletKit.errorXml("userPassWordIsRequired").getBytes();
		}

		
		int userInfo = 0;
		if (!ifRegistered(userName, ""))
		{
			try
			{

				userId = CreateUserId();
				
				SqlSession sessionAddUser = sqlMapper.openSession();
				UserInfo getUserInfo = new UserInfo();
				
				getUserInfo.setName(userName);
				getUserInfo.setUid(userId);
				getUserInfo.setPassWord(password);
				getUserInfo.setEmail(email);
				getUserInfo.setGendar(gendar);
				getUserInfo.setBirthday(birthday);
					
				try
				{
					userInfo = sessionAddUser.insert("ndb.addUser", getUserInfo);
					sessionAddUser.commit();		
				}
				catch (Exception t)
				{
					log.error("mysql", t);
				}
				finally
				{
					sessionAddUser.close();
				}
				log.debug(" Register注册：" + userInfo +  "  uId="  + userId);

				log.debug("player Control--name=" + userName
								+ ",password=" + password + ",email=" + email
								+ ",gendar=" + gendar + ",birthday=" + birthday);

				log.debug(" 成功！insert="  + userInfo);
				rcount = 0;
			}
			catch (Exception Ex)
			{
				Ex.printStackTrace();
	
				log.debug(" DebugInsert="  + userInfo);
				log.debug(Ex.getMessage());
			}

		}
		else
		{
			rcount = 1;
		}

		if (ifSuccess && rcount == 0)
		{
			EchoUserInfo = BackletKit.okXml("registerOk");
		}
		else if (rcount > 0)
		{
			EchoUserInfo = BackletKit.errorXml(
					"NameIsRepeated");
		}
		else if (rcount < 0)
		{
			EchoUserInfo = BackletKit.errorXml(
					"MysqlError");
		}
		return EchoUserInfo.getBytes();
	}

	
	@SuppressWarnings({ "unchecked" })
	public static HashMap<String, String> AutoRegister(String userid, String key)
	{
		int rcount = 0;
		int userId = 0;
		
		HashMap<String, String> userInfoMap = new HashMap<String, String>();
		String Uid = getUidFromMobileUserid(userid);
		if (!Uid.equals("-1"))
		{
			log.warn("这个用户已经注册过");
	
			SqlSession sessionResetUserTime = sqlMapper.openSession();
			
			try
			{
				sessionResetUserTime.update("ndb.ResetUserTime", userid);
				sessionResetUserTime.commit();
			}
			catch (Exception t)
			{
				log.error("mysql error", t);
			}
			finally
			{
				sessionResetUserTime.close();
			}
			return userInfoMap;
		}

		if (key == null)
			key = "";
		String[] passwordList =
		{ "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n",
				"o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", };
		String password = "";
		String email = "";
		String gendar = "-1";
		String birthday = "-1";

		userId = CreateUserId();
		String userName = "C" + userId;

		SqlSession sessionsIfRegistered = sqlMapper.openSession();
		
		int size = 0;
		try
		{
			size = (Integer) sessionsIfRegistered.selectOne("ndb.ifRegistered", userName);
			sessionsIfRegistered.commit();
		}
		catch (Exception t)
		{
			log.error("mysql error", t);
		}
		finally
		{
			sessionsIfRegistered.close();
		}
		if (size > 0)
		{
			userName = "A" + System.currentTimeMillis() + "";
		}

		if (rcount == 0)
		{
			Random random = new Random();
			for (int i = 0; i < 6; i++)
			{
				int suffix = Math.abs(random.nextInt() % (passwordList.length));
				password += passwordList[suffix];
			}

			try
			{

				SqlSession sessionAddUser = sqlMapper.openSession();
				
				UserInfo insertUserInfo = new UserInfo();
				
				insertUserInfo.setUid(userId);
				insertUserInfo.setName(userName);
				insertUserInfo.setPassWord(password);
				insertUserInfo.setEmail(email);
				insertUserInfo.setGendar(gendar);
				insertUserInfo.setBirthday(birthday);
				insertUserInfo.setMobile(userid);
				insertUserInfo.setSessionKey(key);
				
				try
				{
					sessionAddUser.insert("ndb.addUser", insertUserInfo);
					sessionAddUser.commit();
				}
				finally
				{
					sessionAddUser.close();
				}

				log.warn("player Control--name=" + userName
						+ ",password=" + password + ",email=" + email
						+ ",gendar=" + gendar + ",birthday=" + birthday + ",userId=" + userId);

				userInfoMap.put("name", userName);
				userInfoMap.put("uid", userId + "");
				userInfoMap.put("password", password);

			}
			catch (Exception Ex)
			{
				Ex.printStackTrace();
			}

		}
		NewUserInfoFromDb(userId + "");
		
		return userInfoMap;
	}

	/*
	 * action:响应用户的动作。获取到一个可用的uid。 关键字：action，CreateUserId。
	 */
	public static int CreateUserId()
	{
		int maxuid = 0;
		try
		{
			SqlSession session = sqlMapper.openSession();
			try
			{
				maxuid = (Integer) session.selectOne("ndb.getMaxUserId");
				session.commit();
			}
			catch (Exception e)
			{
				log.error("mysql error", e);
			}
			finally
			{
				session.close();
			}
			maxuid += 1;
		}
		catch (Exception ex)
		{
			maxuid = 10001;
		}
		return maxuid;
	}

	/**
	 * boolean isvalidEmail(String email,String[] userinfo) action：判断邮箱是否有效。
	 */
	@SuppressWarnings("unchecked")
	public static boolean isvalidEmail(String email, String[] userinfo)
	{
		SqlSession session = sqlMapper.openSession();
		List<Object> userEmailList = null;
		
		try
		{
			userEmailList = session.selectList("ndb.userValidEmail", email);
			session.commit();
		}
		catch (Exception t)
		{
			log.error("mysql error", t);
		}
		finally
		{
			session.close();
		}
		
		try
		{
			if(userEmailList != null && userEmailList.size() > 0)
			{
				userinfo[0] = ((UserInfo)userEmailList.get(0)).getUid();
				userinfo[1] = ((UserInfo)userEmailList.get(0)).getName();
				return true;
			}
			return false;
		}
		catch (Exception ex)
		{
			return false;
		}

	}

	public static String resetPasswd(UserInfo uinfo)
	{
		String resetpasswd = "a";
		String[] passwordList =
		{ "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n",
				"o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", };
		Random random = new Random();
		for (int i = 0; i < 6; i++)
		{
			int suffix = Math.abs(random.nextInt() % (passwordList.length));
			resetpasswd += passwordList[suffix];
		}
		
		uinfo.setPassWord(resetpasswd);
	
		SqlSession session = sqlMapper.openSession();
		try
		{
			session.update("ndb.resetUserPassword", uinfo);
			session.commit();
		}
		catch (Exception t)
		{
			log.error("mysql", t);
		}
		finally
		{
			session.close();
		}
		return resetpasswd;
	}

	public static ActionscriptObject getExtraObject(ActionscriptObject response, UserInfo uinfo, User user)
	{
		String MaxHandPuker = (uinfo.getMaxHandStr() == null || uinfo.getMaxHandStr()
				.equals("")) ? "-1" : uinfo.getMaxHandStr();
		String isTeachFinished = (uinfo.isTeachFinished == null || uinfo.isTeachFinished
				.equals("")) ? "-1" : uinfo.isTeachFinished;
		response.put("maxhand", MaxHandPuker);
		response.put("isTeachFinished", isTeachFinished);
		response.put("maxTMoney", uinfo.getmaxTMoney() + "");
		response.put("Ver", ConstList.gameVersion);

		ActionscriptObject djObjList = getUsedDj(uinfo, user);

		int[][] diamondList = getDiamondList(djObjList, uinfo);
		response.put("diamond", getDiamondListStr(diamondList));
		response.put("vip", getVipid(djObjList, uinfo) + "");
		int experience = uinfo.getExprience();
		int[] l = Utils.retLevelAndExp(experience);
		uinfo.setLevel(l[0]);
		uinfo.diamondList = diamondList;
		response.put("lv", uinfo.getLevel() + "");
		response.put("curjy", l[1] + "");
		response.put("upjy", l[2] + "");

		return response;

	}

	public static HashMap<String, HashMap<String, Integer>> refreshUserShowDj(UserInfo uinfo, User user)
	{
		HashMap<String, HashMap<String, Integer>> props = new HashMap<String, HashMap<String, Integer>>();
		ActionscriptObject djObjList = getUsedDj(uinfo, user);
		HashMap<String, Integer> equipDJHashMap = new HashMap<String, Integer>();
		HashMap<String, Integer> equipGiftHashMap = new HashMap<String, Integer>();
		ActionscriptObject mydjList = (ActionscriptObject) djObjList.get("djlist");
		if (mydjList != null && mydjList.size() > 0)
		{
			for (int i = 0; i < mydjList.size(); i++)
			{
				ActionscriptObject daoju = (ActionscriptObject) mydjList.get(i);
				int gtype = Integer.parseInt((String) daoju.get("gtype"));
				switch (gtype)
				{
				case 117:
					equipDJHashMap.put("117", 1);
				case 57:
					equipDJHashMap.put("57", 1);
					break;
				}

			}
		}
		ActionscriptObject myGiftList = (ActionscriptObject) djObjList.get("mgift");
		if (myGiftList != null && myGiftList.size() > 0)
		{
			ActionscriptObject daoju = (ActionscriptObject) myGiftList.get(0);
			equipGiftHashMap.put((String) daoju.get("gtype"), 1);
		}

		if (equipGiftHashMap.size() == 0)
			equipGiftHashMap.put("133", 1);
		if (equipDJHashMap.size() == 0)
			equipDJHashMap.put("133", 1);

		props.put("A", equipGiftHashMap);
		props.put("B", equipDJHashMap);

		return props;
	}

	public static ActionscriptObject getUsedDj(UserInfo uinfo, User user)
	{
		String uid = uinfo.getUid();
		ActionscriptObject response = new ActionscriptObject();
		ActionscriptObject reqObj = new ActionscriptObject();
		reqObj.put("uid", uid);
		reqObj.put("type", "inUse");
		reqObj.put("rn", "admin");
		//TODO
//		response = UserModule.getInstance().handleRequest(ConstList.CMD_MYDJ, reqObj, user,
//				UserModule.getInstance().getAdminRoomId(), false);
//		
//		
//		log.warn(" usedDJ=" + response);
		uinfo.MyEquipedDaoju = response;
		return response;
	}
	
	public static ActionscriptObject getUserAch(UserInfo uinfo)
	{
		ActionscriptObject response = new ActionscriptObject();
		ActionscriptObject user = new ActionscriptObject();
		user.put("uid", String.valueOf(uinfo.getUid()));
		user.put("tm", String.valueOf(uinfo.getAMoney()));
		user.put("gl", String.valueOf(uinfo.getGold()));
		user.put("pic", uinfo.getPic());
		user.put("ach", String.valueOf(uinfo.getAchievement()));
		user.put("lae",Utils.retLevelAndExp(uinfo.getExprience()));

		user.put("lvl", Utils.retLevelAndExp(uinfo.getExprience())[0]+"");
		user.put("lev", Utils.retLevelAndExp(uinfo.getExprience()));

		user.put("tzmp", String.valueOf(uinfo.getTzMatchPoint()));
		user.put("zfmp", String.valueOf(uinfo.getZfMatchPoint()));
		user.put("cdtype", uinfo.getCdtype());

		user.put("cdt", Utils.millsTosecond(uinfo.getCoolDownTime()));
		user.put("bmp", Utils.millsTosecond(uinfo.getBaceMPoint()));
		user.put("cmp", String.valueOf(uinfo.getCMatchPoint()));

		response.put("user", user);
		response.put("_cmd", "uinfo");
		
		return response;
	}
	public static boolean ifHavaEquipedSameDaoju(UserInfo uinfo, User user, int Djid)
	{
		boolean flag = false;
		ActionscriptObject djObjList = getUsedDj(uinfo, user);

		ActionscriptObject mydjList = (ActionscriptObject) djObjList
				.get("djlist");
		if (mydjList != null && mydjList.size() > 0)
		{
			for (int i = 0; i < mydjList.size(); i++)
			{
				ActionscriptObject daoju = (ActionscriptObject) mydjList.get(i);
				int gtype = Integer.parseInt((String) daoju.get("gtype"));
				if (Djid == gtype)
				{
					flag = true;
				}

			}
		}
		return flag;
	}

	public static String getDiamondListStr(int[][] diamondList)
	{
		String Str = "";

		for (int i = 0; i < diamondList.length; i++)
		{

			for (int j = 0; j < diamondList[i].length; j++)
			{
				if (i < 3 || j == 0)
					Str += diamondList[i][j] + "@";
				else
					Str += diamondList[i][j] + "";
			}
		}
		if (Str.equals(""))
			Str = "-1";
		return Str;

	}

	public static int SendMoneyWithDiamondList(UserInfo uinfo)
	{
		int[][] diamondList = uinfo.diamondList;

		return PayFinalValue.GIVEN_BET_EveryDay + diamondList[0][1] * 2000
				+ diamondList[1][1] * 15000 + diamondList[2][1] * 8000
				+ diamondList[3][1] * 35000;
	}

	public static int[][] getDiamondList(ActionscriptObject djList,
			UserInfo uinfo)
	{
		int[][] diamondList = new int[][]
		{
		{ 113, 0 },
		{ 114, 0 },
		{ 115, 0 },
		{ 116, 0 }, };

		ActionscriptObject mydjList = (ActionscriptObject) djList.get("djlist");
		if (mydjList != null && mydjList.size() > 0)
		{
			for (int i = 0; i < mydjList.size(); i++)
			{
				ActionscriptObject daoju = (ActionscriptObject) mydjList.get(i);
				int gtype = Integer.parseInt((String) daoju.get("gtype"));
				switch (gtype)
				{
				case 113:
					diamondList[0][1]++;
					break;
				case 114:
					diamondList[1][1]++;
					break;
				case 115:
					diamondList[2][1]++;
					break;
				case 116:
					diamondList[3][1]++;
					break;
				}
			}
		}
		uinfo.diamondList = diamondList;

		return diamondList;
	}

	public static int getVipid(ActionscriptObject djList, UserInfo uinfo)
	{

		int vipid = -1;
		ActionscriptObject mydjList = (ActionscriptObject) djList.get("djlist");
		if (mydjList != null && mydjList.size() > 0)
		{
			for (int i = 0; i < mydjList.size(); i++)
			{
				ActionscriptObject daoju = (ActionscriptObject) mydjList.get(i);
				int gtype = Integer.parseInt((String) daoju.get("gtype"));
				switch (gtype)
				{
				case 108:
					if (vipid < 108)
						;
					vipid = 108;
					break;
				case 109:
					if (vipid < 109)
						;
					vipid = 109;
					break;
				case 110:
					if (vipid < 110)
						;
					vipid = 110;
					break;
				case 111:
					if (vipid < 111)
						;
					vipid = 111;
					break;
				case 112:
					if (vipid < 112)
						;
					vipid = 112;
					break;
				}
			}
		}
		uinfo.vipid = vipid;
		return vipid;
	}

	public static String setDefaultUserKeyWithNullUserId(String Uid)
	{
		return ("id" + Uid + "Ts" + System.currentTimeMillis() + "EmulaterPlayerSetSession")
				.substring(0, 32);
	}

	public static String setNowStandardTimeString()
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String s = sdf.format(new Date());
		return s;
	}

	public static ActionscriptObject getUinfo(UserInfo uinfo, boolean isSelf)
	{
		ActionscriptObject response = new ActionscriptObject();
		response.put("uid", "" + uinfo.getUid());
		response.put("nm", uinfo.getName() + "");
		// response.put("tm", uinfo.getTmoney()+"");
		response.put("rm", uinfo.getRmoney() + "");
		//response.put("password", uinfo.getPassWord());
		response.put("maxTMoney", uinfo.getmaxTMoney() + "");

		String email = "-1";
		if (uinfo.getEmail() != null && uinfo.getEmail().length() > 0)
		{
			if (isSelf)
				email = uinfo.getEmail();
			else
				email = "*";
		}
		response.put("email", email + "");

		String Sex = "-1";
		if (uinfo.getGendar() != null && uinfo.getGendar().length() > 0)
		{
			Sex = uinfo.getGendar();
			if (Sex.equals("男") || Sex.equals("1"))
				Sex = "1";
			else if (Sex.equals("女") || Sex.equals("0"))
				Sex = "0";
			else
				Sex = "-1";
		}
		response.put("sex", Sex + "");

		String bir = "-1";
		if (uinfo.getBirthday() != null && uinfo.getBirthday().length() > 0)
		{
			bir = uinfo.getBirthday();
		}
		response.put("bir", bir + "");

		response.put("gold", uinfo.getGold() + "");

		String pic = "-1";
		if (uinfo.getPic() != null && uinfo.getPic().length() > 0)
		{
			pic = uinfo.getPic();
		}
		response.put("pic", pic + "");

		response.put("wn", uinfo.getWinNum() + "");
		response.put("ln", (uinfo.getCompletePkNum() - uinfo.getWinNum() ) + "");

		String add = "-1";
		if (uinfo.getAddress() != null && uinfo.getAddress().length() > 0)
		{
			add = uinfo.getAddress();
		}
		response.put("add", add + "");

		User u = UserModule.getInstance().getUserByUserId(Integer.parseInt(uinfo.getUid()));
		
		uinfo.setPropmap(refreshUserShowDj(uinfo, u));
		
		if (u == null)
		{
			response = getExtraObject(response, uinfo, u);
		}
		else
		{
			int experience = uinfo.getExprience();
			int[] l = Utils.retLevelAndExp(experience);
			uinfo.setLevel(l[0]);
			response.put("lv", uinfo.getLevel() + "");
			response.put("curjy", l[1] + "");
			response.put("upjy", l[2] + "");

			String MaxHandPuker = (uinfo.getMaxHandStr() == null || uinfo.getMaxHandStr()
					.equals("")) ? "-1" : uinfo.getMaxHandStr();
			String isTeachFinished = (uinfo.isTeachFinished == null || uinfo.isTeachFinished
					.equals("")) ? "-1" : uinfo.isTeachFinished;
			response.put("maxhand", MaxHandPuker);
			response.put("isTeachFinished", isTeachFinished);
			response.put("maxTMoney", uinfo.getmaxTMoney() + "");
			response.put("Ver", ConstList.gameVersion);
			response.put("vip", uinfo.vipid + "");
			int[][] diamondList = uinfo.diamondList;
			response.put("diamond", getDiamondListStr(diamondList));
		}

		String mobi = "-1";
		if (uinfo.getMobile() != null && uinfo.getMobile().length() > 0)
		{
			if (isSelf)
				mobi = uinfo.getMobile();
			else
				mobi = "*";
		}
		response.put("mobi", mobi + "");

		if (ifRewards(uinfo))
		{
			response.put("ifReward", "10000");
			uinfo.setAmoney(uinfo.getAMoney() + 10000);
			response.put("tm", uinfo.getAMoney() + "");
			uinfo.lastAdwardsTime = System.currentTimeMillis() + "";
		}
		else
		{
			response.put("ifReward", "0");
			response.put("tm", uinfo.getAMoney() + "");
		}

		return response;
	}


	public static boolean ifRewards(UserInfo uinfo)
	{
		long rewardTime = Long.parseLong(uinfo.lastAdwardsTime);
		System.out.println("rewardTime=" + rewardTime);
		System.out.println("当前时间=" + System.currentTimeMillis());
		boolean ifReward = false;
		if (rewardTime > 0)
		{
			long curTime = System.currentTimeMillis();
			Date preDate = new Date(rewardTime);
			Date curDate = new Date(curTime);

			Calendar pCalendar = Calendar.getInstance();
			Calendar cCalendar = Calendar.getInstance();
			pCalendar.setTime(preDate);
			cCalendar.setTime(curDate);
			if (pCalendar.get(Calendar.DAY_OF_MONTH) != cCalendar
					.get(Calendar.DAY_OF_MONTH))
			{
				ifReward = true;
			}
		}
		else
			ifReward = true;

		return ifReward;

	}

	@SuppressWarnings("unchecked")
	public static String getUidFromMobileUserid(String mobileUserId)
	{

		String uid = "-1";

		SqlSession session = sqlMapper.openSession();
		List<Object> userInfoList = null;
		
		try
		{
			userInfoList = session.selectList("ndb.userLoginMobile", mobileUserId);
			session.commit();
		}
		catch(Exception t)
		{
			log.error("mysql error", t);
		}
		finally
		{
			session.close();
		}
		
		if(userInfoList != null && userInfoList.size() > 0)
		{
			uid = ((UserInfo)userInfoList.get(0)).getUid();
			log.warn("正常登陆查询数据库,找到了该用户伪码的用户ID不为空，uid="+ uid);
		}
		return uid;
	}

}
