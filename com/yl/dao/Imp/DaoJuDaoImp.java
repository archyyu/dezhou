package com.yl.dao.Imp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.sun.org.apache.bcel.internal.generic.Select;
import com.sun.xml.internal.bind.v2.model.core.ID;
import com.yl.Global.ConstList;
import com.yl.db.*;

import com.yl.dao.DaoJuDao;
import com.yl.web.po.DaoJu;

public class DaoJuDaoImp implements DaoJuDao
{
	private DbManager db;

	public DaoJuDaoImp(DbManager db)
	{
		this.db = db;
	}

	public void saveDJ(String uid, int dtype, int num, int expireday)
	{

		String sql = "insert into daoju(uid,dtype,num,addtime,isUse,expiredays) values('"
				+ uid
				+ "','"
				+ dtype
				+ "','"
				+ num
				+ "',now(),'no',"
				+ expireday + ")";
		db.executeCommand(sql);
	}

	public void savePS(String muid, String suid, int dtype, int num,
			int expireday)
	{
		String sql = "insert into gift(mid,gtype,num,sid,time,expireDate,isUse) values('"
				+ muid
				+ "','"
				+ dtype
				+ "','"
				+ num
				+ "','"
				+ suid
				+ "',now(),ADDDATE(now(), " + expireday + "),'no')";
		db.executeCommand(sql);
	}

	public void saveUseDJ(String suid, String ruid, int dtype, int usecount,
			String expireDate)
	{
		String sql = "insert into usedj(sid,dtype,rid,usecount,time,expireDate) values('"
				+ suid
				+ "','"
				+ dtype
				+ "','"
				+ ruid
				+ "','"
				+ usecount
				+ "',now(),'" + expireDate + "')";
		db.executeCommand(sql);
	}

	public String updateDaoJu(String uid, int dtype, int id, String updateType)
	{
		String rtnString = "";
		if (updateType == null || updateType.equals(""))
			updateType = "inUse";

		String sql = "";
		if (updateType.equals("inUse"))
			sql = "update daoju set isUse='yes',modifyDate=now(),useDate=now(),expireDate=ADDDATE(now(), expiredays) where uid='"
					+ uid
					+ "' and dtype='"
					+ dtype
					+ "' and id="
					+ id
					+ " and isUse='no'";
		else if (updateType.equals("delete"))
			sql = "delete from  daoju  where expireDate<now() and uid='" + uid
					+ "' and id=" + id;
		db.executeCommand(sql);
		return rtnString;
	}

	public String updateGift(String uid, int dtype, int id, String updateType)
	{
		String rtnString = "";
		if (updateType == null || updateType.equals(""))
			updateType = "inUse";
		String sql = "update gift set  isUse='no' where isUse='yes' and mid='"
				+ uid + "' and expireDate>now()";
		ConstList.config.logger.warn(sql);
		db.executeCommand(sql);

		if (id != -1)
		{
			if (updateType.equals("inUse"))
				sql = "update gift set isUse='yes' where mid='" + uid
						+ "' and gtype='" + dtype + "' and id=" + id
						+ " and isUse='no' and expireDate>now()";
			else if (updateType.equals("delete"))
				sql = "delete from  gift  where expireDate<now() and mid='"
						+ uid + "'  and id=" + id;
		}
		else
		{
			sql = "update gift set isUse='yes' where mid='" + uid
					+ "' and gtype='" + dtype
					+ "'  and isUse='no'  and expireDate>now() limit 1";
		}
		ConstList.config.logger.info("sql=" + sql);
		ConstList.config.logger.warn(sql);
		db.executeCommand(sql);

		return rtnString;
	}

