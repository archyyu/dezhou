package com.archy.dezhou.container;

import com.archy.dezhou.util.json.JSONObject;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @author Lapo
 */
public class SFSObject
{
	private Map<String, Object> objData;

	/**
	 * Default constructor
	 * 
	 */
	public SFSObject()
	{
		objData = new HashMap<String, Object>();
	}

	/**
	 * Constructs the ActionscriptObject populating it with the passed
	 * collection
	 * 
	 * @param collection
	 *            a Collection
	 */
	public SFSObject(Collection<?> collection)
	{
		this();
		putCollection(this, "", collection);
	}

	/**
	 * Constructs the ActionscriptObject populating it with the passed map
	 * 
	 * @param map
	 *            a Map
	 */
	public SFSObject(Map<String, Object> map)
	{
		this();
		putMap(this, "", map);
	}

	/**
	 * Put an object with a String key (Associative Array)
	 * 
	 * @param key
	 *            the string key
	 * @param o
	 *            the object
	 */
	public void put(String key, Object o)
	{
		objData.put(key, o);
	}

	/**
	 * Put an object with a numeric key (Indexed Array)
	 * 
	 * @param key
	 *            the index key
	 * @param o
	 *            the object
	 */
	public void put(int key, Object o)
	{
		objData.put(String.valueOf(key), o);
	}

	/**
	 * Put a Number with a numeric key (Indexed Array)
	 * 
	 * @param key
	 *            the index key
	 * @param d
	 *            the number (treated as double)
	 */
	public void putNumber(int key, double d)
	{
		objData.put(String.valueOf(key), new Double(d));
	}

	/**
	 * Put a Number with a string key (Associative Array)
	 * 
	 * @param key
	 *            the string key
	 * @param d
	 *            the number (treated as double)
	 */
	public void putNumber(String key, double d)
	{
		objData.put(key, new Double(d));
	}

	/**
	 * Put a Boolean value with a numeric key (Indexed Array)
	 * 
	 * @param key
	 *            the index key
	 * @param b
	 *            the boolean
	 */
	public void putBool(int key, boolean b)
	{
		objData.put(String.valueOf(key), new Boolean(b));
	}

	/**
	 * Put a Boolean value with a string key (Indexed Array)
	 * 
	 * @param key
	 *            the string key
	 * @param b
	 *            the boolean
	 */
	public void putBool(String key, boolean b)
	{
		objData.put(key, new Boolean(b));
	}

	/**
	 * Get an object from a string key
	 * 
	 * @param key
	 *            the string key
	 * @return the object
	 */
	public Object get(String key)
	{
		return objData.get(key);
	}

	/**
	 * Get an object from an index key
	 * 
	 * @param key
	 *            the key
	 * @return the object
	 */
	public Object get(int key)
	{
		return objData.get(String.valueOf(key));
	}

	/**
	 * Get a String from and index key
	 * 
	 * @param key
	 *            the key
	 * @return the string
	 */
	public String getString(int key)
	{
		String s = (String) objData.get(String.valueOf(key));

		if (s == null)
			return "";
		else
			return s;
	}

	/**
	 * Get a String from a string key
	 * 
	 * @param key
	 *            the key
	 * @return the string
	 */
	public String getString(String key)
	{
		String s = (String) objData.get(key);
		if (s == null)
			return "";
		else
			return s;
	}

	/**
	 * Get a number from an index key
	 * 
	 * @param key
	 *            the key
	 * @return the number (as double)
	 */
	public double getNumber(int key)
	{
		Double var = (Double) objData.get(String.valueOf(key));

		if (var == null)
			return 0; // == NULL == Does this make any sense

		return var.doubleValue();
	}

	/**
	 * Get a number from a string key
	 * 
	 * @param key
	 *            the key
	 * @return the number (as double)
	 */
	public double getNumber(String key)
	{
		Double var = (Double) objData.get(key);

		if (var == null)
			return 0; // ??? == NULL == Does this make any sense ???

		return var.doubleValue();
	}

	/**
	 * Get a boolean from an index key
	 * 
	 * @param key
	 *            the key
	 * @return the boolean
	 */
	public boolean getBool(int key)
	{
		Boolean var = (Boolean) objData.get(String.valueOf(key));

		if (var == null)
			return false;

		return var.booleanValue();
	}

	/**
	 * Get a boolean from a String key
	 * 
	 * @param key
	 *            the key
	 * @return the boolean
	 */
	public boolean getBool(String key)
	{
		Boolean var = (Boolean) objData.get(key);

		if (var == null)
			return false;

		return var.booleanValue();
	}

	/**
	 * Get an ActionscriptObject from an index key
	 * 
	 * @param key
	 *            the index key
	 * @return the ActionscriptObject
	 */
	public SFSObject getObj(int key)
	{
		return (SFSObject) objData.get(String.valueOf(key));
	}

	/**
	 * Get an ActionscriptObject from a string key
	 * 
	 * @param key
	 *            the key
	 * @return the ActionscriptObject
	 */
	public SFSObject getObj(String key)
	{
		return (SFSObject) objData.get(key);
	}

	/**
	 * Get the current number of elements
	 * 
	 * @return the current size
	 */
	public int size()
	{
		return objData.size();
	}

	/**
	 * Get a Set of keys
	 * 
	 * @return the key set
	 */
	public Set<String> keySet()
	{
		return objData.keySet();
	}

	/**
	 * Remove an element
	 * 
	 * @param key
	 *            the index key
	 * @return the element removed
	 */
	public Object removeElement(int key)
	{
		return objData.remove(String.valueOf(key));
	}

	/**
	 * Remove an element
	 * 
	 * @param key
	 *            the string key
	 * @return the element removed
	 */
	public Object removeElement(String key)
	{
		return objData.remove(key);
	}

