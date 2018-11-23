package com.yl.dao.Imp;

import com.yl.db.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.yl.Global.*;
import com.yl.dao.CommonDao;
import com.yl.util.Utils;
import com.yl.vo.UserInfo;
import com.yl.web.po.DaoJu;
import com.yl.web.po.PKFriend;

public class CommonDaoImp implements CommonDao
{
	private DbManager db;

	public CommonDaoImp(DbManager db)
	{
		this.db = db;
	}

	/**
	 * 征服结束更新数据库
	 * 
	 * @param zworl
	 * @param zuid
	 * @param buid
	 * @param muid
	 * @param flag
	 * @return
	 */
	public void ZhengFuOver(String zworl, String zuid, String buid,
			String muid, String flag)
	{
		String sql = "";
		if (zworl.equals("win"))
		{
			if (flag.equals("removebind"))
			{
				sql = "delete from campaign where luid ='" + buid
						+ "' and muid '" + zuid + "'";
			}
			else if (flag.equals("challenge"))
			{
				sql = "insert into campaign(luid,muid,timemillis,time) values('"
						+ zuid
						+ "','"
						+ buid
						+ "','"
						+ Utils.retCurrentTiem()
						+ "',now())";
			}
			else if (flag.equals("fight"))
			{
				sql = "insert into campaign(luid,muid,timemillis,time) values('"
						+ zuid
						+ "','"
						+ muid
						+ "','"
						+ Utils.retCurrentTiem()
						+ "',now())";
			}
		}
		else
		{
			if (flag.equals("removebind"))
			{
				sql = "update campaign set timemillis='"
						+ Utils.retCurrentTiem() + "' ,time=now() where luid='"
						+ buid + "' and muid='" + zuid + "'";
			}
			else
			{
				sql = "insert into campaign(luid,muid,timemillis,time) values('"
						+ buid
						+ "','"
						+ zuid
						+ "','"
						+ Utils.retCurrentTiem()
						+ "',now())";
			}
		}
		db.executeCommand(sql);
	}

	/**
	 * 挑战结束更新数据库
	 * 
	 * @param ztm
	 * @param zexp
	 * @param zuid
	 * @param btm
	 * @param bexp
	 * @param buid
	 * @return
	 */
	public void tiaoZhanOver(UserInfo zuinfo, UserInfo buinfo, String zwol,
			int bet, int obet, int zexp, int bexp)
	{
		updateUinfo(zuinfo);
		updateUinfo(buinfo);
		String sql = "insert into challenge(zuid,buid,type,result,bet,obet,zexp,bexp,time) values('"
				+ zuinfo.getUid()
				+ "','"
				+ buinfo.getUid()
				+ "','tz','"
				+ zwol
				+ "','"
				+ bet
				+ "','"
				+ obet
				+ "','"
				+ zexp
				+ "','"
				+ bexp
				+ "',now())";
		db.executeCommand(sql);
	}

	/**
	 * 征服结束
	 * 
	 * @param zuinfo
	 * @param buinfo
	 * @param db
	 */
	public void zhenFuOver(UserInfo zuinfo, UserInfo buinfo, String wol)
	{
		updateUinfo(zuinfo);
		updateUinfo(buinfo);
		String sql = "insert into challenge(zuid,buid,type,result,time) values('"
				+ zuinfo.getUid()
				+ "','"
				+ buinfo.getUid()
				+ "','zf','"
				+ wol
				+ "',now())";
		db.executeCommand(sql);
	}

	/**
	 * 删除牌友
	 * 
	 * @param muid
	 * @param fuid
	 * @param db
	 */
	public void delPKFriend(String muid, String fuid)
	{
		String sql = "delete from pkfriend where mid in('" + muid + "','"
				+ fuid + "') and fid in ('" + muid + "','" + fuid + "')";
		db.executeCommand(sql);
	}

	/**
	 * 消息置顶
	 * 
	 * @param muid
	 * @param fuid
	 * @param db
	 */
	public void msgTop(String pg, String uid, int type)
	{
		String sql = "";
		if (pg.equals("p"))
		{
			sql = "update daoju set num = num-1 where uid='" + uid
					+ "' and dtype='" + type
					+ "' and num>=1 order by addtime asc limit 1";
		}
		else if (pg.equals("g"))
		{
			sql = "update gift set num = num-1 where mid='" + uid
					+ "'  and gtype='" + type
					+ "' and num>=1 order by time asc limit 1";
		}
		if (!sql.equals(""))
		{
			db.executeCommand(sql);
		}
	}

	/**
	 * 更新用户信息
	 * 
	 * @param uinfo
	 */
	public void updateUinfo(UserInfo uinfo)
	{
		if (uinfo.getAMoney() < 0 || uinfo.getGold() < 0)
		{
			return;
		}
		String sql = "update userinfo set allmoney='" + uinfo.getAMoney()
				+ "',exprience='" + uinfo.getExprience() + "',gold='"
				+ uinfo.getGold() + "',zfmatchpoint='"
				+ uinfo.getZfMatchPoint() + "',tzmatchpoint='"
				+ uinfo.getTzMatchPoint() + "',cmatchpoint='"
				+ uinfo.getCMatchPoint() + "' where uid='" + uinfo.getUid()
				+ "'";
		db.executeCommand(sql);
	}

