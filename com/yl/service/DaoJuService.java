package com.yl.service;

import java.util.List;

import com.yl.vo.Prop;
import com.yl.vo.UserInfo;
import com.yl.web.po.DaoJu;

public interface DaoJuService
{
	public int check(UserInfo muinfo, UserInfo fuinfo, Prop prop, int num);

	public void buyDJ(UserInfo muinfo, UserInfo fuinfo, Prop prop, int num);

	public void sendGift(UserInfo uinfo, Prop prop, int num, String ruid);

	public List<DaoJu> getDaoJu(String uid, String queryType);

	public List<DaoJu> getGift(String uid, String queryType);
}
