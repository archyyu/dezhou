package com.yl.thread;

import com.yl.container.*;
import ndb.DBServer;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.Logger;

import com.yl.Global.*;
import com.yl.vo.UserInfo;

public class MemberRemoveBind extends AbstractExtension implements Runnable
{
	public Thread mrbThread;
	public String handleName = "";
	
	private Logger log = Logger.getLogger(getClass());

	public MemberRemoveBind( String tName,String handleName)
	{
		mrbThread = new Thread(this);
		mrbThread.setName(tName);
		this.handleName = handleName;
	}

	public void run()
	{
		while (true)
		{
			
			UserInfo[] users = UserInfoMemoryCache.toUserInfos();
			if (mrbThread.getName().equals("write2Db"))
			{
				for (UserInfo uInfo : users)
				{
											
					if(uInfo.isSaveUpdate())
					{
						SqlSessionFactory	sqlMapper	= DBServer.getInstance().getSqlMapper();
						SqlSession session = sqlMapper.openSession();
						session.update("ndb.updateUserInfo", uInfo);
						session.commit();
						session.close();
						uInfo.setSaveUpdate(false);
					}
						
				}
				try
				{
					Thread.sleep(30);
				}
				catch (Exception e)
				{
					log.error("==write2Db=error====",e);
				}
			}
		}
	}


}
