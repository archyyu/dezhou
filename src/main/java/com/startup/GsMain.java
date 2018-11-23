package com.startup;

import com.yl.httplink.WebService;

/**
 *@author archy_yu 
 **/

public class GsMain
{
	public static void main(String args[])
	{
		//设置是否是debug模式
		if(args[0].equals("true"))
		{
			WebService.setDebugMode();
		}
		
		GsStartup.startUp();
	}
}
