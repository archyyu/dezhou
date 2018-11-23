package com.yl.db;

import java.util.*;

public class DataRow
{

	public static final int DATAROW_INTKEY = 0;
	public static final int DATAROW_STRINGKEY = 1;
	private int type;
	private HashMap<String, String> hm;
	private ArrayList<String> al;

	public DataRow()
	{
		this(0);
	}

	public DataRow(int type)
	{
		this.type = type;
		if (type == 0)
			al = new ArrayList<String>();
		else
			hm = new HashMap<String, String>();
	}

	public void addItem(String s)
	{
		if (al != null)
			al.add(s);
	}

	public void addItem(String key, String s)
	{
		if (hm != null)
			hm.put(key, s);
	}

	public String getItem(int key)
	{
		String s = null;
		try
		{
			if (al != null)
				s = (String) al.get(key);
		}
		catch (IndexOutOfBoundsException indexoutofboundsexception)
		{
		}
		return s;
	}

	public String getItem(String key)
	{
		if (hm != null)
			return (String) hm.get(key);
		else
			return null;
	}

	public int getType()
	{
		return type;
	}

	public Map<String, String> getDataAsMap()
	{
		return hm;
	}

	public List<String> getDataAsList()
	{
		return al;
	}

	public Object getData()
	{
		Object o = null;
		if (type == 0)
			o = getDataAsList();
		else
			o = getDataAsMap();
		return o;
	}
}
