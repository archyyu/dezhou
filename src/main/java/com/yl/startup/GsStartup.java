package com.yl.startup;

import org.apache.log4j.Logger;

import com.yl.Global.ConstList;
import com.yl.Global.UserModule;
import com.yl.backlet.BackletKit;
import com.yl.httplink.WebService;
import com.yl.netty.HttpServerKit;

/**
 *@author archy_yu 
 **/

public class GsStartup
{
	private static Logger log = Logger.getLogger(GsStartup.class);
	
	public static void startUp()
	{
		
		try
		{
			ConstList.initDbConfig();
			log.warn("db config init ok");
			
			UserModule.getInstance().init();
			log.warn("UserModule init ok");
			
			WebService.init();
			log.warn("WebService init ok");
			
			HttpServerKit.initConfig("");
			log.warn("Http Server init ok");
			
			HttpServerKit.lanuchAllHttpServer();
			log.warn("http Server lanuch ok");
			
			BackletKit.init();
			log.warn("Backlet kit init ok");
			
			log.info("............................................");
			log.info("............................................");
			log.info("                               ");		
			log.info("          ◢█◣　　　　　　◢█◣ ");
			log.info("        ◢███　　　　　　███◣　");
			log.info("        ███◤　　　　　　◥███　　　　　  ");
			log.info("  　　　◥█◤　　　　　　　　◥█◤　　　　　◣  ");
			log.info("  　　◤　　　　　　　　　　　　　　◥　　　　█▍ ");
			log.info("  　▊　　 ◢██◣　　　◢██◣　　　█　　　█▋");
			log.info("  　▉　　 ██○█　　　█○██　　　  █▍　　  ██  ");
			log.info("  █　　 ◥██◤　　　◥██◤　　　█▌　　 ██▍  ");
			log.info("  　█◣　　　　　　　　　　　　　　◢█▋　◢██▎  ");
			log.info("   　██◣　　　　╰╯　　　　◢███▌◢███  ");
			log.info("  　███████◣　　◢███████████▊  ");
			log.info("  　◥█████████████████████▋  ");
			log.info("  　　◥██████◤　◥█████◤█████▍ ");
			log.info("  　　　◥█████　　　████◤　████◤ ");
			log.info("  　　　　◥████　　◢███◤　　▼▼▼▼ ");
			log.info("   　　　　　▼▼▼▼　◢███◤　　  ");
			log.info("  　　　　　　　　 　　▼▼▼▼ ");	
			log.info("............................................");
			log.info("............................................");
			log.warn("..............Gs server init ok.............");
			
		}
		catch (Exception e)
		{
			log.error("Gs start up init error", e);
		}
		return ;
	}
}
