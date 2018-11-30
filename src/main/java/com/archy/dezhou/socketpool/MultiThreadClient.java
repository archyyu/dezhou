package com.archy.dezhou.socketpool;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

import com.archy.dezhou.Global.ConstList;

public class MultiThreadClient
{
	public String RequestStr = "";
	public String ResponeseStr = "";
	public static Socket socket = null;
	public static boolean readFlag = true;
	public static boolean writeFlag = true;

	public static void main(String[] args)
	{

	}

}

// ********************************************************************//
class myClientRead extends Thread
{
	private DataInputStream dis;

	public myClientRead(DataInputStream dis)
	{
		this.dis = dis;
	}

	public void run()
	{
		String info = "";
		try
		{
			while (MultiThreadClient.readFlag)
			{
				try
				{
					if (dis != null)
						info = dis.readUTF();
					ConstList.config.logger.info("你说:" + info);
					if (info.equals("bye"))
					{
						ConstList.config.logger.info("你下线了,程序退出");
						System.exit(0);
					}
				}
				catch (Exception ex)
				{
					if (dis != null)
					{
						dis.close();
						dis = null;
					}
					if (MultiThreadClient.socket != null)
					{
						MultiThreadClient.socket.close();
						MultiThreadClient.socket = null;
					}
					MultiThreadClient.readFlag = false;
				}
				finally
				{

				}

			}
		}
		catch (Exception e)
		{
			ConstList.config.logger.info(e.getMessage());
			e.printStackTrace();
		}

	}
}

// ********************************************************************//

class myClientWrite extends Thread
{
	private DataOutputStream dos;

	public myClientWrite(DataOutputStream dos)
	{
		this.dos = dos;
	}

	public void run()
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String message = "";
		try
		{
			while (MultiThreadClient.writeFlag)
			{

				try
				{
					if (br != null)
						message = br.readLine();
					dos.writeUTF(message);
					if (message.equals("bye"))
					{
						ConstList.config.logger.info("我下线,程序退出");
						System.exit(0);
					}
				}
				catch (Exception ex)
				{
					if (dos != null)
					{
						dos.close();
						dos = null;
					}
					if (MultiThreadClient.socket != null)
					{
						MultiThreadClient.socket.close();
						MultiThreadClient.socket = null;
					}
					MultiThreadClient.writeFlag = false;
				}
				finally
				{

				}

			}
		}
		catch (Exception e)
		{
			ConstList.config.logger.error(e.getMessage());
		}

	}

}