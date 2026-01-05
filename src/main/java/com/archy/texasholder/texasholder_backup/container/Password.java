package com.archy.texasholder.container;

import java.util.ArrayList;
import java.util.HashMap;

public class Password
{

	private Password()
	{
		for (int i = 0; i < noise_base.length; i++)
			noise.add(noise_base[i]);

		noise_base = null;
		decTable = new HashMap();
		decTable.put("0", new Integer(0));
		decTable.put("1", new Integer(1));
		decTable.put("2", new Integer(2));
		decTable.put("3", new Integer(3));
		decTable.put("4", new Integer(4));
		decTable.put("5", new Integer(5));
		decTable.put("6", new Integer(6));
		decTable.put("7", new Integer(7));
		decTable.put("8", new Integer(8));
		decTable.put("9", new Integer(9));
		decTable.put("A", new Integer(10));
		decTable.put("B", new Integer(11));
		decTable.put("C", new Integer(12));
		decTable.put("D", new Integer(13));
		decTable.put("E", new Integer(14));
		decTable.put("F", new Integer(15));
	}

	public static Password getInstance()
	{
		if (_instance == null)
			_instance = new Password();
		return _instance;
	}

	public String decodePassword(String hash)
	{
		int len = hash.length();
		StringBuffer sb = new StringBuffer();
		for (int p = 0; p < len; p += 2)
		{
			String hex = hash.substring(p, p + 2);
			if (!noise.contains(hex))
				sb.append((char) hexToDec(hex, true));
		}

		return sb.reverse().toString();
	}

	private int hexToDec(String hex, boolean reverse)
	{
		int hi;
		int lo;
		if (reverse)
		{
			lo = ((Integer) decTable.get(hex.substring(0, 1))).intValue();
			hi = ((Integer) decTable.get(hex.substring(1, 2))).intValue();
		}
		else
		{
			hi = ((Integer) decTable.get(hex.substring(0, 1))).intValue();
			lo = ((Integer) decTable.get(hex.substring(1, 2))).intValue();
		}
		return hi * 16 + lo;
	}

	static String secretKey = "57C62ECB573455569E38A0AD4756F7A90F070757253D8CFD7148AED03B7CF95B31AC720940C2E83C5F3BF08BBBA9A94AF75D029B4F";
	static String dividers[] =
	{ "0A", "77", "1D", "F0", "E9", "39", "ED", "A7", "C1", "EB", "B7", "1A" };
	static String conversions[] =
	{ "A0", "EA", "01", "1D", "D0", "A0", "CF", "D2", "09", "F0" };
	static String noise_base[] =
	{ "00", "10", "20", "30", "40", "50", "60", "70", "80", "90", "A0", "B0",
			"C0", "D0", "E0", "F0", "01", "11", "21", "31", "41", "51", "61",
			"71", "81", "91", "A1", "B1", "C1", "D1", "E1", "F1", "02", "12",
			"22", "32", "42", "52", "62", "72", "82", "92", "A2", "B2", "C2",
			"D2", "E2", "F2", "B7", "C7", "D7", "E7", "F7", "08", "18", "28",
			"38", "48", "58", "68", "78", "88", "98", "A8", "B8", "C8", "D8",
			"E8", "F8", "09", "19", "29", "39", "49", "59", "69", "79", "89",
			"99", "A9", "B9", "C9", "D9", "E9", "F9", "0A", "1A", "2A", "3A",
			"4A", "5A", "6A", "7A", "8A", "9A", "AA", "BA", "CA", "DA", "EA",
			"FA", "0B", "1B", "2B", "3B", "4B", "5B", "6B", "7B", "8B", "9B",
			"AB", "BB", "CB", "DB", "EB", "FB", "0C", "1C", "2C", "3C", "4C",
			"5C", "6C", "7C", "8C", "9C", "AC", "BC", "CC", "DC", "EC", "FC",
			"0D", "1D", "2D", "3D", "4D", "5D", "6D", "7D", "8D", "9D", "AD",
			"BD", "CD", "DD", "ED", "FD", "0E", "1E", "2E", "3E", "4E", "5E",
			"6E", "7E", "8E", "9E", "AE", "BE", "CE", "DE", "EE", "FE", "0F",
			"1F", "2F", "3F", "4F", "5F", "6F", "7F", "8F", "9F", "AF", "BF",
			"CF", "DF", "EF", "FF" };
	static String hexTable[] =
	{ "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D",
			"E", "F" };
	static ArrayList noise;
	static HashMap decTable;
	static int MAX_PW_CHARS = 20;
	public static Password _instance;

	static
	{
		noise = new ArrayList(noise_base.length);
	}
}
