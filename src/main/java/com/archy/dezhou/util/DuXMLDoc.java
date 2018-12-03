package com.archy.dezhou.util;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.xml.sax.InputSource;

public class DuXMLDoc
{
	public List xmlElements(String xmlDoc)
	{
		// 创建一个新的字符串
		StringReader read = new StringReader(xmlDoc);
		// 创建新的输入源SAX 解析器将使用 InputSource 对象来确定如何读取 XML 输入
		InputSource source = new InputSource(read);
		// 创建一个新的SAXBuilder
		SAXBuilder sb = new SAXBuilder();
		try
		{
			// 通过输入源构造一个Document
			Document doc = sb.build(source);
			// 取的根元素
			Element root = doc.getRootElement();
			System.out.println(root.getName());// 输出根元素的名称（测试）
			// 得到根元素所有子元素的集合
			Namespace ns = root.getNamespace();

			System.out.println(root.getChild("status", ns).getText());
			// List jiedian = root.getChildren();
			// // 获得XML中的命名空间（XML中未定义可不写）
			// Element et = null;
			// for (int i = 0; i < jiedian.size(); i++)
			// {
			// et = (Element) jiedian.get(i);// 循环依次得到子元素
			// // 无命名空间定义时
			// System.out.println(et.getChild("status", ns).getText());
			// }
			// /**//*
			// * 如要取<row>下的子元素的名称
			// */
			// et = (Element) jiedian.get(0);
			// List zjiedian = et.getChildren();
			// for (int j = 0; j < zjiedian.size(); j++) {
			// Element xet = (Element) zjiedian.get(j);
			// //System.out.println(xet.getAccount());
			//
			// }
		}
		catch (JDOMException e)
		{
			// TODO 自动生成 catch 块
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO 自动生成 catch 块
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args)
	{
		DuXMLDoc doc = new DuXMLDoc();
		// String xml =
		// "<?xml version='1.0' encoding='utf-8'?>"+
		// "<Msg>"+
		// "<Head>"+
		// "<Ver>版本号NString3<!--基本报文头--></Ver>"+
		// "<SndTime>发起时间DateTime14<!--基本报文头--></SndTime>"+
		// "<Priority>优先级NString1<!--基本报文头--></Priority>"+
		// "<SysID>发起方系统号NString4<!--基本报文头--></SysID>"+
		// "<MsgNo>报文编号NString6<!--新增结点,将旧的改用新的方式重编--></MsgNo>"+
		// "<MsgID>信息序号NString20<!--基本报--></MsgID>"+
		// "<OrigMsgID>原信息序号NString20<!--单笔应用报文头--></OrigMsgID>"+
		// "<MacFlag>编核押标志0</MacFlag>"+
		// "</Head>"+
		// "</Msg>";

		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<response>" + "<status>1100</status>"
				+ "<msgType>ChargeUpResp</msgType>"
				+ "<balance>6000.0</balance>" + "<hRet>0</hRet>"
				+ "</response>";
		doc.xmlElements(xml);
	}
}
