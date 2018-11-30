package com.archy.dezhou.util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

public class CreateXmlFromObject
{

	public static TestWrite tw = new TestWrite();
	public static WriteXML wx = new WriteXML();

	public static void main(String[] args)
	{
		tw.f();
		wx.write();
	}

	public static class WriteXML
	{
		public void write()
		{
			Element root = new Element("a");
			Element b = new Element("b");
			b.setAttribute("id", "1");
			root.addContent(b);
			Element c = new Element("c");
			b.addContent(c);
			Element forward = new Element("forward");
			forward.setAttribute("name", "toX");
			c.addContent(forward);
			Document doc = new Document(root);
			XMLOutputter xo = new XMLOutputter(Format.getPrettyFormat());
			try
			{
				xo.output(doc, new FileOutputStream(
						"session/global/TestCase.xml"));
			}
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}

		}

	}

	public static class TestWrite
	{
		public void f()
		{
			SAXBuilder sax = new SAXBuilder();
			try
			{
				Document doc = sax.build("session/roomlist/configRoomList.xml");
				Element root = doc.detachRootElement();
				List<Element> list = root.getChildren();
				Element el = list.get(0);
				list = el.getChildren();
				for (int i = 0; i < list.size(); i++)
				{
					System.out.print(list.get(i).getName() + ",");
					if ((i + 1) % 15 == 0)
						System.out.println();
				}
			}
			catch (JDOMException e)
			{
				e.printStackTrace();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}

		}

	}

}
