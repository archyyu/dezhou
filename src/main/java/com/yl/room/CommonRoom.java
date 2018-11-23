package com.yl.room;

import com.yl.Global.UserModule;
import com.yl.container.User;

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
