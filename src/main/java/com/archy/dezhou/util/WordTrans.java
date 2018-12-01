package com.archy.dezhou.util;

import com.archy.dezhou.global.ConstList;

/**
 * 将字符串编码成16进制数字,适用于所有字符（包括中文）
 */
public class WordTrans
{

	public static String encode(String str)
	{
		ConstList.config.logger.debug("根据默认编码获取字节数组");
		byte[] bytes = str.getBytes();
		StringBuilder sb = new StringBuilder(bytes.length * 2);
		ConstList.config.logger.debug("将字节数组中每个字节拆解成2位16进制整数");
		for (int i = 0; i < bytes.length; i++)
		{
			sb.append(hexString.charAt((bytes[i] & 0xf0) >> 4));
			sb.append(hexString.charAt((bytes[i] & 0x0f) >> 0));
		}
		return sb.toString();
	}

	public static String decode(String hexStr)
	{
		if (null == hexStr || "".equals(hexStr) || (hexStr.length()) % 2 != 0)
		{
			return null;
		}

		int byteLength = hexStr.length() / 2;
		byte[] bytes = new byte[byteLength];

		int temp = 0;
		for (int i = 0; i < byteLength; i++)
		{
			temp = hex2Dec(hexStr.charAt(2 * i)) * 16
					+ hex2Dec(hexStr.charAt(2 * i + 1));
			bytes[i] = (byte) (temp < 128 ? temp : temp - 256);
		}
		return new String(bytes);

	}

	private static String hexString = "0123456789ABCDEF";

	private static int hex2Dec(char ch)
	{
		if (ch == '0')
			return 0;
		if (ch == '1')
			return 1;
		if (ch == '2')
			return 2;
		if (ch == '3')
			return 3;
		if (ch == '4')
			return 4;
		if (ch == '5')
			return 5;
		if (ch == '6')
			return 6;
		if (ch == '7')
			return 7;
		if (ch == '8')
			return 8;
		if (ch == '9')
			return 9;
		if (ch == 'a')
			return 10;
		if (ch == 'A')
			return 10;
		if (ch == 'B')
			return 11;
		if (ch == 'b')
			return 11;
		if (ch == 'C')
			return 12;
		if (ch == 'c')
			return 12;
		if (ch == 'D')
			return 13;
		if (ch == 'd')
			return 13;
		if (ch == 'E')
			return 14;
		if (ch == 'e')
			return 14;
		if (ch == 'F')
			return 15;
		if (ch == 'f')
			return 15;
		else
			return -1;

	}

	public static void main(String[] args) {
		String tmpHexString = encode(new String("java  极限编程"));
		ConstList.config.logger.debug(encode(new String("java  极限编程")));
		ConstList.config.logger.debug(decode(tmpHexString));
	}
}
