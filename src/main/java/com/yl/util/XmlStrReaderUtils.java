package com.yl.util;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.InputSource;

import com.yl.entity.MyCard;

public class XmlStrReaderUtils
{
	public static ArrayList<MyCard> ReadStrXml(String strXml)
	{
		strXml = strXml.replaceAll(" ", "").replaceAll("\n", "")
				.replaceAll("\t", "");
		InputSource in = new InputSource(new StringReader(strXml));
		in.setEncoding("UTF-8");
		SAXReader reader = new SAXReader();
		ArrayList<MyCard> tradeList = new ArrayList<MyCard>();
		try
		{
			Document document = reader.read(in);
			Element root = document.getRootElement();
			String FatoryId = root.elementText("FatoryId");
			String TotalNum = root.elementText("TotalNum");
			System.out.println(FatoryId);
			System.out.println(TotalNum);
			Element Records = root.element("Records");
			Iterator<Element> it = Records.elements().iterator();
			while (it.hasNext())
			{
				Element Record = it.next();
				MyCard mc = new MyCard();
				mc.setFatoryId(FatoryId);
				mc.setTotalNum(TotalNum);
				mc.setReturnMsgNo(Record.elementText("ReturnMsgNo"));
				mc.setReturnMsg(Record.elementText("ReturnMsg"));
				mc.setTradeSeq(Record.elementText("TradeSeq"));
				System.out.println(Record.elementText("ReturnMsgNo"));
				System.out.println(Record.elementText("ReturnMsg"));
				System.out.println(Record.elementText("TradeSeq"));
				tradeList.add(mc);
			}
		}
		catch (DocumentException e)
		{
			System.out.println("=read xml error==");

		}
		System.out.println(tradeList.size());
		return tradeList;
	}

	public static void main(String[] args)
	{
		String xml = "<BillingApplyRq> <FatoryId>xxxx</FatoryId> <TotalNum>2</TotalNum> <Records> <Record> <ReturnMsgNo>1</ReturnMsgNo> <ReturnMsg></ReturnMsg> <TradeSeq>00001</ TradeSeq > </Record> <Record> <ReturnMsgNo>1</ReturnMsgNo> <ReturnMsg></ReturnMsg> <TradeSeq>00002</TradeSeq> </Record> </Records> </BillingApplyRq>";
		ArrayList<MyCard> mc = ReadStrXml(xml);
		/*
		 * for(MyCard m:mc){
		 * System.out.println(m.getFatoryId()+m.getMyCard_ID()); }
		 */
	}

}
