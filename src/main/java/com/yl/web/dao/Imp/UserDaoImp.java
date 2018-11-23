package com.yl.web.dao.Imp;

import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;

import com.yl.Global.ConstList;
import com.yl.Global.PropType;
import com.yl.util.Utils;
import com.yl.vo.Count;
import com.yl.web.dao.UserDao;
import com.yl.web.po.TinyUser;

public class UserDaoImp implements UserDao
{
	private JdbcTemplate jt;

	public void setJt(JdbcTemplate jt)
	{
		this.jt = jt;
	}

	public void saveUser(TinyUser u)
	{
		String sql = "insert into userinfo(uid,name,allmoney,pic,sex,address,status,online,birthday,regtime,logintime,zfmatchpoint,tzmatchpoint,cmatchpoint,loginnum,myads) values(?,?,?,?,?,?,?,?,?,now(),now(),1,1,50,1,?)";

		Object[] params = new Object[]
		{ u.getUid(), u.getName(), u.getMoney(), u.getPic(), u.getSex(),
				u.getAddress(), u.getStatus(), u.getOnline(), u.getBir(),
				u.getMyads() };
		int[] types = new int[]
		{ Types.VARCHAR, Types.VARCHAR, Types.INTEGER, Types.VARCHAR,
				Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR,
				Types.VARCHAR, Types.VARCHAR };
		jt.update(sql, params, types);
		params = null;
		types = null;

	}

	// 查询该uid是否存在
	public Map checkUser(String uid, String name, String pic)
	{
		HashMap retmap = new HashMap();
		String f = "no";
		String sql = "select loginnum,DATE_FORMAT(logintime,'%Y-%m-%d') as lt,uid,allmoney as tmoney,gold,exprience,timestampdiff(second,'1970-01-01 00:00:01',counttime)as ctime,timestampdiff(hour,logintime,now())as ftime from userinfo where uid=?";
		Object[] params = new Object[]
		{ uid };
		int[] types = new int[]
		{ Types.VARCHAR };
		List l = jt.queryForList(sql, params, types);
		int loginNum = 0;
		String loginTime = "";
		if (l != null && l.size() == 1)
		{
			f = "yes";
			int exprience = Integer.parseInt(((Map) l.get(0)).get("exprience")
					.toString());
			loginTime = ((Map) l.get(0)).get("lt").toString();
			retmap.put("ftime", ((Map) l.get(0)).get("ftime"));
			retmap.put("ctime", ((Map) l.get(0)).get("ctime"));
			retmap.put("tmoney", ((Map) l.get(0)).get("tmoney"));
			retmap.put("gold", ((Map) l.get(0)).get("gold"));
			retmap.put("level", Utils.retLevel(exprience));
		}
		if (f.equals("yes"))
		{
			String exstr = "";
			if (!loginTime.equals(""))
			{
				if (loginTime.equals(Utils.getDateToStr(0)))
				{
					exstr = "loginnum=loginnum+1";
					loginNum = Integer.parseInt(((Map) l.get(0))
							.get("loginnum").toString()) + 1;
				}
				else
				{
					exstr = "loginnum=1";
					loginNum = 1;
				}
			}
			retmap.put("loginnum", String.valueOf(loginNum));
			sql = "update userinfo set " + exstr + ",name='" + name + "',pic='"
					+ pic + "',logintime= now(),online='Y' where uid='" + uid
					+ "'";
			jt.update(sql);
		}
		retmap.put("flag", f);
		return retmap;
	}

	public HashMap<String, HashMap> userList()
	{
		String sql = "selcet uid,online,DATE_FORMAT(regtime,'%Y.%m.%d') as rt,DATE_FORMAT(logintime,'%Y.%m.%d') as lt,DATE_FORMAT(exittime,'%Y.%m.%d') as et from userinfo";
		List l = jt.queryForList(sql);
		HashMap<String, HashMap> usersmap = new HashMap<String, HashMap>();
		if (l != null && l.size() >= 1)
		{
			for (int i = 0; i < l.size(); i++)
			{
				HashMap user = new HashMap();
				user.put("uid", ((Map) l.get(i)).get("uid"));
				user.put("online", ((Map) l.get(i)).get("online"));
				user.put("rt", ((Map) l.get(i)).get("rt"));
				user.put("lt", ((Map) l.get(i)).get("lt"));
				user.put("et", ((Map) l.get(i)).get("et"));
				usersmap.put(((Map) l.get(i)).get("uid").toString(), user);
			}
		}
		return usersmap;
	}

	public HashMap<String, HashMap> guangRB()
	{
		String sql = "select uid,name,pic,allmoney from userinfo order by exprience desc limit 100";
		List l = jt.queryForList(sql);
		HashMap<String, HashMap> grbmap = new HashMap<String, HashMap>();
		if (l != null && l.size() >= 1)
		{
			for (int i = 0; i < l.size(); i++)
			{
				HashMap<String, String> user = new HashMap<String, String>();
				user.put("uid", ((Map) l.get(i)).get("uid").toString());
				user.put("name", ((Map) l.get(i)).get("name").toString());
				user.put("pic", ((Map) l.get(i)).get("pic").toString());
				user.put("allmoney", ((Map) l.get(i)).get("allmoney")
						.toString());
				user.put("ph", String.valueOf(i + 1));
				grbmap.put(((Map) l.get(i)).get("uid").toString(), user);
			}
		}
		return grbmap;
	}