	/**
	 * Adds the content of a Collection, which may also include other
	 * collections or maps
	 * 
	 * @param key
	 *            the String key
	 * @param collection
	 *            the Collection
	 * 
	 * @since 1.6.0
	 */
	public void putCollection(String key, Collection<?> collection)
	{
		putCollection(new SFSObject(), key, collection);
	}

	/**
	 * Adds the content of a Collection, which may also include other
	 * collections or maps
	 * 
	 * @param key
	 *            the numeric key
	 * @param collection
	 *            the Collection
	 * 
	 * @since 1.6.0
	 */
	public void putCollection(int key, Collection<?> collection)
	{
		putCollection(String.valueOf(key), collection);
	}

	private void putCollection(SFSObject aObj, String key,
			Collection<?> collection)
	{
		Object collectionItem;
		int count = 0;

		// We're nesting a new object
		if (aObj != this)
			put(key, aObj);

		for (Iterator<?> it = collection.iterator(); it.hasNext();)
		{
			collectionItem = it.next();

			if (collectionItem instanceof Collection)
				aObj.putCollection(count, (Collection<?>) collectionItem);

			else if (collectionItem instanceof Map)
				aObj.putMap(count, (Map) collectionItem);

			else
				aObj.put(count, collectionItem);

			count++;
		}
	}

	/**
	 * Add the content of a Map, which can also contain other maps or
	 * collections
	 * 
	 * @param key
	 *            the String key
	 * @param map
	 *            the Map object
	 * 
	 * @since 1.6.0
	 */
	public void putMap(String key, Map map)
	{
		putMap(new SFSObject(), key, map);
	}

	/**
	 * Add the content of a Map, which can also contain other maps or
	 * collections
	 * 
	 * @param key
	 *            the numeric key
	 * @param map
	 *            the Map object
	 * 
	 * @since 1.6.0
	 */
	public void putMap(int key, Map map)
	{
		putMap(String.valueOf(key), map);
	}

	private void putMap(SFSObject aObj, String key, Map map)
	{
		Map.Entry entryItem;
		String itemKey;
		Object itemVal;

		// We're nesting a new object
		if (aObj != this)
			put(key, aObj);

		for (Iterator<?> it = map.entrySet().iterator(); it.hasNext();)
		{
			entryItem = (Map.Entry) it.next();
			itemKey = (String) entryItem.getKey();
			itemVal = entryItem.getValue();

			if (itemVal instanceof Collection)
				aObj.putCollection(itemKey, (Collection) itemVal);

			else if (itemVal instanceof Map)
				aObj.putMap(itemKey, (Map) itemVal);

			else
				aObj.put(itemKey, itemVal);
		}
	}

	@Override
	public String toString()
	{
		StringBuilder str = new StringBuilder();

		str.append("{ ");

		Iterator<?> i = objData.keySet().iterator();

		while (i.hasNext())
		{
			String key = (String) i.next();

			str.append(JSONObject.quote(key));
			str.append(" : ");

			Object o = objData.get(key);
			if (o == null)
			{
				str.append("null");
			}
			else if (o instanceof String)
			{
				str.append(JSONObject.quote((String) o));
			}
			else if (o instanceof String[])
			{
				str.append("[ ");
				for (String s : ((String[]) o))
				{
					str.append(JSONObject.quote(s));
					str.append(" , ");
				}
				if (str.lastIndexOf(" , ") == str.length() - 3)
				{
					str.delete(str.length() - 3, str.length());
				}
				str.append(" ]");
			}
			else if (o instanceof int[])
			{
				str.append("[ ");
				for (int n : ((int[]) o))
				{
					str.append(n);
					str.append(" , ");
				}
				if (str.lastIndexOf(" , ") == str.length() - 3)
				{
					str.delete(str.length() - 3, str.length());
				}
				str.append(" ]");
			}
			else if (o instanceof Map)
			{
				Map map = (Map) o;
				str.append("{ ");
				for (Object k : map.keySet())
				{
					str.append(JSONObject.quote(k.toString()));
					str.append(" : ");
					Object value = map.get(k);
					if (value instanceof String)
					{
						str.append(JSONObject.quote((String) value));
					}
					else
					{
						str.append(value.toString());
					}
					str.append(" , ");
				}
				if (str.lastIndexOf(" , ") == str.length() - 3)
				{
					str.delete(str.length() - 3, str.length());
				}
				str.append(" }");
			}
			else if (o instanceof List)
			{
				List list = (List) o;
				str.append("[ ");
				for (Object v : list)
				{
					if (v instanceof String)
					{
						str.append(JSONObject.quote((String) v));
					}
					else
					{
						str.append(v.toString());
					}
				}
				if (str.lastIndexOf(" , ") == str.length() - 3)
				{
					str.delete(str.length() - 3, str.length());
				}
				str.append(" ]");
			}
			else if (o instanceof Set)
			{
				Set set = (Set) o;
				str.append("[ ");
				for (Object v : set)
				{
					if (v instanceof String)
					{
						str.append(JSONObject.quote((String) v));
					}
					else
					{
						str.append(v.toString());
					}
				}
				if (str.lastIndexOf(" , ") == str.length() - 3)
				{
					str.delete(str.length() - 3, str.length());
				}
				str.append(" ]");
			}
			else
			{
				str.append(o.toString());
			}

			str.append(" , ");
		}

		System.out.println("str.length()=" + str.length());
		if (str.length() > 3 && str.lastIndexOf(" , ") == str.length() - 3)
		{
			str.delete(str.length() - 3, str.length());
		}
		str.append(" }");

		return str.toString();
	}
}