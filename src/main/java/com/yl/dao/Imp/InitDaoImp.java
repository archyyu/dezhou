package com.yl.dao.Imp;

import java.util.ArrayList;
import java.util.HashMap;

import com.yl.Global.PropValues;
import com.yl.Global.UserInfoMemoryCache;
import com.yl.dao.InitDao;
import com.yl.util.Utils;
import com.yl.entity.Campaign;
import com.yl.entity.UserInfo;

import com.yl.db.DataRow;
import com.yl.db.DbManager;

public class InitDaoImp implements InitDao
{
	private DbManager db;

	public InitDaoImp(DbManager db)
	{
		this.db = db;
	}

	public void init()
	{
		this.userInfoInit();
		this.memberInfoInit();
		this.leaderInfoInit();
	}

	public void leaderInfoInit()
	{
		String leadlist = "select u.uid as uid,c.luid as luid from userinfo u join campaign c on u.uid = c.muid";
		ArrayList<?> queryRes = db.executeQuery(leadlist);
		if (queryRes != null && queryRes.size() > 0)
		{
			for (int i = 0; i < queryRes.size(); i++)
			{
				DataRow row = (DataRow) queryRes.get(i);
				String luid = row.getItem("luid");
				UserInfoMemoryCache.leadInfo.put(row.getItem("uid"), luid);
			}
		}
	}

	public void memberInfoInit()
	{
		String memblist = "select u.uid as uid,c.muid as muid,c.timemillis as tm,c.tributebet as tbet,c.luid as luid from userinfo u join campaign c on u.uid = c.luid";
		ArrayList<?> queryRes = db.executeQuery(memblist);
		if (queryRes != null && queryRes.size() > 0)
		{
			for (int i = 0; i < queryRes.size(); i++)
			{
				DataRow row = (DataRow) queryRes.get(i);
				String uid = row.getItem("uid");

				if (UserInfoMemoryCache.memberInfo.containsKey(uid))
				{
					HashMap<String, Campaign> mmap = UserInfoMemoryCache.memberInfo
							.get(uid);
					Campaign cp = new Campaign();
					cp.setLuid(row.getItem("luid"));
					cp.setMuid(row.getItem("muid"));
					cp.setTimemillis(row.getItem("tm"));
					cp.setTributebet(Integer.parseInt(row.getItem("tbet")));
					mmap.put(row.getItem("muid"), cp);
				}
				else
				{
					String muid = row.getItem("muid");
					HashMap<String, Campaign> hm = new HashMap<String, Campaign>();
					Campaign cp = new Campaign();
					cp.setLuid(row.getItem("luid"));
					cp.setMuid(muid);
					cp.setTimemillis(row.getItem("tm"));
					cp.setTributebet(Integer.parseInt(row.getItem("tbet")));
					hm.put(muid, cp);
					UserInfoMemoryCache.memberInfo.put(row.getItem("uid"), hm);
				}
			}
		}
	}

	public void userInfoInit()
	{
		String ulist = "select uid,name,allmoney,pic,gold,exprience,zfmatchpoint,tzmatchpoint,cmatchpoint,showtype,functiontype,usecount from userinfo";
		ArrayList<?> queryRes = db.executeQuery(ulist);
		if (queryRes != null && queryRes.size() > 0)
		{
			for (int i = 0; i < queryRes.size(); i++)
			{
				DataRow row = (DataRow) queryRes.get(i);
				UserInfo uinfo = new UserInfo();
				uinfo.setUid(Integer.parseInt( row.getItem("uid") ));
				uinfo.setName(row.getItem("name"));
				uinfo.setAmoney(Integer.parseInt((row.getItem("allmoney"))));
				uinfo.setPic(row.getItem("pic"));
				uinfo.setGold(Integer.parseInt((row.getItem("gold"))));
				uinfo.setExprience(Integer.parseInt((row.getItem("exprience"))));
				uinfo.setBaceMPoint(Utils.getDateToLong());
				uinfo.setZfMatchPoint(Integer.parseInt((row.getItem("zfmatchpoint"))));
				uinfo.setTzMatchPoint(Integer.parseInt((row.getItem("tzmatchpoint"))));
				uinfo.setCMatchPoint(Integer.parseInt((row.getItem("cmatchpoint"))));

				HashMap<String, HashMap<String, Integer>> propmap = uinfo
						.getPropmap();
				String showType = row.getItem("showtype");
				String functionType = row.getItem("functiontype");
				int usecount = Integer.parseInt(row.getItem("usecount"));
				HashMap<String, Integer> showmap = new HashMap<String, Integer>();
				showmap.put(showType, Integer.parseInt(showType));
				HashMap<String, Integer> funmap = new HashMap<String, Integer>();
				if (!functionType.equals("0"))
				{
					funmap.put(functionType, usecount);
				}
				propmap.put(PropValues.Prop_MainType_A, showmap);
				propmap.put(PropValues.Prop_MainType_B, funmap);
				uinfo.setPropmap(propmap);

				UserInfoMemoryCache.addUserInfo(uinfo);
			}
		}
		// 获得成就的次数
		String cjsql = "select uid,count(uid) as num from achievement group by uid ";
		ArrayList<?> cjlist = db.executeQuery(cjsql);
		if (cjlist != null && cjlist.size() > 0)
		{
			for (int i = 0; i < cjlist.size(); i++)
			{
				DataRow row = (DataRow) cjlist.get(i);
				String uid = row.getItem("uid");
				String num = row.getItem("num");
				
				UserInfo userInfo = UserInfoMemoryCache.getUserInfo(uid);
				if(userInfo != null)
				{
					userInfo.setAchievement(Integer.parseInt(num));
				}

			}
		}
		// 赢得挑战的次数
		String wsql = "select zuid,count(zuid) as num from challenge where result ='win' group by zuid ";
		ArrayList<?> wlist = db.executeQuery(wsql);
		if (wlist != null && wlist.size() > 0)
		{
			for (int i = 0; i < wlist.size(); i++)
			{
				DataRow row = (DataRow) wlist.get(i);
				
				UserInfo userInfo = UserInfoMemoryCache.getUserInfo(row.getItem("zuid"));
				if(userInfo != null)
				{
					userInfo.setWinTzCount(Integer.parseInt(row.getItem("num")));
				}
			}
		}
		// 输挑战的次数
		String lsql = "select zuid,count(zuid) as num from challenge where result ='lost'  group by zuid ";
		ArrayList<?> llist = db.executeQuery(lsql);
		if (llist != null && llist.size() > 0)
		{
			for (int i = 0; i < llist.size(); i++)
			{
				DataRow row = (DataRow) llist.get(i);
				String zuid = row.getItem("zuid");
				UserInfoMemoryCache.getUserInfo(row.getItem("zuid")).setLostTzCount(Integer.parseInt(row.getItem("num")));
				
				if (UserInfoMemoryCache.isUserInfoOnline(zuid))
				{
					UserInfoMemoryCache.getUserInfo(zuid).setLostTzCount( Integer.parseInt(row.getItem("num")));
				}
			}
		}

	}

}
