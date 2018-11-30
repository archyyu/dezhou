package com.archy.dezhou.container;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import com.archy.dezhou.Global.ConstList;
import net.n3.nanoxml.IXMLElement;
import net.n3.nanoxml.IXMLParser;
import net.n3.nanoxml.IXMLReader;
import net.n3.nanoxml.StdXMLReader;
import net.n3.nanoxml.XMLParserFactory;

/**
 * 
 * @author Lapo
 */
public class SFSObjectSerializer
{
	// The XML Parser already does THIS when parsing
	// -------------------------------------------------------------------------------

	private static Map<String, String> asciiTable_e; // ascii code table for
														// encoding
	private static Map<String, String> asciiTable_d; // ascii code table for
														// decoding

	static
	{
		// --- XML Entities Encoding Table ------------------------------
		asciiTable_e = new HashMap<String, String>();

		asciiTable_e.put(">", "&gt;");
		asciiTable_e.put("<", "&lt;");
		asciiTable_e.put("&", "&amp;");
		asciiTable_e.put("'", "&apos;");
		asciiTable_e.put("\"", "&quot;");

		// --- XML Entities Decoding Table ------------------------------
		asciiTable_d = new HashMap<String, String>();

		asciiTable_d.put("&gt;", ">");
		asciiTable_d.put("&lt;", "<");
		asciiTable_d.put("&amp;", "&");
		asciiTable_d.put("&apos;", "'");
		asciiTable_d.put("&quot;", "\"");
	}

	/*
	 * Encode XML entities in their right format ... &<code>;
	 * 
	 * @param str = string to process
	 * 
	 * @return = String with encoded entities
	 */
	private static String encodeEntities(String str)
	{
		StringBuffer in_sb = new StringBuffer(str);
		StringBuffer out_sb = new StringBuffer();

		Object o;

		int i = 0;
		char c;

		while (i < in_sb.length())
		{
			c = in_sb.charAt(i);

			// Handle \t and \r\n codes
			if (c == 9 || c == 10 || c == 13)
				out_sb.append(c);

			else if (c >= 32 && c <= 126)
			{
				// search for this char in the map
				o = asciiTable_e.get(new String("" + c));

				// if an entity was found add it to the result StringBuffer
				if (o != null)
					out_sb.append((String) o);
				else
					out_sb.append(c);
			}

			else
				out_sb.append(c);

			i++;
		}

		return out_sb.toString();
	}

	/*
	 * Deserialize an XML Object into an Actionscript Object
	 * 
	 * @param xmlData = the XML
	 * 
	 * @return = ActionscriptObject
	 */
	public static SFSObject deserialize(String xmlData)
	{
		SFSObject ao = new SFSObject();

		try
		{
			IXMLParser parser = XMLParserFactory.createDefaultXMLParser();
			IXMLReader reader = StdXMLReader.stringReader(xmlData);
			parser.setReader(reader);
			IXMLElement xml = (IXMLElement) parser.parse();
			xml2obj(xml, ao, 0);
		}
		catch (Exception e)
		{
			ConstList.config.logger.error(e.getMessage());

		}

		return ao;
	}

	/*
	 * Convert an XML object into an Actionscript Object
	 * 
	 * @param ao = the ASObject
	 * 
	 * @return = and XML String with the serialized object
	 */
	public static String serialize(SFSObject ao)
	{
		StringBuffer xmlData = new StringBuffer();
		obj2xml(ao, 0, "", xmlData);
		return xmlData.toString();
	}

