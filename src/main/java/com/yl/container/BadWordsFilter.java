package com.yl.container;

import java.util.*;

public class BadWordsFilter
{

	private BadWordsFilter()
	{
	}

	public static BadWordsFilter getInstance()
	{
		if (instance == null)
			instance = new BadWordsFilter();
		return instance;
	}

	public void initBadWordsList(LinkedList bwList)
	{
		badWords = new String[bwList.size()];
		int c = 0;
		for (Iterator i = bwList.iterator(); i.hasNext();)
			badWords[c++] = (String) i.next();

	}

	public boolean containsBadWords(String s)
	{
		boolean b = false;
		StringBuffer sb = stripPunctuation(s, new int[s.length()]);
		for (int i = 0; i < badWords.length; i++)
		{
			if (sb.indexOf(badWords[i]) == -1)
				continue;
			b = true;
			break;
		}

		return b;
	}

	public String filterWords(String s)
	{
		int pItems[] = new int[s.length()];
		StringBuffer sb = stripPunctuation(s, pItems);
		for (int i = 0; i < badWords.length; i++)
		{
			int p;
			do
			{
				p = sb.indexOf(badWords[i]);
				if (p != -1)
				{
					char base[] = new char[badWords[i].length()];
					Arrays.fill(base, 0, base.length, '*');
					String rep = new String(base);
					sb.replace(p, p + badWords[i].length(), rep);
				}
			}
			while (p != -1);
		}

		rebuildOriginal(sb, s, pItems);
		return sb.toString();
	}

	private void rebuildOriginal(StringBuffer clean, String original,
			int pItems[])
	{
		int len = original.length() - clean.length();
		boolean notLowerCase = false;
		for (int i = 0; i < len; i++)
		{
			int p = pItems[i];
			clean.insert(p, original.charAt(p));
		}

		for (int i = 0; i < original.length(); i++)
		{
			char c = original.charAt(i);
			notLowerCase = c < 'a' || c > 'z';
			if (notLowerCase && clean.charAt(i) != '*')
				clean.setCharAt(i, c);
		}

	}

	public StringBuffer stripPunctuation(String s, int items[])
	{
		StringBuffer sb = new StringBuffer(s.toLowerCase());
		StringBuffer res = new StringBuffer();
		int itemsC = 0;
		for (int i = 0; i < sb.length(); i++)
		{
			char c = sb.charAt(i);
			if (punctuation.indexOf(c) == -1)
				res.append(c);
			else
				items[itemsC++] = i;
		}

		return res;
	}

	public static BadWordsFilter instance;
	private static String badWords[];
	public static String punctuation = ",.;:_!\"$%&/()[]#*-+'?!";
	public static final int MODE_FILTER = 0;
	public static final int MODE_REMOVE = 1;

}
