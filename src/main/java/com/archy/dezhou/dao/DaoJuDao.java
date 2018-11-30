package com.archy.dezhou.dao;

import java.util.List;


public interface DaoJuDao
{
	void saveDJ(String uid, int dtype, int num, int expireday);

	void saveUseDJ(String suid, String ruid, int dtype, int usecount,
                   String expireDate);

	void savePS(String muid, String suid, int dtype, int num,
                int expireday);

	String updateDaoJu(String suid, int dtype, int id, String updateType);

	String updateGift(String suid, int dtype, int id, String updateType);

	String deleteDaoJu(String suid, int id);

	String deleteGift(String suid, int id);
}
