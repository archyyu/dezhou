package com.archy.dezhou.sockeManager;

import java.io.*;
import java.net.*;
import java.util.Iterator;
import java.util.Vector;
import com.archy.dezhou.util.VectorIterator;

public class RemoteFileServer
{
	int listenPort;

	public RemoteFileServer(int listenPort)
	{
		this.listenPort = listenPort;
	}

	// 允许客户机连接到服务器,等待客户机请求
	public void acceptConnections()
	{
		try
		{
			ServerSocket server = new ServerSocket(listenPort, 100);
			Socket incomingConnection = null;
			while (true)
			{
				incomingConnection = server.accept();
				handleConnection(incomingConnection);
			}
		}
		catch (BindException e)
		{
			System.out.println("Unable to bind to port " + listenPort);
		}
		catch (IOException e)
		{
			System.out.println("Unable to instantiate a ServerSocket on port: "
					+ listenPort);
		}
	}

	// 与客户机Socket交互以将客户机所请求的文件的内容发送到客户机
	public void handleConnection(Socket incomingConnection)
	{
		try
		{
			InputStream inputFromSocket = incomingConnection.getInputStream();
			BufferedReader streamReader = new BufferedReader(
					new InputStreamReader(inputFromSocket));
			FileReader fileReader = new FileReader(new File(
					streamReader.readLine()));

			BufferedReader bufferedFileReader = new BufferedReader(fileReader);
			OutputStream outputToSocket = incomingConnection.getOutputStream();
			PrintWriter streamWriter = new PrintWriter(outputToSocket);
			String line = null;
			int i = 0;
			Vector<String> vector = new Vector<String>();
			while ((line = bufferedFileReader.readLine()) != null)
			{
				streamWriter.println(line);
				vector.add(line);

				System.out
						.println("i = " + i + ",vector.size=" + vector.size());
				i++;
			}

			Iterator<Object> iter = new VectorIterator(vector);
			while (iter.hasNext())
			{
				String str = (String) iter.next();
				System.out.println(str);
			}
			fileReader.close();
			streamWriter.close();
			streamReader.close();
		}
		catch (Exception e)
		{
			System.out.println("Error handling a client: " + e);
			e.printStackTrace();
		}
	}

	public static void main(String args[])
	{
		RemoteFileServer server = new RemoteFileServer(1001);
		server.acceptConnections();
	}
}