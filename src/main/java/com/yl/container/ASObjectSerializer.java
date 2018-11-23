package com.yl.container;

import java.io.PrintStream;
import java.util.*;
import java.util.logging.Logger;
import net.n3.nanoxml.*;

public class ASObjectSerializer
{

	public ASObjectSerializer()
	{
	}

	private static String encodeEntities(String str)
	{
		StringBuffer in_sb = new StringBuffer(str);
		StringBuffer out_sb = new StringBuffer();
		for (int i = 0; i < in_sb.length(); i++)
		{
			char c = in_sb.charAt(i);
			if (c == '\t' || c == '\n' || c == '\r')
				out_sb.append(c);
			else if (c >= ' ' && c <= '~')
			{
				Object o = asciiTable_e.get(new String((new StringBuilder())
						.append(c).toString()));
				if (o != null)
					out_sb.append((String) o);
				else
					out_sb.append(c);
			}
			else
			{
				out_sb.append(c);
			}
		}

		return out_sb.toString();
	}

	public static ActionscriptObject deserialize(String xmlData)
	{
		ActionscriptObject ao = new ActionscriptObject();
		try
		{
			IXMLParser parser = XMLParserFactory.createDefaultXMLParser();
			net.n3.nanoxml.IXMLReader reader = StdXMLReader
					.stringReader(xmlData);
			parser.setReader(reader);
			IXMLElement xml = (IXMLElement) parser.parse();
			xml2obj(xml, ao, 0);
		}
		catch (Exception e)
		{

		}
		return ao;
	}

	public static String serialize(ActionscriptObject ao)
	{
		StringBuffer xmlData = new StringBuffer();
		obj2xml(ao, 0, "", xmlData);
		return xmlData.toString();
	}

	private static void xml2obj(IXMLElement node, ActionscriptObject ao, int n)
	{
		for (Enumeration subNodes = node.enumerateChildren(); subNodes
				.hasMoreElements();)
		{
			IXMLElement subNode = (IXMLElement) subNodes.nextElement();
			String subNodeName = subNode.getName();
			if (subNodeName.equals("obj"))
			{
				String name = subNode.getAttribute("o", "");
				ActionscriptObject subAO = new ActionscriptObject();
				ao.put(name, subAO);
				xml2obj(subNode, subAO, n + 1);
			}
			else if (subNodeName.equals("var"))
			{
				String name = subNode.getAttribute("n", "");
				String type = subNode.getAttribute("t", "");
				String val = subNode.getContent();
				Object varValue = null;
				if (type.equals("b"))
					varValue = val.equals("1") ? ((Object) (new Boolean(true)))
							: ((Object) (new Boolean(false)));
				else if (type.equals("s"))
					varValue = val;
				else if (type.equals("n"))
					varValue = new Double(Double.parseDouble(val));
				ao.put(name, varValue);
			}
		}

	}

	private static void obj2xml(ActionscriptObject ao, int depth,
			String nodeName, StringBuffer xmlData)
	{
		if (depth == 0)
			xmlData.append("<dataObj>");
		else
			xmlData.append("<obj o='").append(nodeName).append("' t='a'>");
		LinkedList keys = new LinkedList(ao.keySet());
		for (Iterator i = keys.iterator(); i.hasNext();)
		{
			String key = (String) i.next();
			Object o = ao.get(key);
			if (o == null)
				xmlData.append("<var n='").append(key).append("' t='x' />");
			else if (o instanceof ActionscriptObject)
			{
				obj2xml((ActionscriptObject) o, depth + 1, key, xmlData);
				xmlData.append("</obj>");
				IXMLElement temp = new XMLElement("obj");
			}
			else if (o instanceof Double)
			{
				double d = ((Double) o).doubleValue();
				String number = new String();
				if (d == Math.rint(d))
					number = (new StringBuilder()).append((long) d).toString();
				else
					number = (new StringBuilder()).append(d).toString();
				xmlData.append("<var n='").append(key).append("' t='n'>")
						.append(number).append("</var>");
			}
			else if (o instanceof String)
				xmlData.append("<var n='").append(key).append("' t='s'>")
						.append(encodeEntities((String) o)).append("</var>");
			else if (o instanceof Boolean)
			{
				boolean b = ((Boolean) o).booleanValue();
				xmlData.append("<var n='").append(key).append("' t='b'>")
						.append(b ? "1" : "0").append("</var>");
			}
		}

		if (depth == 0)
			xmlData.append("</dataObj>");
	}

	private static void echo(String s, int n)
	{
		echoTabs(n);
		System.out.println(s);
	}

	private static void echoTabs(int n)
	{
		for (int i = 0; i < n; i++)
			System.out.print("\t");

	}

	public static void dump(ActionscriptObject ao, int depth)
	{
		echo("", 0);
		LinkedList keys = new LinkedList(ao.keySet());
		String key;
		String output;
		for (Iterator i = keys.iterator(); i.hasNext(); echo(
				(new StringBuilder("[ ")).append(key).append(" ] > ")
						.append(output).toString(), depth))
		{
			key = (String) i.next();
			Object o = ao.get(key);
			output = "";
			if (o == null)
				output = "Null";
			else if (o instanceof ActionscriptObject)
			{
				output = "Object: ";
				dump((ActionscriptObject) o, depth + 1);
			}
			else if (o instanceof Double)
				output = (new StringBuilder("Number: ")).append(
						((Double) o).toString()).toString();
			else if (o instanceof String)
				output = (new StringBuilder("String: ")).append(
						((String) o).toString()).toString();
			else if (o instanceof Boolean)
				output = (new StringBuilder("Bool: ")).append(
						((Boolean) o).toString()).toString();
		}

	}

	private static Map asciiTable_e;
	private static Map asciiTable_d;

	static
	{
		asciiTable_e = new HashMap();
		asciiTable_e.put(">", "&gt;");
		asciiTable_e.put("<", "&lt;");
		asciiTable_e.put("&", "&amp;");
		asciiTable_e.put("'", "&apos;");
		asciiTable_e.put("\"", "&quot;");
		asciiTable_d = new HashMap();
		asciiTable_d.put("&gt;", ">");
		asciiTable_d.put("&lt;", "<");
		asciiTable_d.put("&amp;", "&");
		asciiTable_d.put("&apos;", "'");
		asciiTable_d.put("&quot;", "\"");
	}
}
