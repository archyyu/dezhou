package com.yl.util;

import java.security.*;

import com.yl.Global.ConstList;

public class Md5Encode
{

	public final static String MD5(String s)
	{
		char hexDigits[] =
		{ '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
				'e', 'f' };
		try
		{
			byte[] strTemp = s.getBytes();
			MessageDigest mdTemp = MessageDigest.getInstance("MD5");
			mdTemp.update(strTemp);
			byte[] md = mdTemp.digest();
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++)
			{
				byte byte0 = md[i];
				str[k] = hexDigits[byte0 >>> 4 & 0xf];
				str[k] = hexDigits[byte0 & 0xf];
			}
			return new String(str);
		}
		catch (Exception e)
		{
			return null;
		}
	}

	public static void main(String[] args)
	{

		try
		{
			byte[] tmpbyteA = Md5Encode.MD5("xinyuan").getBytes();
			for (int i = 0; i < tmpbyteA.length; i++)
				System.out.print(tmpbyteA[i]);
			System.out.println();
		}
		catch (Exception ex)
		{
			ConstList.config.logger.error(ex.getMessage());
		}
	}
}
