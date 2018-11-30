package com.archy.dezhou.dao.Imp;

import com.archy.dezhou.Global.ConstList;
import com.archy.dezhou.dao.DaoJuDao;
import com.archy.dezhou.db.DbManager;
import com.archy.dezhou.db.*;

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
