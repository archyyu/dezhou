package com.yl.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletException;

public class PropReadUtils extends javax.servlet.http.HttpServlet implements
		javax.servlet.Servlet
{
	public static Properties pro;

	@Override
	public void init() throws ServletException
	{
		System.out.println("=====init()==============");
		pro();
	}

	public void pro()
	{
		pro = new Properties();
		InputStream in = null;
		try
		{
			in = getClass().getResourceAsStream("/interfaceUrl.properties");
			pro.load(in);
		}
		catch (FileNotFoundException ex)
		{
			System.out.println("读取属性文件--->失败！- 原因：文件路径错误或者文件不存在");
		}
		catch (IOException ex)
		{
			System.out.println("装载文件--->失败!");
		}
		finally
		{
			if (null != in)
			{
				try
				{
					in.close();
				}
				catch (Exception e)
				{
				}
			}
		}
	}
}
