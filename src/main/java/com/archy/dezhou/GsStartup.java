package com.archy.dezhou;

import com.archy.dezhou.global.ConstList;
import com.archy.dezhou.global.UserModule;
import com.archy.dezhou.backlet.BackletKit;
import com.archy.dezhou.global.UserService;
import com.archy.dezhou.netty.HttpServerKit;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.net.URL;
import java.io.InputStream;

/**
 *@author archy_yu 
 **/

public class GsStartup
{
	private static Logger log = Logger.getLogger(GsStartup.class);


	public static void main(String[] args)
	{
		GsStartup.startUp();
	}

	
	public static void startUp()
	{
		
		try
		{

			ClassLoader classLoader = GsStartup.class.getClassLoader();
			URL roomRes = classLoader.getResource("room.json");
			URL httpRes = classLoader.getResource("server.json");
			URL logRes = classLoader.getResource("log4j.properties");

			PropertyConfigurator.configure(logRes.getPath());

			UserModule.getInstance().init(roomRes.getPath());
			log.warn("UserModule init ok");

			HttpServerKit.initConfig(httpRes.getPath());
			log.warn("Http Server init ok");

			
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
