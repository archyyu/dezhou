package com.yl.ndb;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.log4j.Logger;

public class DBServer
{
	private Logger log	= Logger.getLogger(DBServer.class);

	// sql工厂
	private SqlSessionFactory	sqlMapper;

	private static DBServer server;

	private DBServer()
	{
		try
		{
			String resource = "session/config/db-config.xml";
			InputStream in = new FileInputStream(resource);
			sqlMapper = new SqlSessionFactoryBuilder().build(in);
			in.close();
		}
		catch(IOException e)
		{
			log.error(e,e);
		}
	}

	public static DBServer getInstance()
	{
		if(server == null)
		{
			server = new DBServer();
		}
		return server;
	}

	public SqlSessionFactory getSqlMapper()
	{
		return sqlMapper;
	}
}
