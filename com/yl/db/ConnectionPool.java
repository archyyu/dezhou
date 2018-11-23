package com.yl.db;

import java.sql.SQLException;
import com.mysql.jdbc.Connection;

/**
 * 数据库连接池接口类
 * 
 * @author Administrator
 * 
 */
public interface ConnectionPool
{

	/**
	 * 设置数据库连接信息
	 * 
	 * @param url
	 *            连接地址
	 * @param username
	 *            用户名
	 * @param password
	 *            密码
	 */
	public void setConnect(String url, String username, String password);

	/**
	 * 获得一个数据库连接
	 * 
	 * @throws SQLException
	 */
	public Connection getConnection() throws SQLException;

	/**
	 * 关闭一个数据库连接
	 * 
	 * @throws SQLException
	 */
	public void freeConnection(Connection conn) throws SQLException;

	/**
	 * 打印调试信息
	 */
	public void printDebug();

}
