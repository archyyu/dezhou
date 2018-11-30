package com.archy.dezhou.xload;

/**
 *@author archy_yu 
 **/

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.apache.log4j.Logger;

public class XLoad
{
	
	private static Logger log = Logger.getLogger(XLoad.class);
	
	public static byte[] getResource(String path)
	{
		RandomAccessFile file = null;
		try 
		{
			file = new RandomAccessFile(path, "r");
			byte[] bytes = new byte[(int)file.length()];
			file.read(bytes);
			return bytes;
		} 
		catch (FileNotFoundException e) 
		{
		   log.error(path + " not exist", e);
		} 
		catch (IOException e)
		{
			log.error(path + " read error", e);
		}
		finally
		{
			try
			{
				file.close();
			}
			catch (Exception e)
			{
				log.error("file close error", e);
			}
		}
		return null;
	}
	
}
