package com.archy.dezhou.container;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.archy.dezhou.Global.ConstList;

public class MD5
{

	private MD5()
	{
		try
		{
			messageDigest = MessageDigest.getInstance("MD5");
		}
		catch (NoSuchAlgorithmException e)
		{
			ConstList.config.logger
					.info("Could not instantiate the MD5 Message Digest!");
		}
	}

	public static MD5 instance()
	{
		if (_instance == null)
			_instance = new MD5();
		return _instance;
	}

	public String getHash(String s)
	{
		byte data[] = s.getBytes();
		messageDigest.update(data);
		return toHexString(messageDigest.digest());
	}

	private String toHexString(byte byteData[])
	{
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < byteData.length; i++)
		{
			String hex = Integer.toHexString(byteData[i] & 0xff);
			if (hex.length() == 1)
				hex = (new StringBuilder("0")).append(hex).toString();
			sb.append(hex);
		}

		return sb.toString();
	}

	private static MD5 _instance;
	private MessageDigest messageDigest;
}
