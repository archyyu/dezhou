package com.yl.sockeManager;

import java.io.*;
import java.net.*;
import java.util.*;

import com.yl.Global.ConstList;
import com.yl.util.VectorIterator;

public class PooledConnectionHandler implements Runnable
{
	protected Socket connection;
	protected static List<Socket> pool = new LinkedList<Socket>();

	public PooledConnectionHandler()
	{
	}

	public void handleConnection()
	{
		try
		{
			PrintWriter streamWriter = new PrintWriter(
					connection.getOutputStream());

			BufferedReader streamReader = new BufferedReader(
					new InputStreamReader(connection.getInputStream()));
			String RequestStr = streamReader.readLine();
			BufferedReader response = new BufferedReader(new FileReader(
					RequestStr));
			String line = null;
			int i = 0;
			Vector<String> vector = new Vector<String>();
			while ((line = response.readLine()) != null)
			{
				streamWriter.println(line);
				vector.add(line);

				ConstList.config.logger.info("i = " + i + ",vector.size="
						+ vector.size());
				i++;
			}

			Iterator<Object> iter = new VectorIterator(vector);
			while (iter.hasNext())
			{
				String str = (String) iter.next();
				System.out.println(str);
			}

			response.close();
			streamWriter.close();
			streamReader.close();
		}
		catch (FileNotFoundException e)
		{
			ConstList.config.logger.error("file not be found!");
		}
		catch (IOException e)
		{
			ConstList.config.logger.error("io Exception error!error is :" + e);
		}
	}

	public static void processRequest(Socket requestToHandle)
	{
		synchronized (pool)
		{
			pool.add(pool.size(), requestToHandle);
			pool.notifyAll();
		}
	}

	public void run()
	{
		while (true)
		{
			synchronized (pool)
			{
				while (pool.isEmpty())
				{
					try
					{
						pool.wait();
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}
				connection = (Socket) pool.remove(0);
			}
			handleConnection();
		}
	}

	public Reader handResquest(String RequestStr)
	{

		String string = "";

		Reader reader = new StringReader(string);
		return reader;
	}

}
