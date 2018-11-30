package com.archy.dezhou.util;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class Config
{

	public Logger logger;

	public Config()
	{
		logger = Logger.getLogger(Config.class.getName());
		PropertyConfigurator.configure("session/log/log4j.xml");
	}

}
