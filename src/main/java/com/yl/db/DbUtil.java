package com.yl.db;

import java.sql.*;
import java.util.*;

import com.yl.Global.*;

public class DbUtil
{

	private Statement statement;
	private ResultSet resultSet;

	public DbUtil()
	{
		
	}

	public void displayResultSet(ResultSet rs) throws SQLException
	{
		// 定位到达第一条记录
		boolean moreRecords = rs.next();
		// 如果没有记录，则提示一条消息
		if (!moreRecords)
		{
			ConstList.config.logger.debug("无记录显示");
			return;
		}
		Vector<String> columnHeads = new Vector<String>();
		Vector<Vector<String>> rows = new Vector<Vector<String>>();
		try
		{
			// 获取字段的名称
			ResultSetMetaData rsmd = rs.getMetaData();
			for (int i = 1; i <= rsmd.getColumnCount(); ++i)
				columnHeads.addElement(rsmd.getColumnName(i));
			// 获取记录集
			do
			{
				rows.addElement(getNextRow(rs, rsmd));
			}
			while (rs.next());
			ShowSqlResult(columnHeads, rows);

		}
		catch (SQLException sqlex)
		{
			ConstList.config.logger.error("错误信息：" + sqlex.getMessage());
		}
	}

	public void ShowSqlResult(Vector<String> header, Vector<Vector<String>> rows)
	{
		for (int i = 0; i < rows.size(); i++)
		{
			for (int j = 0; j < header.size(); j++)
				ConstList.config.logger.info(header.get(j) + ":"
						+ ((Vector<String>) rows.get(i)).get(j));
		}
	}

	public Vector<String> getNextRow(ResultSet rs, ResultSetMetaData rsmd)
			throws SQLException
	{
		Vector<String> currentRow = new Vector<String>();
		for (int i = 1; i <= rsmd.getColumnCount(); ++i)
			currentRow.addElement(rs.getString(i));
		// 返回一条记录
		return currentRow;
	}

	public static String query = "select id,name,uid,roommoney,allmoney,sex,pic,online,address,status,"
			+ "regtime,birthday,counttime,logintime,gold,exprience,exittime,zfmatchpoint,"
			+ "tzmatchpoint,cmatchpoint,loginnum,requesturl,myads from userinfo where id <500";

}
