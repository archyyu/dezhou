package com.archy.dezhou;

import com.archy.dezhou.global.ConstList;
import com.archy.dezhou.global.UserModule;
import com.archy.dezhou.backlet.BackletKit;
import com.archy.dezhou.netty.HttpServerKit;
import org.apache.log4j.Logger;

/**
 *@author archy_yu 
 **/

public class GsStartup
{
	private static Logger log = Logger.getLogger(GsStartup.class);


	public static void main()
	{
		GsStartup.startUp();
	}

	
	public static void startUp()
	{
		
		try
		{
			ConstList.initDbConfig();
			log.warn("db config init ok");
			
			UserModule.getInstance().init();
			log.warn("UserModule init ok");

			HttpServerKit.initConfig();
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
