package com.archy.dezhou.db;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Driver;

/**
 * MySQL数据库连接池
 * 
 * @author 石莹
 * @since 1.0
 * 
 */
public class DbManager implements ConnectionPool
{

	private static final int FREE_AND_USED_RATIO = 1;

	// todo
	private static String m_url = "";
	private static String m_user = "";
	private static String m_password = "";

	private static LinkedList<Connection> freeConn = new LinkedList<Connection>();
	private static LinkedList<Connection> usedConn = new LinkedList<Connection>();

	private static Connection conn;

	static
	{
		// 当类加载时，自动加载并初始化JDBC驱动
		initDriver();
	}

	/** 加载JDBC驱动 */
	private static void initDriver()
	{
		Driver driver = null;
		try
		{
			driver = (Driver) Class.forName("com.mysql.jdbc.Driver")
					.newInstance();
			installDriver(driver);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/** 注册安装JDBC驱动 */
	private static void installDriver(Driver driver)
	{
		try
		{
			DriverManager.registerDriver(driver);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public synchronized Connection getConnection() throws SQLException
	{
		Connection conn = null;
		if (freeConn.isEmpty())
		{
			conn = (Connection) DriverManager.getConnection(m_url, m_user,
					m_password);
		}
		else
		{
			// 取出一个连接并且检查是否关闭，防止用户误操作调用了conn.close();
			while (true)
			{
				conn = freeConn.removeFirst();
				if (!conn.isClosed())
				{
					break;
				}
				else
				{
					if (!freeConn.isEmpty())
					{
						continue;
					}
					else
					{
						conn = (Connection) DriverManager.getConnection(m_url,
								m_user, m_password);
						break;
					}
				}
			}
		}
		usedConn.add(conn);
		return conn;
	}

	@Override
	public synchronized void freeConnection(Connection conn)
			throws SQLException
	{
		usedConn.remove(conn);
		if (freeConn.size() > usedConn.size() * FREE_AND_USED_RATIO)
		{
			conn.close();
		}
		else
		{
			freeConn.add(conn);
		}
	}

	/**
	 * 创建一个新的连接
	 * 
	 * @return 创建的连接
	 * @throws SQLException
	 */
	protected Connection getNewConnection() throws SQLException
	{
		Connection conn = (Connection) DriverManager.getConnection(m_url,
				m_user, m_password);
		return conn;
	}

	public String getUrl()
	{
		return m_url;
	}

	public void setUrl(String mUrl)
	{
		m_url = mUrl;
	}

	public String getUserName()
	{
		return m_user;
	}

	public void setUserName(String mUser)
	{
		m_user = mUser;
	}

	public String getPassword()
	{
		return m_password;
	}

	public void setPassword(String mPassword)
	{
		m_password = mPassword;
	}

	@Override
	public void setConnect(String url, String username, String password)
	{
		this.setUrl(url);
		this.setUserName(username);
		this.setPassword(password);
	}

	@Override
	public void printDebug()
	{
		System.out.println("Free conn num : " + freeConn.size()
				+ "\tUsed conn num : " + usedConn.size());
	}

	public ArrayList<Object> executeQuery(String cmd)
	{
		return executeQuery(cmd, 1);
	}

	public ArrayList<Object> executeQuery(String cmd, int keyType)
	{
		Statement stmt;
		ArrayList<Object> list;
		stmt = null;
		list = null;
		ResultSet rset = null;
		try
		{
			conn = getConnection();
			stmt = conn.createStatement();
			rset = stmt.executeQuery(cmd);
			list = buildListFromResult(rset, keyType);
		}
		catch (SQLException sqle)
		{
			sqle.printStackTrace();
		}

		try
		{
			freeConnection(conn);
		}
		catch (Exception exception2)
		{

		}

		return list;
	}

	public boolean executeCommand(String cmd, int type)
	{

		return executeCommand(cmd);
	}

	public boolean executeCommand(String cmd)
	{
		Statement stmt;
		boolean success;
		stmt = null;
		success = false;
		try
		{
			conn = getConnection();
			stmt = conn.createStatement();
			stmt.executeUpdate(cmd);
			success = true;
		}
		catch (SQLException sqle)
		{
			sqle.printStackTrace();
		}
		try
		{
			stmt.close();
			freeConnection(conn);
		}
		catch (Exception exception1)
		{
			exception1.printStackTrace();
		}

		return success;
	}

	private ArrayList<Object> buildListFromResult(ResultSet rs, int keyType)
			throws SQLException
	{
		ArrayList<Object> list = new ArrayList<Object>();
		ResultSetMetaData metaData = rs.getMetaData();
		int cols = metaData.getColumnCount();
		DataRow row;
		for (; rs.next(); list.add(row))
		{
			int c = 1;
			row = new DataRow(keyType);
			row.addItem("");
			for (; c <= cols; c++)
			{
				String colName = metaData.getColumnName(c);
				if (keyType == 0)
					row.addItem(rs.getString(c));
				else
					row.addItem(colName, rs.getString(c));
			}

		}
		return list;
	}
}