	/*
	 * Convert an XML object into an Actionscript Object
	 * 
	 * @param node = an XML node
	 * 
	 * @param ao = the ASObject
	 * 
	 * @param n = the depth level of recursion
	 */
	private static void xml2obj(IXMLElement node, SFSObject ao, int n)
	{
		// takes the children of <dataObj></dataObj>
		Enumeration<?> subNodes = node.enumerateChildren();

		// Loop through children
		while (subNodes.hasMoreElements())
		{
			// get next element
			IXMLElement subNode = (IXMLElement) subNodes.nextElement();

			String subNodeName = subNode.getName();

			// A sub-object was found, dig in recursively
			if (subNodeName.equals("obj"))
			{
				String name = subNode.getAttribute("o", "");
				SFSObject subAO = new SFSObject();

				ao.put(name, subAO);

				xml2obj(subNode, subAO, n + 1);
			}

			// A variable declaration was found
			else if (subNodeName.equals("var"))
			{
				String name = subNode.getAttribute("n", "");
				String type = subNode.getAttribute("t", "");
				String val = subNode.getContent();

				Object varValue = null;
				// Cast val to the right type
				// --- Boolean
				// ----------------------------------------------------------------
				if (type.equals("b"))
					varValue = val.equals("1") ? new Boolean(true)
							: new Boolean(false);
				// --- String
				// ------------------------------------------------------------------
				else if (type.equals("s"))
					varValue = val;
				// --- Number
				// ------------------------------------------------------------------
				else if (type.equals("n"))
					varValue = new Double(Double.parseDouble(val));
				// Add as string key
				ao.put(name, varValue);
			}
		}
	}

	/*
	 * Convert an ActionscriptObject object into an XML serialized
	 * representation
	 * 
	 * @param ao = the ActionscriptObject to serialize
	 * 
	 * @param depth = the recursion level
	 * 
	 * @param nodeName = the name of the subNode name to create (not necessary
	 * if it's the root node)
	 * 
	 * @xmlData = the StringBuffer that holds the XML
	 */
	public static byte[] obj2xml(SFSObject ao, int depth, String nodeName,
			StringBuffer xmlData)
	{
		if (depth == 0)
			xmlData.append("<dataObj>\n");
		else
			xmlData.append("<obj o='").append(nodeName).append("' t='a'>\n");

		LinkedList<Object> keys = new LinkedList<Object>(ao.keySet());
		Iterator<Object> i = keys.iterator();

		while (i.hasNext())
		{
			String key = (String) i.next();
			Object o = ao.get(key);

			// --- Handle Nulls -----------------------------------------
			if (o == null)
			{
				xmlData.append("<var n='").append(key).append("' t='x' />\n");
			}
			else if (o instanceof SFSObject)
			{
				// Scan the object recursively
				obj2xml((SFSObject) o, depth + 1, key, xmlData);

				// When you get back to this level, close the
				xmlData.append("</obj>\n");

			}

			// --- Handle Numbers ---------------------------------------
			else if (o instanceof Double)
			{
				double d = ((Double) o).doubleValue();
				String number = new String();

				// if the value is integer, return a long
				if (d == Math.rint(d))
					number = "" + (long) d;
				else
					number = "" + d;

				xmlData.append("<var n='").append(key).append("' t='n'>")
						.append(number).append("</var>\n");
//				System.out.println(" Object o of Double=" + key);
			}

			else if(o instanceof Integer)
			{
				String number = new String();
				
				number = String.valueOf(o);
				
				xmlData.append("<var n='").append(key).append("' t='s'>")
				.append(encodeEntities(number)).append("</var>\n");
				
//				System.out.println(" o of Integer=" + key);
			}
			// --- Handle Strings ---------------------------------------
			else if (o instanceof String)
			{
				xmlData.append("<var n='").append(key).append("' t='s'>")
						.append(encodeEntities((String) o)).append("</var>\n");
//				System.out.println(" Object o of String=" + key);
			}
			else if (o instanceof Boolean)
			{
				boolean b = ((Boolean) o).booleanValue();
				xmlData.append("<var n='").append(key).append("' t='b'>")
						.append((b ? "1" : "0")).append("</var>\n");
				
//				System.out.println(" Object o of boolean=" + key);
			}

		}

		// If we're back to root node then close it!
		if (depth == 0)
			xmlData.append("</dataObj>\n");

		return xmlData.toString().getBytes();

	}

	public static void dumpAsObj(SFSObject ao)
	{
		ConstList.config.logger.info(" dumpAsObj: "  + ao.toString());
	}

}
