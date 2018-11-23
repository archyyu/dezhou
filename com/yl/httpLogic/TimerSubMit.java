package com.yl.httpLogic;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Timer;
import java.util.TimerTask;


public class TimerSubMit
{

	public Timer timer;
	PrintWriter out;

	public TimerSubMit(int seconds, PrintWriter out)
	{
		timer = new Timer();
		this.out = out;
		/**
		 * 指定时间后执行的一次性动作: timer.schedule(new RemindTask(), seconds*1000);
		 * 指定时间和指定间隔时间重复执行的动作: timer.scheduleAtFixedRate(new RemindTask(),
		 * seconds*1000,1000);
		 */
		timer.scheduleAtFixedRate(new RemindTask(), seconds * 1000, 1000);
	}

	public class RemindTask extends TimerTask
	{
		public void run()
		{
			out.println("global is checking");
			out.flush();
		}
	}

	public static void main(String[] args) throws IOException
	{

		PrintWriter pw = new PrintWriter(System.out);
		TimerSubMit ts = new TimerSubMit(60, pw);

	}

}
