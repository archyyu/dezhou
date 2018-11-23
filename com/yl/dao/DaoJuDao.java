package com.yl.dao;

import java.util.List;

import com.yl.web.po.DaoJu;

public interface DaoJuDao
{
	public void saveDJ(String uid, int dtype, int num, int expireday);

	public void saveUseDJ(String suid, String ruid, int dtype, int usecount,
			String expireDate);

	public void savePS(String muid, String suid, int dtype, int num,
			int expireday);

	public String updateDaoJu(String suid, int dtype, int id, String updateType);

	public String updateGift(String suid, int dtype, int id, String updateType);

	public String deleteDaoJu(String suid, int id);

	public String deleteGift(String suid, int id);

	public List<DaoJu> queryMyProp(String uid, String queryType);

	public List<DaoJu> queryMyGift(String uid, String queryType);
}
