package com.yl.service;

import com.yl.entity.Prop;
import com.yl.entity.UserInfo;

public interface DaoJuService
{
	public int check(UserInfo muinfo, UserInfo fuinfo, Prop prop, int num);

	public void buyDJ(UserInfo muinfo, UserInfo fuinfo, Prop prop, int num);

	public void sendGift(UserInfo uinfo, Prop prop, int num, String ruid);

}
