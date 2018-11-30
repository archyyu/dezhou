package com.archy.dezhou.sockeManager;

import java.io.*;
import java.net.*;

import com.archy.dezhou.Global.ConstList;

public class MultithreadedRemoteFileServer
{
	int listenPort;

	public MultithreadedRemoteFileServer(int listenPort)
	{
		this.listenPort = listenPort;
	}

	// 允许客户机连接到服务器,等待客户机请求
	public void acceptConnections()
	{
		try
		{
			ServerSocket server = new ServerSocket(listenPort, 500);
			Socket incomingConnection = null;
			while (true)
			{
				incomingConnection = server.accept();
				handleConnection(incomingConnection);
			}
		}
		catch (BindException e)
		{
			ConstList.config.logger.error("Unable to bind to port "
					+ listenPort);
		}
		catch (IOException e)
		{
			ConstList.config.logger
					.error("Unable to instantiate a ServerSocket on port: "
							+ listenPort);
		}
	}

	// 与客户机Socket交互以将客户机所请求的文件的内容发送到客户机
	public void handleConnection(Socket connectionToHandle)
	{
		new Thread(new ConnectionHandler(connectionToHandle)).start();
	}

	public static void main(String args[])
	{
		MultithreadedRemoteFileServer server = new MultithreadedRemoteFileServer(
				1001);
		server.acceptConnections();
	}
}
