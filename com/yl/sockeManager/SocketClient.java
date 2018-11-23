package com.yl.sockeManager;

import java.io.*;
import java.net.*;

import com.yl.Global.ConstList;

public class SocketClient
{
	protected BufferedReader socketReader;
	protected PrintWriter socketWriter;
	protected String hostIp;
	protected int hostPort;

	// 构造方法
	public SocketClient(String hostIp, int hostPort)
	{
		this.hostIp = hostIp;
		this.hostPort = hostPort;
	}

	// 向服务器请求文件的内容
	public String getResPonse(String fileNameToGet)
	{
		StringBuffer responseLine = new StringBuffer();
		try
		{
			socketWriter.println(fileNameToGet);
			socketWriter.flush();
			String line = null;
			while ((line = socketReader.readLine()) != null)
			{
				responseLine.append(line + "\n");
			}
		}
		catch (IOException e)
		{
			ConstList.config.logger.error("Error reading from file: "
					+ fileNameToGet);
		}
		return responseLine.toString();
	}

	// 连接到远程服务器
	public void setUpConnection()
	{
		try
		{
			Socket client = new Socket(hostIp, hostPort);
			socketReader = new BufferedReader(new InputStreamReader(
					client.getInputStream()));
			socketWriter = new PrintWriter(client.getOutputStream());
		}
		catch (UnknownHostException e)
		{
			ConstList.config.logger
					.error("Error1 setting up socket connection: unknown host at "
							+ hostIp + ":" + hostPort);
		}
		catch (IOException e)
		{
			ConstList.config.logger
					.error("Error2 setting up socket connection: " + e);
		}
	}

	// 断开远程服务器
	public void tearDownConnection()
	{
		try
		{
			socketWriter.close();
			socketReader.close();
		}
		catch (IOException e)
		{
			ConstList.config.logger
					.error("Error tearing down socket connection: " + e);
		}
	}

	public static void main(String args[])
	{
		SocketClient sc = new SocketClient("127.0.0.1", 1001);
		sc.setUpConnection();
		StringBuffer response = new StringBuffer();
		String requestStr = "res/log4j.properties";
		response.append(sc.getResPonse(requestStr));
		sc.tearDownConnection();
		System.out.println(response);
	}
}
