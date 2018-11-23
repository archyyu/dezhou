/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.yl.container;

import java.util.HashMap;
import java.util.Map;

/*
 *
 * @author Lapo
 */
public class Entities
{
	private static final Map<Character, String> ascTab = new HashMap<Character, String>();
	private static final Map<String, String> ascTabRev = new HashMap<String, String>();
	private static final Map<String, String> hexTable = new HashMap<String, String>();

	static
	{
		ascTab.put((Character) '>', "&gt;");
		ascTab.put((Character) '<', "&lt;");
		ascTab.put((Character) '&', "&amp;");
		ascTab.put((Character) '\'', "&apos;");
		ascTab.put((Character) '"', "&quot;");

		ascTabRev.put("&gt;", ">");
		ascTabRev.put("&lt;", "<");
		ascTabRev.put("&amp;", "&");
		ascTabRev.put("&apos;", "'");
		ascTabRev.put("&quot;", "\"");

		/*
		 * int count = 0; for (String key : new String[]{"0", "1", "2", "3",
		 * "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"}) {
		 * hexTable.put(key, String.valueOf(count++)); }
		 */
	}

	public static String encodeEntities(String st)
	{
		StringBuilder sb = new StringBuilder();
		// char codes < 32 are ignored except for tab,lf,cr

		for (int i = 0; i < st.length(); i++)
		{
			char ch = st.charAt(i);

			if (ch == 9 || ch == 10 || ch == 13)
				sb.append(ch);

			else if (ch >= 32 && ch <= 126)
			{
				String entity = ascTab.get(new Character(ch));

				if (entity != null)
					sb.append(entity);
				else
					sb.append(ch);
			}

			else
				sb.append(ch);
		}

		return sb.toString();
	}

	public static String decodeEntities(String st)
	{
		StringBuilder sb = new StringBuilder();

		int i = 0;
		char chi;

		StringBuilder entity;

		while (i < st.length())
		{
			char ch = st.charAt(i);

			if (ch == '&')
			{
				entity = new StringBuilder("&");

				do
				{
					i++;
					chi = st.charAt(i);
					entity.append(chi);
				}
				while (chi != ';' && i < st.length());

				String convertedEntity = ascTabRev.get(entity.toString());

				if (convertedEntity != null)
					sb.append(convertedEntity);

			}
			else
				sb.append(ch);

			i++;
		}

		return sb.toString();
	}

}