	public HashMap<String, HashMap> recUlist()
	{
		String str = Utils.getDateToStr(-1);// 昨天
		String sql = "select uid,name,pic,allmoney from userinfo  where online=1 or logintime>='"
				+ str + " 00:00:00' order by allmoney desc limit 100";
		List l = jt.queryForList(sql);
		HashMap<String, HashMap> recumap = new HashMap<String, HashMap>();
		if (l != null && l.size() >= 1)
		{
			for (int i = 0; i < l.size(); i++)
			{
				HashMap<String, String> user = new HashMap<String, String>();
				user.put("uid", ((Map) l.get(i)).get("uid").toString());
				user.put("name", ((Map) l.get(i)).get("name").toString());
				user.put("pic", ((Map) l.get(i)).get("pic").toString());
				user.put("allmoney", ((Map) l.get(i)).get("allmoney")
						.toString());
				user.put("ph", String.valueOf(i + 1));
				recumap.put(((Map) l.get(i)).get("uid").toString(), user);
			}
		}
		return recumap;
	}

	public HashMap<String, HashMap> whoWin()
	{
		String sql = "select uid,name,pic,allmoney from userinfo order by allmoney desc limit 100";
		List l = jt.queryForList(sql);
		HashMap<String, HashMap> wwmap = new HashMap<String, HashMap>();
		if (l != null && l.size() >= 1)
		{
			for (int i = 0; i < l.size(); i++)
			{
				HashMap<String, String> user = new HashMap<String, String>();
				user.put("uid", ((Map) l.get(i)).get("uid").toString());
				user.put("name", ((Map) l.get(i)).get("name").toString());
				user.put("pic", ((Map) l.get(i)).get("pic").toString());
				user.put("allmoney", ((Map) l.get(i)).get("allmoney")
						.toString());
				user.put("ph", String.valueOf(i + 1));
				wwmap.put(((Map) l.get(i)).get("uid").toString(), user);
			}
		}
		return wwmap;
	}

	// 数据统计
	public HashMap countlist()
	{
		HashMap hm = new HashMap();
		ArrayList clist = new ArrayList();
		String sql = "select DATE_FORMAT(time,'%Y.%m.%d') as time,playercount,regcount,totalcount,nologincount,counttime,fivecount,tencount,tweentycount,thirtycount,overcount from count order by time desc limit 100";
		List l = jt.queryForList(sql);
		if (l != null && l.size() >= 1)
		{
			for (int i = 0; i < l.size(); i++)
			{
				Count c = new Count();
				c.setTime(((Map) l.get(i)).get("time").toString());
				c.setPlayercount(Integer.parseInt(((Map) l.get(i)).get(
						"playercount").toString()));
				c.setRegcount(Integer.parseInt(((Map) l.get(i)).get("regcount")
						.toString()));
				c.setTotalcount(Integer.parseInt(((Map) l.get(i)).get(
						"totalcount").toString()));
				c.setNologincount(Integer.parseInt(((Map) l.get(i)).get(
						"nologincount").toString()));
				c.setCounttime(Integer.parseInt(((Map) l.get(i)).get(
						"counttime").toString()));

				c.setFiveCount(Integer.parseInt(((Map) l.get(i)).get(
						"fivecount").toString()));
				c.setFifteenCount(Integer.parseInt(((Map) l.get(i)).get(
						"tencount").toString()));
				c.setThirtyFiveCount(Integer.parseInt(((Map) l.get(i)).get(
						"tweentycount").toString()));
				c.setSixtyFiveCount(Integer.parseInt(((Map) l.get(i)).get(
						"thirtycount").toString()));
				c.setOverCount(Integer.parseInt(((Map) l.get(i)).get(
						"overcount").toString()));
				clist.add(c);
			}
		}
		hm.put("clist", clist);
		int regNum = 0;
		int onlineNum = 0;
		// 获取当天注册人数
		String str = Utils.getDateToStr(0);// 当天
		String rcsql = "select count(*) as rc from userinfo where regtime between '"
				+ str + " 00:00:00' and '" + str + " 23:59:59'";
		// 获取当前在线人数
		String onlinesql = "select count(*) as oc from userinfo where online='Y'";
		List rclist = jt.queryForList(rcsql);
		List onlinelist = jt.queryForList(onlinesql);

		if (rclist != null && rclist.size() > 0)
		{
			regNum = Integer.parseInt(((Map) rclist.get(0)).get("rc")
					.toString());
		}
		if (onlinelist != null && onlinelist.size() > 0)
		{
			onlineNum = Integer.parseInt(((Map) onlinelist.get(0)).get("oc")
					.toString());
		}

		hm.put("regnum", String.valueOf(regNum));
		hm.put("onlinenum", String.valueOf(onlineNum));
		return hm;
	}

}
