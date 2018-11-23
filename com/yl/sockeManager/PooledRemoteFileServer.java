package com.yl.sockeManager;

import java.io.*;
import java.net.*;

import com.yl.Global.ConstList;

public class PooledRemoteFileServer
{
	protected int maxConnections;
	protected int listenPort;
	protected ServerSocket serverSocket;

	public PooledRemoteFileServer(int aListenPort, int maxConnections)
	{
		listenPort = aListenPort;
		this.maxConnections = maxConnections;
	}

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
			ConstList.config.logger.error("bind error!");
		}
		catch (IOException e)
		{
			ConstList.config.logger.error("error:" + listenPort);
		}
	}

	protected void handleConnection(Socket connectionToHandle)
	{
		PooledConnectionHandler.processRequest(connectionToHandle);
	}

	public void setUpHandlers()
	{
		for (int i = 0; i < maxConnections; i++)
		{
			PooledConnectionHandler currentHandler = new PooledConnectionHandler();
			new Thread(currentHandler, "Handler " + i).start();
		}
	}

	public static void main(String args[])
	{
		PooledRemoteFileServer server = new PooledRemoteFileServer(1001, 3000);
		server.setUpHandlers();
		server.acceptConnections();
	}
}