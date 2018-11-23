package com.yl.socketpool;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.util.concurrent.*;

import com.yl.Global.ConstList;

public class MultiThreadServer
{
	public static String RequestStr = "";
	public static String ResponeseStr = "";

//	private int port = Integer.parseInt(ConstList.HttpIp);
	private ServerSocket serverSocket;
	/**
	 * 线程池
	 */
	private ExecutorService executorService;
	/**
	 * 单个CPU线程池大小
	 */
	public static boolean ifReadFlag = true;
	public static boolean ifWriteFlag = true;

	private final int POOL_SIZE = ConstList.LinkPoolSize;

	public MultiThreadServer() throws IOException
	{
//		serverSocket = new ServerSocket(port);
		
		executorService = Executors.newFixedThreadPool(Runtime.getRuntime()
				.availableProcessors() * POOL_SIZE);
		ConstList.config.logger.info("服务器启动,当前的CPU为："
				+ Runtime.getRuntime().availableProcessors() + "个,总的连接数为："
				+ (Runtime.getRuntime().availableProcessors() * POOL_SIZE));
	}

	public void service()
	{
		while (true)
		{
			Socket socket = null;
			try
			{
				ConstList.config.logger
						.info("接收客户连接,只要客户进行了连接,就会触发accept();从而建立连接");
				socket = serverSocket.accept();
				executorService.execute(new Handler(socket));
			}
			catch (Exception e)
			{
				ConstList.config.logger.error(e.getMessage());
			}
		}
	}

	public static void main(String[] args) throws IOException
	{
//		new MultiThreadServer().service();
	}

	// ********************************************************************//
	class Handler implements Runnable
	{
		private Socket socket;

		public Handler(Socket socket)
		{
			this.socket = socket;
		}

		private DataOutputStream dos(Socket socket) throws IOException
		{
			OutputStream socketOut = socket.getOutputStream();
			return new DataOutputStream(socketOut);
		}

		private DataInputStream dis(Socket socket) throws IOException
		{
			InputStream socketIn = socket.getInputStream();
			return new DataInputStream(socketIn);
		}

		public void run()
		{
			try
			{
				ConstList.config.logger.info("New connection accepted "
						+ socket.getInetAddress() + ":" + socket.getPort());
				OutputStream os = null;
				DataOutputStream dos = null;
				InputStream is = null;
				DataInputStream dis = null;
				if (socket != null && !socket.isClosed()
						&& socket.isConnected())
				{
					os = socket.getOutputStream();
					dos = new DataOutputStream(os);
					is = socket.getInputStream();
					dis = new DataInputStream(is);
				}
				new myServerRead(dis).start();
				new myServerWrite(dos).start();
			}
			catch (IOException e)
			{
				ConstList.config.logger.error(e.getMessage());
			}
		}
	}

	// ********************************************************************//

	class myServerRead extends Thread
	{
		private DataInputStream dis;

		public myServerRead(DataInputStream dis)
		{
			this.dis = dis;
		}

		public void run()
		{
			String message = "";
			try
			{
				while (MultiThreadServer.ifReadFlag)
				{
					try
					{
						if (dis != null)
							message = dis.readUTF();
						System.out.println("你说：" + message);

						if (message.equals("bye"))
						{
							ConstList.config.logger.info("你下线,程序退出");
							System.exit(0);
						}
						else if (!message.equals(""))
						{
							MultiThreadServer.RequestStr = message;
						}
						ConstList.config.logger
								.info("myServerRead the RequestStr is "
										+ MultiThreadServer.RequestStr);
					}
					catch (Exception ex)
					{

					}
				}
			}
			catch (Exception e)
			{
				ConstList.config.logger.error(e.getMessage());
				e.printStackTrace();
			}

		}
	}

	// ********************************************************************//
	class myServerWrite extends Thread
	{
		private DataOutputStream dos;

		public myServerWrite(DataOutputStream dos)
		{
			this.dos = dos;
		}

		public void run()
		{
			String Message = "";
			ConstList.config.logger.debug("myServerWrite the RequestStr is "
					+ MultiThreadServer.RequestStr);

			try
			{
				while (MultiThreadServer.ifWriteFlag)
				{
					Message = MultiThreadServer.RequestStr;
					if (Message != null && !Message.equals(""))
					{
						if (Message.startsWith("begin", 0))
							Message = "我想说的是你现在很幸福！";
						else
							Message = "我响应一个信息给你！";
						dos.writeUTF(Message);
						if (Message.equals("bye"))
						{
							ConstList.config.logger.info("我下线,程序退出");
						}
						ConstList.config.logger
								.info("0--MultiThreadServer.RequestStr is "
										+ MultiThreadServer.RequestStr);
						MultiThreadServer.RequestStr = "";
					}
				}
			}
			catch (Exception e)
			{
				ConstList.config.logger.error(e.getMessage());
			}
		}
	}

}
