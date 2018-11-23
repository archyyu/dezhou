package com.yl.db;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDriver;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;

public class PoolManager
{
	private static String driver = "org.gjt.mm.mysql.Driver";// 驱动
	private static String url = "jdbc:mysql://localhost:3306/dezhou?characterEncoding=utf8";// URL
	private static String Name = "root";// 用户名
	private static String Password = "root";// 密码

	private static Class driverClass = null;
	private static ObjectPool connectionPool = null;

	public PoolManager()
	{
	}

	/**
	 * 装配配置文件 initProperties
	 */
	private static void loadProperties()
	{
		try
		{
			java.io.InputStream stream = new java.io.FileInputStream(
					"res/config.properties");
			java.util.Properties props = new java.util.Properties();
			props.load(stream);

			driver = props.getProperty("driverClassName");
			url = props.getProperty("dburl");
			Name = props.getProperty("dbusrnm");
			Password = props.getProperty("dbpasswd");

		}
		catch (FileNotFoundException e)
		{
			System.out.println("读取配置文件异常");
		}
		catch (IOException ie)
		{
			System.out.println("读取配置文件时IO异常");
		}
	}

	/**
	 * 初始化数据源
	 */
	private static synchronized void initDataSource()
	{
		if (driverClass == null)
		{
			try
			{
				driverClass = Class.forName(driver);
			}
			catch (ClassNotFoundException e)
			{
				e.printStackTrace();
			}
		}
	}

	/**
	 * 连接池启动
	 * 
	 * @throws Exception
	 */
	public static void StartPool()
	{
		loadProperties();
		initDataSource();
		if (connectionPool != null)
		{
			ShutdownPool();
		}
		try
		{
			connectionPool = new GenericObjectPool(null);
			ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(
					url, Name, Password);
			PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(
					connectionFactory, connectionPool, null, null, false, true);
			Class.forName("org.apache.commons.dbcp.PoolingDriver");
			PoolingDriver driver = (PoolingDriver) DriverManager
					.getDriver("jdbc:apache:commons:dbcp:");
			driver.registerPool("dbpool", connectionPool);
			System.out.println("装配连接池OK");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 释放连接池
	 */
	public static void ShutdownPool()
	{
		try
		{
			PoolingDriver driver = (PoolingDriver) DriverManager
					.getDriver("jdbc:apache:commons:dbcp:");
			driver.closePool("dbpool");
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 取得连接池中的连接
	 * 
	 * @return
	 */
	public static Connection getConnection()
	{
		Connection conn = null;
		if (connectionPool == null)
			StartPool();
		try
		{
			conn = DriverManager
					.getConnection("jdbc:apache:commons:dbcp:dbpool");
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return conn;
	}

	/**
	 * 获取连接 getConnection
	 * 
	 * @param name
	 * @return
	 */
	public static Connection getConnection(String name)
	{
		return getConnection();
	}

	/**
	 * 释放连接 freeConnection
	 * 
	 * @param conn
	 */
	public static void freeConnection(Connection conn)
	{
		if (conn != null)
		{
			try
			{
				conn.close();
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
	}

	/**
	 * 释放连接 freeConnection
	 * 
	 * @param name
	 * @param con
	 */
	public static void freeConnection(String name, Connection con)
	{
		freeConnection(con);
	}

	/**
	 * 例子 main
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
		try
		{
			Connection conn = PoolManager.getConnection();
			if (conn != null)
			{
				Statement statement = conn.createStatement();
				ResultSet rs = statement
						.executeQuery("select * from userinfo limit 10");
				int c = rs.getMetaData().getColumnCount();
				while (rs.next())
				{
					System.out.println();
					for (int i = 1; i <= c; i++)
					{
						System.out.print(rs.getObject(i));
					}
				}
				rs.close();
				statement.close();
			}
			PoolManager.freeConnection(conn);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

	}

}
