package com.yl.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.XMLWriter;

public class XmlWriteUtils
{
	public static void creatXml(HashMap<String, HashMap> hm) throws IOException
	{
		Document doc = DocumentHelper.createDocument();
		Element rc = doc.addElement("roomCollection");
		Element rooms = rc.addElement("rooms");
		for (String key : hm.keySet())
		{
			Element room = rooms.addElement("room");
			HashMap sr = hm.get(key);
			room.addAttribute("name", sr.get("name").toString());
			room.addAttribute("showname", sr.get("show_name").toString());
			room.addAttribute("bbet", sr.get("big_bet").toString());
			room.addAttribute("sbet", sr.get("small_bet").toString());
			room.addAttribute("mixbuy", sr.get("mix_buy").toString());
			room.addAttribute("maxbuy", sr.get("mix_buy").toString());
			room.addAttribute("roomtype", sr.get("roomtype").toString());
		}

		XMLWriter xw = new XMLWriter(new FileOutputStream("c:\\common.xml"));
		xw.write(doc);
		xw.close();
	}

}
