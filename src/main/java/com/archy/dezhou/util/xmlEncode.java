package com.archy.dezhou.util;

import com.archy.dezhou.global.ConstList;
import org.jdom.*;

import java.io.*;

import org.jdom.output.*;

public class xmlEncode
{

	public void writerElement(String xmlFile, String Content, int type,
			String nodep, String nodec)
	{
		Element root = null;
		ConstList.config.logger.debug("定义根");
		Element addElement = null;
		ConstList.config.logger.debug("定义第二级节点");
		Element addElementc = null;
		ConstList.config.logger.debug("定义第三级节点");
		Document docJDOM = null;
		ConstList.config.logger.debug("定义Doc，用于作为写入对象");
		xmlDecode reader = null;
		if (type == 0)
			reader = new xmlDecode(xmlFile, type);
		else
			reader = new xmlDecode(Content, type);
		ConstList.config.logger.debug("定义一个XmlReader对象");
		if (reader.getM_RootElement() == null)
		{//
			ConstList.config.logger.debug("判断Xml是否已经存在根元素,不存在");
			root = new Element("Application");// 获得新的根
			docJDOM = new Document(root);// 根据开始写入的根获得Document
			addElement = new Element("Group");// 获得新的第二级节点
			Attribute a = new Attribute("name", nodep);// ;定义属性
			addElement.setAttribute(a);// 在节点中添加属性
			addElementc = new Element("key");// 获得新的第三级节点
			addElementc.addContent(nodec);// 添加内容
			addElement.addContent(addElementc);// 将第三级添加到第二级
			root.addContent(addElement);// 将第二级添加到根
		}
		else
		{
			ConstList.config.logger.debug("判断Xml是否已经存在根元素,存在");

			root = reader.getM_RootElement();// 读出已经存在的根
			if (reader.checkElement(root, nodep) != null)
			{// 判断要写入的第二级根在Xml中是否已经存在
				// 存在
				addElement = reader.checkElement(root, nodep);// 判断写入的第三级节点是否在Xml中
				if (reader.checkElementc(root, nodep, nodec) == null)
				{// 不存在
					addElementc = new Element("key");
					addElementc.addContent(nodec);
					addElement.addContent(addElementc);
					docJDOM = addElement.getDocument();// 获得写入的位置
				}
			}
			else
			{// 不存在
				docJDOM = root.getDocument();// 获得写入的位置
				addElement = new Element("Group");
				Attribute a = new Attribute("name", nodep);
				addElement.setAttribute(a);
				addElementc = new Element("key");
				addElementc.addContent(nodec);
				addElement.addContent(addElementc);
				root.addContent(addElement);
			}
		}

		try
		{
			Format format = Format.getPrettyFormat();
			format.setEncoding("UTF8");
			format.setLineSeparator("\r\n");// 自动换行
			XMLOutputter xmlOut = new XMLOutputter(format);
			FileWriter fwXML = new FileWriter(xmlFile);
			xmlOut.output(docJDOM, fwXML);// 写入
			fwXML.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {

		xmlEncode xmlencode = new xmlEncode();
		xmlencode.writerElement("session/test/demo.xml", xmlDecode.str, 0,
				"Application", "");
	}

}