	/**
	 * 失去连接更新
	 * 
	 * @param uinfo
	 * @param db
	 */
	public void updateUinfoLost(UserInfo uinfo)
	{
		HashMap<String, HashMap<String, Integer>> propmap = uinfo.getPropmap();
		if (uinfo.getAMoney() < 0 || uinfo.getGold() < 0)
		{
			return;
		}
		HashMap<String, Integer> smap = propmap.get(PropValues.Prop_MainType_A);
		HashMap<String, Integer> fmap = propmap.get(PropValues.Prop_MainType_B);
		int showType = 0;
		int funcType = 0;
		int useCount = 0;
		for (String key : smap.keySet())
		{
			showType = Integer.parseInt(key);
		}
		for (String key : fmap.keySet())
		{
			funcType = Integer.parseInt(key);
			useCount = fmap.get(key);
		}
		String addSql = "showtype='" + showType + "',functiontype='" + funcType
				+ "',usecount='" + useCount + "'";

		String sql = "update userinfo set "
				+ addSql
				+ ",online='N',allmoney='"
				+ uinfo.getAMoney()
				+ "',exprience='"
				+ uinfo.getExprience()
				+ "',gold='"
				+ uinfo.getGold()
				+ "',zfmatchpoint='"
				+ uinfo.getZfMatchPoint()
				+ "',tzmatchpoint='"
				+ uinfo.getTzMatchPoint()
				+ "',cmatchpoint='"
				+ uinfo.getCMatchPoint()
				+ "',exittime=now(),counttime=timestamp(counttime,timediff(now(),logintime)) where uid='"
				+ uinfo.getUid() + "'";
		db.executeCommand(sql);
	}

	public List<DaoJu> reflushAch(String uid)
	{
		String sql = "select ctype,num FROM achievement where uid='" + uid
				+ "'";
		List<DaoJu> retList = new ArrayList<DaoJu>();
		ArrayList<?> queryRes = db.executeQuery(sql);
		if (queryRes != null && queryRes.size() > 0)
		{
			for (int i = 0; i < queryRes.size(); i++)
			{
				DataRow row = (DataRow) queryRes.get(i);
				DaoJu dj = new DaoJu();
				dj.setType(row.getItem("ctype"));
				dj.setNum(Integer.parseInt(row.getItem("num")));
				retList.add(dj);
			}
		}
		return retList;
	}

	public void addPKFriend(UserInfo muinfo, UserInfo fuinfo)
	{
		String sql = "select count(0) as rcount from  pkfriend where mid='"
				+ muinfo.getUid() + "' and fid='" + fuinfo.getUid() + "'";
		ArrayList<?> queryRes = db.executeQuery(sql);
		System.out.println("sql=" + sql);
		if (((DataRow) queryRes.get(0)).getItem("rcount").equals("0"))
		{
			sql = "insert into pkfriend(mid,mname,mpic,fname,fid,fpic,addtime) values('"
					+ muinfo.getUid()
					+ "','"
					+ muinfo.getName()
					+ "','"
					+ muinfo.getPic()
					+ "','"
					+ fuinfo.getName()
					+ "','"
					+ fuinfo.getUid() + "','" + fuinfo.getPic() + "',now())";
			System.out.println("sql=" + sql);
			db.executeCommand(sql);
		}
		else
		{
			System.out.println("这个人已经是" + muinfo.getUid() + "的好友！");
		}
	}

	public void TimePlan(String uid, int time)
	{

	}

	public List<PKFriend> getPKFriends(String uid)
	{

		String sql = "select mid,mname,mpic,fid,fname,fpic FROM pkfriend where mid='"
				+ uid + "' or fid='" + uid + "'";
		List<PKFriend> retList = new ArrayList<PKFriend>();
		ArrayList<?> queryRes = db.executeQuery(sql);
		if (queryRes != null && queryRes.size() > 0)
		{
			for (int i = 0; i < queryRes.size(); i++)
			{
				DataRow row = (DataRow) queryRes.get(i);
				PKFriend pkf = new PKFriend();
				pkf.setMuid(row.getItem("mid"));
				pkf.setFuid(row.getItem("fid"));
				pkf.setFname(row.getItem("fname"));
				pkf.setMname(row.getItem("mname"));
				pkf.setMpic(row.getItem("mpic"));
				pkf.setFpic(row.getItem("fpic"));
				retList.add(pkf);
			}
		}
		return retList;
	}

	public void updateUserWhenLevel(UserInfo uinfo)
	{
		if (uinfo.getAMoney() < 0 || uinfo.getGold() < 0)
		{
			return;
		}
		String sql = "update userinfo set allmoney='" + uinfo.getAMoney()
				+ "',exprience='" + uinfo.getExprience() + "',gold='"
				+ uinfo.getGold() + "',zfmatchpoint='"
				+ uinfo.getZfMatchPoint() + "',tzmatchpoint='"
				+ uinfo.getTzMatchPoint() + "',cmatchpoint='"
				+ uinfo.getCMatchPoint() + "' where uid='" + uinfo.getUid()
				+ "'";
		db.executeCommand(sql);
	}

	public boolean checkAchievement(String uid, int type)
	{
		String sql = "select * from achievement where uid='" + uid
				+ "' and ctype='" + type + "'";
		ArrayList<?> checkach = db.executeQuery(sql);
		return checkach == null || checkach.size() <= 0 ? true : false;
	}

	public void insertAchievement(String uid, int type)
	{
		String sql = "insert into achievement(uid,ctype,num,gettime) values('"
				+ uid + "','" + type + "','1',now())";
		db.executeCommand(sql);
	}

	public void updateAchievement(String uid, int type)
	{
		String sql = "update achievement set num=num+1 where uid='" + uid
				+ "' and ctype='" + type + "'";
		db.executeCommand(sql);
	}

	public boolean delAchievement(String uid, int type)
	{
		boolean flag = true;
		try
		{
			ConstList.config.logger.warn("delAchievement");
			String sql = "delete from achievement  where uid='" + uid
					+ "' and ctype='" + type + "'";
			db.executeCommand(sql);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			flag = false;
		}
		return flag;
	}

	public void delCreditCard(String uid, int type)
	{

	}

}
