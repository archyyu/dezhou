package com.yl.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.jdom.Document;
import org.jdom.output.XMLOutputter;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.yl.Global.ConstList;

public class DomParse extends DefaultHandler
{
	private String tagValue;
	long starttime;
	long endtime;

	public static void main(String[] args)
	{
		String filename = "session/test/demo.xml";
		SAXParserFactory spf = SAXParserFactory.newInstance();
		try
		{
			SAXParser saxParser = spf.newSAXParser();
			saxParser.parse(new File(filename), new DomParse());
		}
		catch (Exception e)
		{
			ConstList.config.logger.error(e.getMessage());
		}

	}

	// 开始解析XML文件
	public void startDocument() throws SAXException
	{
		// 可以在此初始化变量等操作
		ConstList.config.logger.debug("~~~~解析文档开始~~~");
		starttime = System.currentTimeMillis();
	}

	// 结束解析XML文件
	public void endDocument() throws SAXException
	{
		endtime = System.currentTimeMillis();
		ConstList.config.logger.debug("~~~~解析文档结束~~~");
		ConstList.config.logger.debug("共用" + (endtime - starttime) + "毫秒");
	}

	/**
	 * 在解释到一个开始元素时会调用此方法.但是当元素有重复时可以自己写算法来区分
	 * 
	 */
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException
	{
		ConstList.config.logger.debug("startElement处标签名:" + qName);
		try
		{
			if (attributes != null && attributes.getLength() != 0)
			{
				ConstList.config.logger.debug("--" + "该标签有属性值:");
				for (int i = 0; i < attributes.getLength(); i++)
				{
					ConstList.config.logger.debug(attributes.getQName(i) + "="
							+ attributes.getValue(attributes.getQName(i)));
				}
			}
		}
		catch (Exception ex)
		{
			ConstList.config.logger.error(ex.getMessage());
		}

	}

	/**
	 * 在遇到结束标签时调用此方法
	 */
	public void endElement(String uri, String localName, String qName)
			throws SAXException
	{
		ConstList.config.logger.debug("endElement处的值是:" + tagValue);
	}

	/**
	 * 所有的XML文件中的字符会放到ch[]中
	 */
	public void characters(char ch[], int start, int length)
			throws SAXException
	{
		tagValue = new String(ch, start, length);

	}

	public static void writeDocumentToFile(Document document, String fileName)
	{
		XMLOutputter outputter = new XMLOutputter();
		PrintWriter out = null;
		try
		{
			out = new PrintWriter(new BufferedWriter(new FileWriter(new File(
					fileName))));
			outputter.output(document, out);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			try
			{
				if (out != null)
				{
					out.close();
				}
			}
			catch (Exception ex)
			{
				ConstList.config.logger.error(ex.getMessage());
			}
		}
	}

}