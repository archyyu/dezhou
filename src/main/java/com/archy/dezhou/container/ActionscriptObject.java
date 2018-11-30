package com.archy.dezhou.container;

import com.archy.dezhou.db.DataRow;
import java.util.*;

public class ActionscriptObject extends SFSObject
{

	private Map objData;

	public ActionscriptObject()
	{
		objData = new HashMap();
	}

	public ActionscriptObject(Collection collection)
	{
		this();
		putCollection(this, "", collection);
	}

	public ActionscriptObject(Map map)
	{
		this();
		putMap(this, "", map);
	}

	public ActionscriptObject(DataRow row)
	{
		this();
		putDataRow(this, "", row);
	}

	public void put(String key, Object o)
	{
		objData.put(key, o);
	}

	public void put(int key, Object o)
	{
		objData.put(String.valueOf(key), o);
	}

	public void putNumber(int key, double d)
	{
		objData.put(String.valueOf(key), new Double(d));
	}

	public void putNumber(String key, double d)
	{
		objData.put(key, new Double(d));
	}

	public void putBool(int key, boolean b)
	{
		objData.put(String.valueOf(key), new Boolean(b));
	}

	public void putBool(String key, boolean b)
	{
		objData.put(key, new Boolean(b));
	}

	public Object get(String key)
	{
		return objData.get(key);
	}

	public Object get(int key)
	{
		return objData.get(String.valueOf(key));
	}

	public String getString(int key)
	{
		String s = (String) objData.get(String.valueOf(key));
		if (s == null)
			return "";
		else
			return s;
	}

	public String getString(String key)
	{
		String s = (String) objData.get(key);
		if (s == null)
			return "";
		else
			return s;
	}

	public double getNumber(int key)
	{
		Double var = (Double) objData.get(String.valueOf(key));
		if (var == null)
			return 0.0D;
		else
			return var.doubleValue();
	}

	public double getNumber(String key)
	{
		Double var = (Double) objData.get(key);
		if (var == null)
			return 0.0D;
		else
			return var.doubleValue();
	}

	public boolean getBool(int key)
	{
		Boolean var = (Boolean) objData.get(String.valueOf(key));
		if (var == null)
			return false;
		else
			return var.booleanValue();
	}

	public boolean getBool(String key)
	{
		Boolean var = (Boolean) objData.get(key);
		if (var == null)
			return false;
		else
			return var.booleanValue();
	}

	public ActionscriptObject getObj(int key)
	{
		return (ActionscriptObject) objData.get(String.valueOf(key));
	}

	public ActionscriptObject getObj(String key)
	{
		return (ActionscriptObject) objData.get(key);
	}

	public int size()
	{
		return objData.size();
	}

	public Set keySet()
	{
		return objData.keySet();
	}

	public Object removeElement(int key)
	{
		return objData.remove(String.valueOf(key));
	}

	public Object removeElement(String key)
	{
		return objData.remove(key);
	}

	public void putCollection(String key, Collection collection)
	{
		putCollection(new ActionscriptObject(), key, collection);
	}

	public void putCollection(int key, Collection collection)
	{
		putCollection(String.valueOf(key), collection);
	}

	private void putCollection(ActionscriptObject aObj, String key,
			Collection collection)
	{
		int count = 0;
		if (aObj != this)
			put(key, aObj);
		for (Iterator it = collection.iterator(); it.hasNext();)
		{
			Object collectionItem = it.next();
			if (collectionItem instanceof Collection)
				aObj.putCollection(count, (Collection) collectionItem);
			else if (collectionItem instanceof Map)
				aObj.putMap(count, (Map) collectionItem);
			else if (collectionItem instanceof DataRow)
				aObj.putDataRow(count, (DataRow) collectionItem);
			else
				aObj.put(count, collectionItem);
			count++;
		}

	}

	public void putMap(String key, Map map)
	{
		putMap(new ActionscriptObject(), key, map);
	}

	public void putMap(int key, Map map)
	{
		putMap(String.valueOf(key), map);
	}

	private void putMap(ActionscriptObject aObj, String key, Map map)
	{
		if (aObj != this)
			put(key, aObj);
		for (Iterator it = map.entrySet().iterator(); it.hasNext();)
		{
			java.util.Map.Entry entryItem = (java.util.Map.Entry) it.next();
			String itemKey = (String) entryItem.getKey();
			Object itemVal = entryItem.getValue();
			if (itemVal instanceof Collection)
				aObj.putCollection(itemKey, (Collection) itemVal);
			else if (itemVal instanceof Map)
				aObj.putMap(itemKey, (Map) itemVal);
			else if (itemVal instanceof DataRow)
				aObj.putDataRow(itemKey, (DataRow) itemVal);
			else
				aObj.put(itemKey, itemVal);
		}

	}

	public void putDataRow(String key, DataRow row)
	{
		putDataRow(new ActionscriptObject(), key, row);
	}

	public void putDataRow(int key, DataRow row)
	{
		putDataRow(String.valueOf(key), row);
	}

	private void putDataRow(ActionscriptObject aObj, String key, DataRow row)
	{
		if (row.getType() == 0)
			putCollection(aObj, key, row.getDataAsList());
		else
			putMap(aObj, key, row.getDataAsMap());
	}
}
