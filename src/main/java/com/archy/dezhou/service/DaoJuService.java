package com.archy.dezhou.service;

import com.archy.dezhou.entity.Prop;
import com.archy.dezhou.entity.UserInfo;

public interface DaoJuService
{
	int check(UserInfo muinfo, UserInfo fuinfo, Prop prop, int num);

	void buyDJ(UserInfo muinfo, UserInfo fuinfo, Prop prop, int num);

	void sendGift(UserInfo uinfo, Prop prop, int num, String ruid);

}
