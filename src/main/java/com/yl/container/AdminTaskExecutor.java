package com.yl.container;

import java.util.*;

public class AdminTaskExecutor extends TimerTask
{

	public AdminTaskExecutor()
	{
		taskList = new LinkedList();
	}

	public void addTask(ExecutableAdminTask eat)
	{
		synchronized (taskList)
		{
			taskList.add(eat);
		}
	}

	public void run()
	{
		if (taskList.size() > 0)
		{
			long now = System.currentTimeMillis();
			synchronized (taskList)
			{
				for (Iterator i = taskList.iterator(); i.hasNext();)
				{
					ExecutableAdminTask task = (ExecutableAdminTask) i.next();
					if (now >= task.getExecTime())
					{
						task.execute();
						if (task.isLooping())
							task.setExecTime(System.currentTimeMillis()
									+ task.getDelay());
						else
							i.remove();
					}
				}

			}
		}
	}

	private LinkedList taskList;
}
