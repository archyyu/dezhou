package com.yl.util;

import java.util.Iterator;
import java.util.Vector;

public class VectorIterator implements Iterator
{
	private Vector<Object> v;
	private int currentIndex = 0;

	public VectorIterator()
	{
	}

	public VectorIterator(Vector v)
	{
		this.v = v;
	}

	public boolean hasNext()
	{

		if (this.currentIndex < v.size())
		{
			System.out.println("current index is : " + this.currentIndex);
			return true;
		}
		else
		{
			System.out.println("out of the bound ");
			return false;
		}

	}

	public Object next()
	{
		return this.v.get(this.currentIndex++);
	}

	public void remove()
	{
		this.v.remove(this.currentIndex);
	}

	public static void main(String[] args)
	{
		Vector<Object> v = new Vector<Object>();
		v.add(new String("aaa"));
		v.add(new String("bbb"));
		v.add(new String("ccc"));
		Iterator<Object> iter = new VectorIterator(v);
		while (iter.hasNext())
		{
			String str = (String) iter.next();
			System.out.println(str);
		}
	}
}
