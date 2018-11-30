package com.archy.dezhou.util;

import com.archy.dezhou.Global.ConstList;
import org.jdom.*;
import org.jdom.input.*;

import java.io.*;
import java.util.*;

public class xmlDecode
{

	private Element m_RootElement = null;

	/**
	 * 构造函数
	 * 
	 * @param xmlFile
	 *            String 根据文件的路径初始化dom的根
	 */
	public xmlDecode(String xmlFileOrContent, int type)
	{
		try
		{
			SAXBuilder builder = new SAXBuilder();
			ConstList.config.logger.debug("解析器定义");

			Document doc = null;
			if (type == 0)
				doc = builder.build(new FileInputStream(xmlFileOrContent));
			else
			{
				InputStream is = new ByteArrayInputStream(
						xmlFileOrContent.getBytes());
				doc = builder.build(is);
			}
			ConstList.config.logger.debug("读入Xml文件，获得Doc");

			this.m_RootElement = doc.getRootElement();
			ConstList.config.logger.info("获得Xml文件的最上面的根[1]"
					+ m_RootElement.getName() + "," + m_RootElement.getText()
					+ "," + m_RootElement.getValue());
		}
		catch (IOException ex)
		{
			this.m_RootElement = null;
		}
		catch (JDOMException ex)
		{
			this.m_RootElement = null;
		}
	}

	/**
	 * 获得指定名字的根的内容，此方法只适用于此xml
	 * 
	 * @param curRoot
	 *            Element Your XmlRoot
	 * @param codeName
	 *            String Your XmlCode
	 * @return List
	 */
	public List getElement(Element curRoot, String codeName)
	{
		List result = null;

		if (null == curRoot)
		{
			curRoot = m_RootElement;
			ConstList.config.logger.debug("判断Xml不存在，以及根是否正确解析");
		}
		else if (null != curRoot)
		{
			ConstList.config.logger.debug("判断Xml存在，以及根是否正确解析");

			List<String> l = curRoot.getChildren();// 获得最上层根的所有字节点
			Iterator it = l.iterator();// 递归取出
			while (it.hasNext())
			{
				Element e = (Element) it.next();
				if (e.getAttributeValue("name").equals(codeName))
				{// 获取这些根是否为所需要的
					List l1 = e.getChildren();// 如果需要，解析出这个子节点的所有子节点
					Iterator it1 = l1.iterator();
					while (it1.hasNext())
					{
						Element e1 = (Element) it1.next();
						ConstList.config.logger.info("[2]" + e1.getName() + ","
								+ e1.getText() + "," + e1.getValue());
						result.add(e1.getTextTrim());// 取出所包含的值，放到要返回的结果集中
					}
					break;
				}
			}
		}
		return result;
	}

	/**
	 * 检查属性为codeName的第二级子元素（即<Group name="">）的name的值是否存在
	 * 
	 * @param curRoot
	 *            Element Your XmlRoot
	 * @param codeName
	 *            String Your XmlCode
	 * @return Element 如果存在则返回Element的对象
	 */

	public Element checkElement(Element curRoot, String codeName)
	{
		Element element = null;
		if (null == curRoot)
		{
			curRoot = m_RootElement;
			ConstList.config.logger.debug("检查属性为codeName的第二级子元素name的值不存在");
		}
		else if (null != curRoot)
		{
			ConstList.config.logger.debug("检查属性为codeName的第二级子元素name的值存在");

			List l = curRoot.getChildren();// 获得最上层根的所有字节点
			Iterator it = l.iterator();
			while (it.hasNext())
			{
				Element e = (Element) it.next();
				ConstList.config.logger.info("[3]" + e.getName() + ","
						+ e.getText() + "," + e.getValue());
				if (e.getAttributeValue("name").equals(codeName))
				{
					element = e;
					break;
				}
			}
		}
		return element;
	}

	/**
	 * 检查值为codeName的第三级子元素（即<Key></Key>）中的值是否存在
	 * 
	 * @param curRoot
	 *            Element Your XmlRoot
	 * @param codeNamep
	 *            String Your XmlCode
	 * @param codeNamec
	 *            String Your XmlCode
	 * @return Element 如果存在则返回Element的对象
	 */

	public Element checkElementc(Element curRoot, String codeNamep,
			String codeNamec)
	{
		Element element = null;
		if (null == curRoot)
		{
			curRoot = m_RootElement;
			ConstList.config.logger.debug("检查属性为codeName的第三级子元素name的值不存在");

		}
		else if (null != curRoot)
		{
			ConstList.config.logger.debug("检查属性为codeName的第三级子元素name的值存在");

			List l = curRoot.getChildren();// 获得最上层根的所有字节点

			Iterator it = l.iterator();
			while (it.hasNext())
			{
				Element e = (Element) it.next();
				if (e.getAttributeValue("name").equals(codeNamep))
				{
					List l1 = e.getChildren();
					Iterator it1 = l1.iterator();
					while (it1.hasNext())
					{
						Element e1 = (Element) it1.next();
						ConstList.config.logger.info("[4]" + e1.getName() + ","
								+ e1.getText() + "," + e1.getValue());
						if (e1.getTextTrim().equals(codeNamec))
						{// 比较第三级子元素
							element = e1;
							break;
						}
					}
					break;
				}
			}
		}
		return element;
	}

	public Element getM_RootElement()
	{
		ConstList.config.logger.debug("得到根元素");
		return m_RootElement;
	}

	public void setM_RootElement(Element m_RootElement)
	{
		ConstList.config.logger.debug("设置根元素");
		this.m_RootElement = m_RootElement;
	}

	public static String str = "<?xml version='1.0' encoding='UTF8'?>"
			+ "<Application>" + "<Group name='book'>" + "<key>loops</key>"
			+ "<key>look</key>" + "</Group>" + "</Application>";
}