	public List<DaoJu> queryMyGift(String uid, String queryType)
	{
		String sql = "select id,gtype,num,time,expireDate,isUse FROM gift where mid='"
				+ uid + "' and num>=1 and expireDate > now()";
		if (queryType.equals("inUse"))
			sql = "select id,gtype,num,time,expireDate,isUse FROM gift where mid='"
					+ uid
					+ "' and num>=1 and expireDate > now() and isUse='yes'";
		List<DaoJu> retList = new ArrayList<DaoJu>();
		ArrayList<?> glist = db.executeQuery(sql);
		if (glist != null && glist.size() > 0)
		{
			for (int i = 0; i < glist.size(); i++)
			{
				DataRow row = (DataRow) glist.get(i);
				DaoJu dj = new DaoJu();
				dj.setNum(Integer.parseInt(row.getItem("num")));
				dj.setType(row.getItem("gtype"));
				dj.setIsUse(row.getItem("isUse"));
				dj.setDjDbId(Integer.parseInt(row.getItem("id")));

				String expireDateStr = row.getItem("expireDate");
				String addDateStr = row.getItem("time");
				SimpleDateFormat formatDate = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				try
				{
					Date expireDate = formatDate.parse(expireDateStr);
					Date addDate = formatDate.parse(addDateStr);
					Calendar CD = Calendar.getInstance();

					CD.setTime(addDate);
					dj.setBuyTimecount(CD.getTimeInMillis());

					CD.setTime(expireDate);
					dj.setExpireTimecount(CD.getTimeInMillis());
				}
				catch (Exception ex)
				{

				}

				retList.add(dj);
			}
		}
		return retList;
	}

	public List<DaoJu> queryMyProp(String uid, String queryType)
	{
		String sql = "select id,dtype,num,addtime,expireDate,isUse,useDate,expiredays  FROM daoju where uid='"
				+ uid
				+ "' and num>=1  and (expireDate > now() or expireDate is null)";
		if (queryType.equals("inUse"))
			sql = "select id,dtype,num,addtime,expireDate,isUse,useDate,expiredays  FROM daoju where uid='"
					+ uid
					+ "' and num>=1  and (expireDate > now() or expireDate is null) and isUse='yes'";

		List<DaoJu> retList = new ArrayList<DaoJu>();
		ArrayList<?> djlist = db.executeQuery(sql);
		if (djlist != null && djlist.size() > 0)
		{
			for (int i = 0; i < djlist.size(); i++)
			{
				DataRow row = (DataRow) djlist.get(i);
				DaoJu dj = new DaoJu();
				dj.setNum(Integer.parseInt(row.getItem("num")));
				dj.setType(row.getItem("dtype"));
				String expireDateStr = row.getItem("expireDate");
				dj.setDjDbId(Integer.parseInt(row.getItem("id")));

				if (expireDateStr == null)
					expireDateStr = "-1";

				String isUse = row.getItem("isUse");
				ConstList.config.logger.info("当前道具的使用情况为isUse=" + isUse);

				if (isUse == null || isUse.equals(""))
				{
					isUse = "yes";
					ConstList.config.logger.info("当前道具的使用情况为null,缺省设置为yes");
				}

				String useDateStr = row.getItem("useDate");
				if (useDateStr == null)
					useDateStr = "-1";
				String expiredays = row.getItem("expiredays");

				String addDateStr = row.getItem("addtime");
				SimpleDateFormat formatDate = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				try
				{
					Calendar CD = Calendar.getInstance();

					if (!addDateStr.equals("-1"))
					{
						Date addDate = formatDate.parse(addDateStr);
						CD.setTime(addDate);
						dj.setBuyTimecount(CD.getTimeInMillis());
					}
					else
						dj.setBuyTimecount(-1);

					if (!expireDateStr.equals("-1"))
					{
						Date expireDate = formatDate.parse(expireDateStr);
						CD.setTime(expireDate);
						dj.setExpireTimecount(CD.getTimeInMillis());
					}
					else
						dj.setExpireTimecount(-1);

					if (!useDateStr.equals("-1"))
					{
						Date useDate = formatDate.parse(useDateStr);
						CD.setTime(useDate);
						dj.setUseTimecount(CD.getTimeInMillis());
					}
					else
						dj.setUseTimecount(-1);
					dj.setExpireDays(Integer.parseInt(expiredays));
					dj.setIsUse(isUse);

				}
				catch (Exception ex)
				{
					ex.printStackTrace();
				}
				retList.add(dj);
			}
		}
		return retList;
	}

	public String deleteDaoJu(String uid, int id)
	{

		String expireDateStr = "";
		String sql = "delete from daoju where mid='" + uid + "' and id=" + id
				+ " and expireDate<now()";
		db.executeCommand(sql);
		return expireDateStr;

	}

	public String deleteGift(String uid, int id)
	{

		String expireDateStr = "";
		String sql = "delete from gift where mid='" + uid + "' and id=" + id
				+ " and expireDate<now() ";
		db.executeCommand(sql);
		return expireDateStr;

	}
}
