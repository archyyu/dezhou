package com.archy.dezhou.entity.room;

import com.archy.dezhou.global.UserModule;
import com.archy.dezhou.container.User;

public class CommonRoom
{
	public void beatHeart(long now)
	{
		User[] users = UserModule.getInstance().userToArray();
		for(User user : users)
		{
			if(user.isOffLine())
			{
				UserModule.getInstance().UserLogout(user.getUserId());
			}
		}
	}
	
}
