package com.archy.dezhou.httpLogic;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.archy.dezhou.Global.UserModule;
import com.archy.dezhou.room.base.IRoom;

public class RoomListLevel
{

	public static File destFile = null;

	public static String getRoomListFromMemory(String roomtype, String bb,
			String sb)
	{
		List<IRoom> roomlist = UserModule.getInstance().getRoomList();
		String xmlString = "";
		Element root = new Element("roomCollection");
		Element child = new Element("rooms");
		for (int i = 0; i < roomlist.size(); i++)
		{
			IRoom room = roomlist.get(i);
			Element subchild = new Element("room");
			subchild.setAttribute("id", "" + room.getRoomId());
			subchild.setAttribute("name", room.getName());
			subchild.setAttribute("mu", "" + room.getMaxUsers());
			int[] usercount = UserModule.getInstance().getplayerNum(room.getName());
			subchild.setAttribute("uc", "" + usercount[1]);
			subchild.setAttribute("sc", "" + usercount[2]);
			
			subchild.setAttribute("mix_buy",room.getValueByKey("mixbuy"));
			subchild.setAttribute("big_bet",room.getValueByKey("bbet"));
			subchild.setAttribute("ts",room.getValueByKey("ts"));
			subchild.setAttribute("small_bet",room.getValueByKey("sbet"));
			subchild.setAttribute("max_buy",room.getValueByKey("maxbuy"));
			subchild.setAttribute("roomtype",room.getValueByKey("roomtype"));
			subchild.setAttribute("creater",room.getValueByKey("creater"));
			subchild.setAttribute("show_name",room.getValueByKey("showname"));
			
			if(  roomlist.get(i).getBBet() >= Integer.parseInt(sb)
					&& roomlist.get(i).getBBet() <= Integer.parseInt(bb))
			{
				child.addContent(subchild);
			}
		}

		root.addContent(child);
		Document doc = new Document(root);
		Format format = Format.getPrettyFormat();
		format.setEncoding("UTF-8");
		XMLOutputter outputter = new XMLOutputter();
		outputter.setFormat(format);
		xmlString = outputter.outputString(doc);
		return xmlString;
	}

	public static void WriteRoomListXMLFile(String sourceFileName,
			String destFileName, SAXBuilder builder)
	{
	
	}


	@SuppressWarnings("rawtypes")
	public static void writeGameinfo2XmlFile(HashMap<String, HashMap> gameInfo)
	{
		
	}

}
