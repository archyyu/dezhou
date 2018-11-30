package com.archy.dezhou.util;

import java.io.File;
import java.util.Enumeration;
import java.util.HashMap;

import com.archy.dezhou.entity.Prop;

import net.n3.nanoxml.IXMLElement;
import net.n3.nanoxml.IXMLParser;
import net.n3.nanoxml.IXMLReader;
import net.n3.nanoxml.StdXMLReader;
import net.n3.nanoxml.XMLParserFactory;

public class XmlReaderUtils
{
	public static HashMap<String, HashMap<String, String>> retXmlReaderByRoom(String xml)
	{
		HashMap<String, HashMap<String, String>> retmap = new HashMap<String, HashMap<String, String>>();
		try
		{
			
			IXMLParser xmlParser = XMLParserFactory.createDefaultXMLParser();
			IXMLReader xmlReader = StdXMLReader.fileReader((new File(xml)
					.toURI().toString()).substring(5));
			xmlParser.setReader(xmlReader);
			IXMLElement xmlDoc = (IXMLElement) xmlParser.parse();
			IXMLElement node = xmlDoc.getFirstChildNamed("rooms");
			Enumeration<?> rooms = node.enumerateChildren();
			while (rooms.hasMoreElements())
			{
				IXMLElement room = (IXMLElement) rooms.nextElement();
				HashMap<String, String> hmap = new HashMap<String, String>();
				hmap.put("name", room.getAttribute("name", "").trim());
				hmap.put("showname", room.getAttribute("showname", "").trim());
				hmap.put("sbet", room.getAttribute("sbet", "").trim());
				hmap.put("bbet", room.getAttribute("bbet", "").trim());
				hmap.put("mixbuy", room.getAttribute("mixbuy", "").trim());
				hmap.put("maxbuy", room.getAttribute("maxbuy", "").trim());
				hmap.put("roomtype", room.getAttribute("roomtype", "").trim());

				if (room.getAttribute("creater", "") != null)
					hmap.put("creater", room.getAttribute("creater", "").trim());
				else
					hmap.put("creater", "10000");

				if (room.getAttribute("ts", "") != null)
					hmap.put("ts", room.getAttribute("ts", "").trim());
				else
					hmap.put("ts", "0");

				retmap.put(room.getAttribute("name", "").trim(), hmap);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return retmap;
	}

	public static HashMap<String, Prop> retXmlReaderByProp(String xml)
	{
		HashMap<String, Prop> retmap = new HashMap<String, Prop>();
		try
		{
			IXMLParser xmlParser = XMLParserFactory.createDefaultXMLParser();
			IXMLReader xmlReader = StdXMLReader.fileReader((new File(xml)
					.toURI().toString()).substring(5));
			xmlParser.setReader(xmlReader);
			IXMLElement xmlDoc = (IXMLElement) xmlParser.parse();
			IXMLElement node = xmlDoc.getFirstChildNamed("props");
			Enumeration<?> propNodeList = node.enumerateChildren();
			while (propNodeList.hasMoreElements())
			{
				IXMLElement propNode = (IXMLElement) propNodeList.nextElement();
				Prop prop = new Prop();
				prop.setMailType(propNode.getAttribute("mailType", "").trim());
				prop.setSecondaryType(propNode
						.getAttribute("secondaryType", "").trim());
				prop.setType(propNode.getAttribute("type", "").trim());
				prop.setName(propNode.getAttribute("name", "").trim());
				prop.setAmount(Integer.parseInt(propNode.getAttribute("amount",
						"").trim()));
				prop.setCurrency(propNode.getAttribute("currency", "").trim());
				prop.setLevel(propNode.getAttribute("level", "").trim());
				prop.setForType(propNode.getAttribute("forType", "").trim());
				prop.setExpireDay(Integer.parseInt(propNode.getAttribute(
						"expireday", "").trim()));
				prop.setConsumeCode(propNode.getAttribute("consumeCode", "")
						.trim());
				retmap.put(propNode.getAttribute("type", "").trim(), prop);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return retmap;
	}

	/**
	 * 黑名单解析
	 */
	public static HashMap<Integer, String> retXmlReaderByBW(String fileName)
	{
		HashMap<Integer, String> bwList = new HashMap<Integer, String>();
		try
		{
			IXMLParser xmlParser = XMLParserFactory.createDefaultXMLParser();
			IXMLReader xmlReader = StdXMLReader.fileReader((new File(fileName)
					.toURI().toString()).substring(5));
			xmlParser.setReader(xmlReader);
			IXMLElement xmlDoc = (IXMLElement) xmlParser.parse();
			IXMLElement node = xmlDoc.getFirstChildNamed("wList");
			Enumeration<?> rooms = node.enumerateChildren();
			while (rooms.hasMoreElements())
			{
				IXMLElement w = (IXMLElement) rooms.nextElement();
				String nameString = w.getAttribute("name", "").trim();
				bwList.put(bwList.size(), nameString);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return bwList;
	}

	public static void main(String[] args)
	{
		retXmlReaderByProp("");
		retXmlReaderByRoom("");
	}

}
