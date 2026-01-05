package com.archy.texasholder.service;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


import com.archy.texasholder.container.MD5;
import com.archy.texasholder.entity.Player;
import com.archy.texasholder.entity.User;
import com.archy.texasholder.global.ConstList;

import jakarta.annotation.Resource;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


@Service
public class PlayerService
{

	private static Logger log = LoggerFactory.getLogger(PlayerService.class);

	private static SqlSessionFactory sqlMapper = null;

	@Resource
	private UserService userService;

	@SuppressWarnings("unchecked")
	public static User NewUserInfoFromDb(String uid)
	{
		SqlSession session = sqlMapper.openSession();
		List<Object> getUserInfo = null;
		try
		{
			getUserInfo = session.selectList("ndb.getUserInfo", uid);
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



	/*****************************************************************************************
	 * action:响应用户的动作。响应用户的注册动作。 关键字：action，Register
	 */
	@SuppressWarnings("deprecation")
	public String Register(String userName, String password,
			String email, String gendar, String birthday, String UserId, String key)
	{
		int rcount = -1;
		int userId = 0;
		String EchoUserInfo = "";
		boolean ifSuccess = true;

		if (userName.equals("") || userName == null)
		{
			return "userNameIsRequired";
		}
		else if (password.equals("") || password == null)
		{
			return "userPassWordIsRequired";
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
			EchoUserInfo = "registerOk";
		}
		else if (rcount > 0)
		{
			EchoUserInfo = "NameIsRepeated";
		}
		else if (rcount < 0)
		{
			EchoUserInfo = "MysqlError";
		}
		return EchoUserInfo;
	}


	@SuppressWarnings({ "unchecked" })
	public HashMap<String, String> AutoRegister(String userid, String key)
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

	public static Player selectPlayerById(Integer uid)
    {
	    return null;
    }

    public static Player selectPlayerByAccount(String account)
    {
	    return null;
    }

    public static boolean savePlayer(Player player)
    {
        return true;
    }




}
