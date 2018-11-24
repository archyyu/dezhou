package com.yl.service.Imp;

import java.util.List;

import com.yl.Global.*;
import com.yl.dao.DaoJuDao;
import com.yl.dao.Imp.DaoJuDaoImp;
import com.yl.service.DaoJuService;
import com.yl.util.Utils;
import com.yl.vo.Prop;
import com.yl.vo.UserInfo;
import com.yl.container.AbstractExtension;

public class DaoJuServiceImp extends AbstractExtension implements DaoJuService
{
	private DaoJuDao djdao;

	public DaoJuServiceImp(DaoJuDaoImp djdao)
	{
		this.djdao = djdao;
	}

	public int check(UserInfo muinfo, UserInfo fuinfo, Prop prop, int num)
	{
		int retInfo = 0;
		boolean isLevelLimit = prop.getSecondaryType().equals(
				PropValues.Prop_Level_IsLimit) ? true : false;
		// 是否存在等级限制
		if (isLevelLimit)
		{
			String level = Utils.retLevelAndExp(muinfo.getExprience())[0] + "";
			if (Integer.parseInt(level) < Integer.parseInt(prop.getLevel()))
			{
				retInfo = PropValues.Buy_NotEn_Level;
			}
		}
		else
		{
			if (prop.getCurrency().equals(PropValues.Prop_Cur_Gold))
			{
				retInfo = muinfo.getGold() < prop.getAmount() * num ? PropValues.Buy_NotEn_Gold
						: 0;
			}
			else if (prop.getCurrency().equals(PropValues.Prop_Cur_Bet))
			{
				retInfo = muinfo.getAMoney() < prop.getAmount() * num ? PropValues.Buy_NotEn_Bet
						: 0;
			}
		}
		return retInfo;
	}

	public void buyDJ(UserInfo muinfo, UserInfo fuinfo, Prop prop, int num)
	{
		if (prop.getCurrency().equals(PropValues.Prop_Cur_Gold))
		{
			if (false)
				muinfo.setGold(muinfo.getGold() - prop.getAmount() * num);
		}
		else if (prop.getCurrency().equals(PropValues.Prop_Cur_Bet))
		{
			muinfo.setAmoney(muinfo.getAMoney() - prop.getAmount() * num);
		}
		int expireDays = prop.getExpireDay();
		if (prop.getMailType().equals("A"))// 礼物
		{
			djdao.savePS(fuinfo.getUid(), muinfo.getUid(),
					Integer.parseInt(prop.getType()), num, expireDays);
		}
		else if (prop.getMailType().equals("B"))// 道具
		{
			djdao.saveDJ(fuinfo.getUid(), Integer.parseInt(prop.getType()),
					num, expireDays);
		}

		System.out.println("prop.getMailType()=" + prop.getMailType()
				+ ",prop.getType()=" + prop.getType());

	}

	public void sendGift(UserInfo uinfo, Prop prop, int num, String ruid)
	{
		if (prop.getCurrency().equals(PropValues.Prop_Cur_Gold))
		{
			if (false)
				uinfo.setGold(uinfo.getGold() - prop.getAmount() * num);
		}
		else if (prop.getCurrency().equals(PropValues.Prop_Cur_Bet))
		{
			uinfo.setAmoney(uinfo.getAMoney() - prop.getAmount() * num);
		}
		int expireDays = prop.getExpireDay();
		djdao.savePS(ruid, uinfo.getUid(), Integer.parseInt(prop.getType()),
				num, expireDays);
	}

	private int retNumByDtype(int dtype)
	{
		int usecount = 0;
		if (dtype == 3)
		{
			usecount = 5;
		}
		else if (dtype == 4)
		{
			usecount = 10;
		}
		else if (dtype == 5)
		{
			usecount = 50;
		}
		return usecount;

	}

}
