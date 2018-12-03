package com.archy.dezhou.global;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.archy.dezhou.backlet.BackletKit;
import com.archy.dezhou.container.ActionscriptObject;
import com.archy.dezhou.container.MD5;
import com.archy.dezhou.entity.Player;
import com.archy.dezhou.entity.User;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.Logger;

import com.archy.dezhou.container.SFSObjectSerializer;
import com.archy.dezhou.util.Utils;


public class PlayerService
{

	protected static Logger log = Logger.getLogger(PlayerService.class);

	private static SqlSessionFactory	sqlMapper	= null;

	@SuppressWarnings("unchecked")
	public static User NewUserInfoFromDb(String uid)
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

        return null;
	}


	public static ActionscriptObject UpdateUserInfo(User user, ActionscriptObject passwordInfo)
	{
		ActionscriptObject UpdateStatus = new ActionscriptObject();

		String name = "";
		try
		{
			name = URLDecoder.decode(user.getAccount(),"UTF-8");
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}

		if (passwordInfo.size() != 2)
		{
			user.setAccount(name);
			UpdateStatus.put("status", "updateUserInfoOk");
			UpdateStatus.put("cnt", "用户资料修改成功！");
			UpdateStatus.put("code", "1");
		}
		else
		{
			int rcount = 0;
			String psd = MD5.instance().getHash(passwordInfo.getString("op"));
			log.warn(" user MD5 getPassWord= " + user.getPassWord()
				+ " MD5 oldPsd="  + psd);

			if(user.getPassWord().equals(psd))
			{
				log.warn(" user getPassWord= " + passwordInfo.getString("op"));
				rcount = 1;
			}

			if(rcount > 0)
			{
				String newPassword = MD5.instance().getHash(passwordInfo.getString("np"));
				user.setPassWord(newPassword);
				log.warn(" updateInfo MD5 userPassword=" + user.getPassWord()
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
			size = sessionIfRegistered.selectOne("ndb.ifRegistered", userName);
			sessionIfRegistered.commit();
			sessionIfRegistered.close();
		}
		else
		{
			SqlSession sessionIfRepeatName = sqlMapper.openSession();
			size = sessionIfRepeatName.selectOne("ndb.ifRepeatName", Integer.parseInt( uid ));
			sessionIfRepeatName.commit();
			sessionIfRepeatName.close();
		}

        return size > 0;
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

				oneUser.put("name", (((User)userInfoRankList.get(i)).getAccount()));
				oneUser.put("uid", (((User)userInfoRankList.get(i)).getUid()));
				oneUser.put("level", (((User)userInfoRankList.get(i)).getLevel()));
				oneUser.put("allmoney", (((User)userInfoRankList.get(i)).getAMoney()));

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
			uid = ((User)userInfoList.get(0)).getUid();
			log.warn("正常登陆查询数据库uid=" + uid);
		}

		if (uid > 0)// 正常登陆
		{
			try
			{
				Player user = PlayerService.selectPlayerById(uid);

				if (user.getRmoney() > 0)
				{
					user.addAmoney(user.getRmoney());
					user.clearRoomMoney();
				}
				user.setLastUpdateTime(System.currentTimeMillis());

				String s = setNowStandardTimeString();
				user.setLogintime(s);


				UserModule.getInstance().addUser(user);


				user.setUid(uid);

				ActionscriptObject response = getUinfo(user, true);
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
				response = getExtraObject(response,user);
				if (needResetPwd)
				{
					response.put("password", resetPasswd(user));
				}

				StringBuffer sb = new StringBuffer();
				sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");

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
				User getUser = new User();

				getUser.setAccount(userName);
				getUser.setUid(userId);
				getUser.setPassWord(password);
				getUser.setEmail(email);
				getUser.setGendar(gendar);
				getUser.setBirthday(birthday);

				try
				{
					userInfo = sessionAddUser.insert("ndb.addUser", getUser);
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
		String Uid = userid;
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
			size = sessionsIfRegistered.selectOne("ndb.ifRegistered", userName);
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

				User insertUser = new User();

				insertUser.setUid(userId);
				insertUser.setAccount(userName);
				insertUser.setPassWord(password);
				insertUser.setEmail(email);
				insertUser.setGendar(gendar);
				insertUser.setBirthday(birthday);
				insertUser.setMobile(userid);

				try
				{
					sessionAddUser.insert("ndb.addUser", insertUser);
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
				maxuid = session.selectOne("ndb.getMaxUserId");
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
				userinfo[0] = ((User)userEmailList.get(0)).getUid() + "";
				userinfo[1] = ((User)userEmailList.get(0)).getAccount();
				return true;
			}
			return false;
		}
		catch (Exception ex)
		{
			return false;
		}

	}

	public static String resetPasswd(User uinfo)
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

	public static ActionscriptObject getExtraObject(ActionscriptObject response, User uinfo )
	{
		response.put("Ver", ConstList.gameVersion);

		int experience = uinfo.getExprience();
		int[] l = Utils.retLevelAndExp(experience);
		uinfo.setLevel(l[0]);

		response.put("lv", uinfo.getLevel() + "");
		response.put("curjy", l[1] + "");
		response.put("upjy", l[2] + "");

		return response;

	}

	public static HashMap<String, HashMap<String, Integer>> refreshUserShowDj(User uinfo, User user)
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

	public static ActionscriptObject getUsedDj(User uinfo, User user)
	{
		String uid = uinfo.getUid() + "";
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
		return response;
	}

	public static ActionscriptObject getUserAch(User uinfo)
	{
		ActionscriptObject response = new ActionscriptObject();
		ActionscriptObject user = new ActionscriptObject();
		user.put("uid", String.valueOf(uinfo.getUid()));
		user.put("tm", String.valueOf(uinfo.getAMoney()));
		user.put("gl", String.valueOf(uinfo.getGold()));
		user.put("lae",Utils.retLevelAndExp(uinfo.getExprience()));

		user.put("lvl", Utils.retLevelAndExp(uinfo.getExprience())[0]+"");
		user.put("lev", Utils.retLevelAndExp(uinfo.getExprience()));


		response.put("user", user);
		response.put("_cmd", "uinfo");

		return response;
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


	public static int[][] getDiamondList(ActionscriptObject djList,
			User uinfo)
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

		return diamondList;
	}

	public static int getVipid(ActionscriptObject djList, User uinfo)
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

	public static ActionscriptObject getUinfo(User uinfo, boolean isSelf)
	{
		ActionscriptObject response = new ActionscriptObject();
		response.put("uid", "" + uinfo.getUid());
		response.put("nm", uinfo.getAccount() + "");
		// response.put("tm", uinfo.getTmoney()+"");
		response.put("rm", uinfo.getRmoney() + "");
		//response.put("password", uinfo.getPassWord());

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
		response.put("pic", pic + "");

		String add = "-1";
		if (uinfo.getAddress() != null && uinfo.getAddress().length() > 0)
		{
			add = uinfo.getAddress();
		}
		response.put("add", add + "");



        int experience = uinfo.getExprience();
        int[] l = Utils.retLevelAndExp(experience);
        uinfo.setLevel(l[0]);
        response.put("lv", uinfo.getLevel() + "");
        response.put("curjy", l[1] + "");
        response.put("upjy", l[2] + "");



		String mobi = "-1";
		if (uinfo.getMobile() != null && uinfo.getMobile().length() > 0)
		{
			if (isSelf)
				mobi = uinfo.getMobile();
			else
				mobi = "*";
		}
		response.put("mobi", mobi + "");


		return response;
	}

	public static Player selectPlayerById(Integer uid)
    {
	    return new Player();
    }

    public static Player selectPlayerByAccount(String account)
    {
	    return new Player();
    }

    public static boolean savePlayer(Player player)
    {
        return true;
    }




}
