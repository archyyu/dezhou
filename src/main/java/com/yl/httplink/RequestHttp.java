package com.yl.httplink;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.xml.sax.InputSource;

import com.yl.Global.ConstList;
import com.yl.container.ActionscriptObject;
import com.yl.container.User;
import com.yl.thread.AsynchronousModule;
import com.yl.util.Utils;

public class RequestHttp implements Runnable
{

	String urlString = "";
	String handleName = "";
	public String consumeid = "";
	public String mobile = "";
	public String userId = "";
	public String key = "";
	public String uid = "";
	public String chargeNumCode = "";
	public String djid = "";
	public String channelId = "";
	public User user = null;
	// 查询明细所用
	public String Startdate = "";
	public String Enddate = "";

	public String threadName = "deafult";

	// 消费记录
	public String OneMonth = "";
	public String queryType = "";
	public String queryrange = "";

	// 公测变量
	public HashMap<String, String> publicTestMap = new HashMap<String, String>();

	public String ResponseStr = "";

	public static int transIn = 100000;

	public RequestHttp(String url, String handleName)
	{
		this.urlString = url;
		this.handleName = handleName;
	}

	public RequestHttp(String url, User u, String handleName)
	{
		this.urlString = url;
		this.user = u;
		this.handleName = handleName;
	}

	public void run()
	{
		if (urlString.indexOf("PublicTestRequest") < 0)
			ResponseStr = transport(urlString);
		else
			ResponseStr = "";
		if (urlString.indexOf("bizcontrol/ChargeUp") >= 0
				&& ResponseStr.length() > 10)
		{
			/**
			 * <?xml version="1.0" encoding="UTF-8"?> <response>
			 * <status>1100</status> <msgType>ChargeUpResp</msgType>
			 * <balance>6000.0</balance> <hRet>0</hRet> </response>
			 */
			AsynchronousModule am = new AsynchronousModule(user, "chargeUp",
					retXmlReaderChargeUp(ResponseStr), handleName, channelId,
					userId, consumeid, djid);
			ConstList.config.logger
					.warn("001---AsynchronousModule(user,\"chargeUp\",retXmlReaderChargeUp(ResponseStr)) is newed!");
			am.t.start();
		}
		else if (urlString.indexOf("bizcontrol/BuyGameTool") >= 0
				&& ResponseStr.length() > 10)
		{
			/**
			 * <?xml version="1.0" encoding="UTF-8"?> <response>
			 * <status>1800</status> <msgType>BuyGameToolResp</msgType>
			 * <balance>35397.0</balance> <hRet>0</hRet> <point>2000</point>
			 * </response>
			 */
			AsynchronousModule am = new AsynchronousModule(user, "BuyGameTool",
					retXmlReaderBuyGameTool(ResponseStr), handleName,
					channelId, userId, consumeid, djid);
			ConstList.config.logger
					.warn("002---AsynchronousModule(user,\"BuyGameTool\",retXmlReaderBuyGameTool(ResponseStr)) is newed!");
			am.t.start();
		}
		else if (urlString.indexOf("bizcontrol/QueryBalance") >= 0
				&& ResponseStr.length() > 10)
		{
			/**
			 * <?xml version="1.0" encoding="UTF-8"?> <response>
			 * <status>1105</status> <msgType>QueryBalanceResp</msgType>
			 * <hRet>0</hRet> <userIdType>1</userIdType>
			 * <userLabel>13667202367</userLabel> <point>59900.0</point>
			 * </response>
			 **/
			AsynchronousModule am = new AsynchronousModule(user,
					"QueryBalance", retXmlReaderQueryBalance(ResponseStr),
					handleName, channelId, userId, consumeid, djid);
			ConstList.config.logger
					.warn("003---AsynchronousModule(user,\"QueryBalance\",retXmlReaderQueryBalance(ResponseStr)) is newed!");

			am.t.start();
		}
		else if (urlString.indexOf("bizcontrol/QueryCharge") >= 0
				&& ResponseStr.length() > 10)
		{
			/**
			 * <?xml version="1.0" encoding="UTF-8"?> <response>
			 * <status>1105</status> <msgType>QueryBalanceResp</msgType>
			 * <hRet>0</hRet> <userIdType>1</userIdType>
			 * <userLabel>13667202367</userLabel> <point>59900.0</point>
			 * </response>
			 **/
			AsynchronousModule am = new AsynchronousModule(user, "QueryCharge",
					retXmlReaderQueryCharge(ResponseStr), handleName,
					channelId, userId, consumeid, djid);
			ConstList.config.logger
					.warn("004---AsynchronousModule(user,\"QueryCharge\",retXmlReaderQueryCharge(ResponseStr)) is newed!");
			am.t.start();
		}
		else if (urlString.indexOf("bizcontrol/QueryConsumeRecord") >= 0
				&& ResponseStr.length() > 10)
		{
			AsynchronousModule am = new AsynchronousModule(user,
					"QueryConsume", retXmlReaderQueryConsume(ResponseStr),
					handleName, channelId, userId, consumeid, djid);
			ConstList.config.logger
					.warn("005---AsynchronousModule(user,\"QueryConsume\",retXmlReaderQueryConsume(ResponseStr)) is newed!");
			am.t.start();
		}
		else if (urlString.indexOf("PublicTestRequest") >= 0)
		{
			AsynchronousModule am = new AsynchronousModule(7, "PublicTest",
					publicTestMap, handleName);
			ConstList.config.logger
					.warn("005---AsynchronousModule(user,\"PublicTest\",publicTestMap,handleName) is newed!");
			am.t.start();
		}
	}

	/**
	 * <response> <msgType>QueryConsumeRecordResp</msgType>
	 * <queryType>1…13</queryType> <hRet>1</hRet> <status>1102</status>
	 * <userIdType>1</userIdType> <userLabel>13800138000</userLabel>
	 * <recordList> <recordSchema>
	 * <!--消费类型：A-充值B-套餐O-开户C1-客户端单机C2-客户端网游道具C3-WAP网游道具C4-WAP单机-->
	 * <recordType>消费类型</recordType> <cpId>合作方ID</cpId> <cpName>合作方名称</cpName>
	 * <channelId>渠道ID</channelId> <channelName>渠道名称</channelName>
	 * <cpServiceId>业务ID或充值代码ID</cpServiceId>
	 * <cpServiceName>业务名称或充值名称</cpServiceName> <packageId>套餐ID</packageId>
	 * <packageName>套餐名称</packageName> <toolsId>道具ID</toolsId>
	 * <toolsName>道具名称</toolsName> <date>消费时间(格式20070618 09:58)</date>
	 * <payType>计费方式(1-点数 2-话费)</payType> <payValue>支付点数或话费分</payValue>
	 * </recordSchema> </recordList> </response>
	 * 
	 * <?xml version="1.0" encoding="UTF-8"?> <response>
	 * <msgType>QueryConsumeRecordResp</msgType> <queryType>3</queryType>
	 * <hRet>0</hRet> <status>1102</status> <userIdType>3</userIdType>
	 * <userLabel>04371075</userLabel> <recordList> <recordSchema>
	 * <recordType>C2</recordType> <cpId>710525</cpId>
	 * <cpName>南昌鼎安科技有限公司</cpName> <channelId>12068000</channelId>
	 * <cpServiceId></cpServiceId> <cpServiceName></cpServiceName>
	 * <packageId></packageId> <packageName>啤酒</packageName>
	 * <toolsId>000052577034</toolsId> <toolsName>啤酒</toolsName> <date>20120810
	 * 11:44</date> <payType>1</payType> <payValue>50</payValue> </recordSchema>
	 * </recordList> </response>
	 */
	public ActionscriptObject retXmlReaderQueryConsume(String xmlDoc)
	{
		ActionscriptObject QueryConsumepros = new ActionscriptObject();
		StringReader read = new StringReader(xmlDoc);
		InputSource source = new InputSource(read);
		SAXBuilder sb = new SAXBuilder();
		try
		{
			Document doc = sb.build(source);
			Element root = doc.getRootElement();
			ConstList.config.logger.warn(root.getName());// 输出根元素的名称（测试）
			Namespace ns = root.getNamespace();

			String status = root.getChild("status", ns).getText();
			if (status != null)
				QueryConsumepros.put("status", status);

			String msgType = root.getChild("msgType", ns).getText();
			if (msgType != null)
				QueryConsumepros.put("msgType", msgType);

			String userIdType = root.getChild("userIdType", ns).getText();
			if (userIdType != null)
				QueryConsumepros.put("userIdType", userIdType);

			String userLabel = root.getChild("userLabel", ns).getText();
			if (userLabel != null)
				QueryConsumepros.put("userLabel", userLabel);

			String hRet = root.getChild("hRet", ns).getText();
			if (hRet != null)
				QueryConsumepros.put("hRet", hRet);

			String queryType = root.getChild("queryType", ns).getText();
			if (queryType != null)
				QueryConsumepros.put("queryType", queryType);

			Element recordList = root.getChild("recordList", ns);

			List<Element> consumerSchemaList = recordList.getChildren();
			Namespace sns = recordList.getNamespace();
			QueryConsumepros.put("cmd", "QueryConsume");
			ConstList.config.logger.warn(root.getChild("status", ns).getText()
					+ "," + root.getChild("msgType", ns).getText() + ","
					+ root.getChild("hRet", ns).getText() + ","
					+ root.getChild("queryType", ns).getText() + ","
					+ root.getChild("userIdType", ns).getText() + ","
					+ root.getChild("userLabel", ns).getText() + ",");

			ActionscriptObject consumerListaObj = new ActionscriptObject();
			if (consumerSchemaList != null && consumerSchemaList.size() > 0)
			{
				for (int i = 0; i < consumerSchemaList.size(); i++)
				{
					ActionscriptObject consumeraObj = new ActionscriptObject();
					;
					Element consumerSchema = consumerSchemaList.get(i);

					String date = consumerSchema.getChild("date", sns)
							.getText();
					String payType = consumerSchema.getChild("payType", sns)
							.getText();
					String payValue = consumerSchema.getChild("payValue", sns)
							.getText();

					String recordType = consumerSchema.getChild("recordType",
							sns).getText();
					String cpId = consumerSchema.getChild("cpId", sns)
							.getText();
					String cpName = consumerSchema.getChild("cpName", sns)
							.getText();

					String channelId = consumerSchema
							.getChild("channelId", sns).getText();

					String cpServiceId = consumerSchema.getChild("cpServiceId",
							sns).getText();

					String cpServiceName = consumerSchema.getChild(
							"cpServiceName", sns).getText();
					String packageId = consumerSchema
							.getChild("packageId", sns).getText();
					String packageName = consumerSchema.getChild("packageName",
							sns).getText();

					String toolsId = consumerSchema.getChild("toolsId", sns)
							.getText();
					String toolsName = consumerSchema
							.getChild("toolsName", sns).getText();

					if (date == null || date.length() == 0)
						date = "-1";
					if (payType == null || payType.length() == 0)
						payType = "-1";
					if (payValue == null || payValue.length() == 0)
						payValue = "-1";

					if (recordType == null || recordType.length() == 0)
						recordType = "-1";
					if (cpId == null || cpId.length() == 0)
						cpId = "-1";
					if (cpName == null || cpName.length() == 0)
						cpName = "-1";

					if (channelId == null || channelId.length() == 0)
						channelId = "-1";
					if (cpServiceId == null || cpServiceId.length() == 0)
						cpServiceId = "-1";

					if (cpServiceName == null || cpServiceName.length() == 0)
						cpServiceName = "-1";
					if (packageId == null || packageId.length() == 0)
						packageId = "-1";
					if (packageName == null || packageName.length() == 0)
						packageName = "-1";

					if (toolsId == null || toolsId.length() == 0)
						toolsId = "-1";
					if (toolsName == null || toolsName.length() == 0)
						toolsName = "-1";
					consumeraObj.put("date", date);
					consumeraObj.put("payType", payType);
					consumeraObj.put("payValue", payValue);
					consumeraObj.put("recordType", recordType);
					consumeraObj.put("toolsId", toolsId);
					consumeraObj.put("toolsName", toolsName);
					consumerListaObj.put("" + i, consumeraObj);
					ConstList.config.logger.warn(i + ":  " + date + ","
							+ payType + "," + payValue + "," + recordType + ","
							+ cpId + "," + cpName + "," + channelId + ","
							+ cpServiceId + "," + cpServiceName + ","
							+ packageId + "," + packageName + "," + toolsId
							+ "," + toolsName);

				}
			}
			QueryConsumepros.put("detail", consumerListaObj);

		}
		catch (JDOMException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return QueryConsumepros;

	}

	public ActionscriptObject publicTestParms(String xmlDoc)
	{
		ActionscriptObject obj = new ActionscriptObject();

		return obj;
	}

	/**
	 * <?xml version="1.0" encoding="UTF-8"?> <response>
	 * <msgType>QueryChargeResp</msgType> <hRet>0</hRet> <status>1200</status>
	 * <userIdType>1</userIdType> <userLabel>13888888888</userLabel>
	 * <startSequence>1</startSequence> <recordCount>20</recordCount>
	 * <chargeList> <consumerSchema> <date>String</date>
	 * <consumerType>A</consumerType> <point>String</point> </consumerSchema>
	 * <consumerSchema> <date>String</date> <consumerType>A</consumerType>
	 * <point>String</point> </consumerSchema> <consumerSchema>
	 * <date>String</date> <consumerType>A</consumerType> <point>String</point>
	 * </consumerSchema> </chargeList> </response>
	 **/

	public ActionscriptObject retXmlReaderQueryCharge(String xmlDoc)
	{
		ActionscriptObject QueryChargepros = new ActionscriptObject();
		StringReader read = new StringReader(xmlDoc);
		InputSource source = new InputSource(read);
		SAXBuilder sb = new SAXBuilder();
		try
		{
			Document doc = sb.build(source);
			Element root = doc.getRootElement();
			ConstList.config.logger.warn(root.getName());// 输出根元素的名称（测试）
			Namespace ns = root.getNamespace();

			String status = root.getChild("status", ns).getText();
			if (status != null)
				QueryChargepros.put("status", status);

			String msgType = root.getChild("msgType", ns).getText();
			if (msgType != null)
				QueryChargepros.put("msgType", msgType);

			String userIdType = root.getChild("userIdType", ns).getText();
			if (userIdType != null)
				QueryChargepros.put("userIdType", userIdType);

			String userLabel = root.getChild("userLabel", ns).getText();
			if (userLabel != null)
				QueryChargepros.put("userLabel", userLabel);

			String startSequence = root.getChild("startSequence", ns).getText();
			if (startSequence != null)
				QueryChargepros.put("startSequence", startSequence);

			String hRet = root.getChild("hRet", ns).getText();
			if (hRet != null)
				QueryChargepros.put("hRet", hRet);

			String recordCount = root.getChild("recordCount", ns).getText();
			if (recordCount != null)
				QueryChargepros.put("recordCount", recordCount);

			Element chargeList = root.getChild("chargeList", ns);

			List<Element> consumerSchemaList = chargeList.getChildren();
			Namespace sns = chargeList.getNamespace();
			QueryChargepros.put("cmd", "QueryCharge");
			ConstList.config.logger.warn(root.getChild("status", ns).getText()
					+ "," + root.getChild("msgType", ns).getText() + ","
					+ root.getChild("hRet", ns).getText() + ","
					+ root.getChild("userIdType", ns).getText() + ","
					+ root.getChild("userLabel", ns).getText() + ",");

			ActionscriptObject consumerListaObj = new ActionscriptObject();
			if (consumerSchemaList != null && consumerSchemaList.size() > 0)
			{
				for (int i = 0; i < consumerSchemaList.size(); i++)
				{
					ActionscriptObject consumeraObj = new ActionscriptObject();
					;
					Element consumerSchema = consumerSchemaList.get(i);
					String date = consumerSchema.getChild("date", sns)
							.getText();
					String consumerType = consumerSchema.getChild(
							"consumerType", sns).getText();
					String pointStr = consumerSchema.getChild("point", sns)
							.getText();
					if (date == null)
						date = "";
					if (consumerType == null)
						consumerType = "";
					if (pointStr == null)
						pointStr = "";
					consumeraObj.put("date", date);
					consumeraObj.put("consumerType", consumerType);
					consumeraObj.put("point", pointStr);
					consumerListaObj.put(i + "", consumeraObj);

				}
			}
			QueryChargepros.put("detail", consumerListaObj);

		}
		catch (JDOMException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return QueryChargepros;

	}

	public ActionscriptObject retXmlReaderQueryBalance(String xmlDoc)
	{
		ActionscriptObject QueryBalancepros = new ActionscriptObject();
		StringReader read = new StringReader(xmlDoc);
		InputSource source = new InputSource(read);
		SAXBuilder sb = new SAXBuilder();
		try
		{
			Document doc = sb.build(source);
			Element root = doc.getRootElement();
			ConstList.config.logger.warn(root.getName());// 输出根元素的名称（测试）
			Namespace ns = root.getNamespace();

			String status = root.getChild("status", ns).getText();
			if (status != null)
				QueryBalancepros.put("status", status);

			String msgType = root.getChild("msgType", ns).getText();
			if (msgType != null)
				QueryBalancepros.put("msgType", msgType);

			String userIdType = root.getChild("userIdType", ns).getText();
			if (userIdType != null)
				QueryBalancepros.put("userIdType", userIdType);

			String userLabel = root.getChild("userLabel", ns).getText();
			if (userLabel != null)
				QueryBalancepros.put("userLabel", userLabel);

			String point = root.getChild("point", ns).getText();
			if (point != null)
				QueryBalancepros.put("point", point);

			String hRet = root.getChild("hRet", ns).getText();
			if (hRet != null)
				QueryBalancepros.put("hRet", hRet);

			QueryBalancepros.put("cmd", "QueryBalance");
			ConstList.config.logger.warn(root.getChild("status", ns).getText()
					+ "," + root.getChild("msgType", ns).getText() + ","
					+ root.getChild("hRet", ns).getText() + ","
					+ root.getChild("point", ns).getText() + ","
					+ root.getChild("userIdType", ns).getText() + ","
					+ root.getChild("userLabel", ns).getText() + ",");

		}
		catch (JDOMException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return QueryBalancepros;

	}

	public ActionscriptObject retXmlReaderBuyGameTool(String xmlDoc)
	{
		ActionscriptObject BuyGameToolpros = new ActionscriptObject();
		StringReader read = new StringReader(xmlDoc);
		InputSource source = new InputSource(read);
		SAXBuilder sb = new SAXBuilder();
		try
		{
			Document doc = sb.build(source);
			Element root = doc.getRootElement();
			ConstList.config.logger.warn(root.getName());// 输出根元素的名称（测试）
			Namespace ns = root.getNamespace();

			String status = root.getChild("status", ns).getText();
			if (status != null)
				BuyGameToolpros.put("status", status);

			String msgType = root.getChild("msgType", ns).getText();
			if (msgType != null)
				BuyGameToolpros.put("msgType", msgType);

			String balance = root.getChild("balance", ns).getText();
			if (balance != null)
				BuyGameToolpros.put("balance", balance);
			else
				BuyGameToolpros.put("balance", "-1");

			String hRet = root.getChild("hRet", ns).getText();
			if (hRet != null)
				BuyGameToolpros.put("hRet", hRet);

			String point = root.getChild("point", ns).getText();
			if (point != null)
				BuyGameToolpros.put("point", point);
			else
				BuyGameToolpros.put("point", "-1");

			BuyGameToolpros.put("djid", djid);
			BuyGameToolpros.put("cmd", "BuyGameTool");
			ConstList.config.logger.warn(root.getChild("status", ns).getText()
					+ "," + root.getChild("msgType", ns).getText() + ","
					+ root.getChild("balance", ns).getText() + ","
					+ root.getChild("hRet", ns).getText() + ","
					+ root.getChild("point", ns).getText() + ","
					+ root.getChild("point", ns).getText() + ",");

		}
		catch (JDOMException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return BuyGameToolpros;

	}

	public ActionscriptObject retXmlReaderChargeUp(String xmlDoc)
	{
		ActionscriptObject chargepros = new ActionscriptObject();
		StringReader read = new StringReader(xmlDoc);
		InputSource source = new InputSource(read);
		SAXBuilder sb = new SAXBuilder();
		try
		{
			Document doc = sb.build(source);
			Element root = doc.getRootElement();
			ConstList.config.logger.warn(root.getName());// 输出根元素的名称（测试）
			Namespace ns = root.getNamespace();
			String status = root.getChild("status", ns).getText();
			if (status != null)
				chargepros.put("status", status);
			String msgType = root.getChild("msgType", ns).getText();
			if (msgType != null)
				chargepros.put("msgType", msgType);
			String balance = root.getChild("balance", ns).getText();
			if (balance != null)
				chargepros.put("balance", balance);
			String hRet = root.getChild("hRet", ns).getText();
			if (hRet != null)
				chargepros.put("hRet", hRet);

			chargepros.put("cmd", "chargeUp");

			ConstList.config.logger.warn(root.getChild("status", ns).getText()
					+ "," + root.getChild("msgType", ns).getText() + ","
					+ root.getChild("balance", ns).getText() + ","
					+ root.getChild("hRet", ns).getText() + ",");

		}
		catch (JDOMException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return chargepros;

	}

	public String transport(String url)
	{
		System.out.println("transport(String " + url + ") is called!");
		String transIDO = "";
		transIn++;

		Calendar ca = Calendar.getInstance();
		int year = ca.get(Calendar.YEAR);
		int month = ca.get(Calendar.MONTH);
		int day = ca.get(Calendar.DAY_OF_MONTH);

		int hour = ca.get(Calendar.HOUR_OF_DAY);
		int minute = ca.get(Calendar.MINUTE);
		int second = ca.get(Calendar.SECOND);
		String monthStr = "0" + (month + 1);
		ConstList.config.logger.warn("monthStr=" + monthStr);
		if (monthStr.length() > 2)
			monthStr = monthStr.substring(1);

		String dayStr = "0" + day;
		if (dayStr.length() > 2)
			dayStr = dayStr.substring(1);

		String hourStr = "0" + hour;
		if (hourStr.length() > 2)
			hourStr = hourStr.substring(1);
		ConstList.config.logger.warn("hour=" + hour + ",hourStr=" + hourStr);

		String minuteStr = "0" + minute;
		if (minuteStr.length() > 2)
			minuteStr = minuteStr.substring(1);

		String secondStr = "0" + second;
		if (secondStr.length() > 2)
			secondStr = secondStr.substring(1);

		transIDO = "" + ConstList.cpid + "" + year + monthStr + dayStr
				+ hourStr + minuteStr + secondStr + "" + transIn;
		ConstList.config.logger.warn("transIDO=" + transIDO);

		StringBuffer sb = new StringBuffer();
		String msg = "";
		if (url.indexOf("bizcontrol/ChargeUp") >= 0)
		{
			msg = ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r"
					+ "<request>\r" + "	<msgType>ChargeUpReq</msgType>\r"
					+ "	<sender>" + ConstList.SenderId + "</sender>\r"
					+ "	<userIdType>3</userIdType>\r" + "	<userLabel>" + userId
					+ "</userLabel>\r" + "	<channelId>"
					+ ConstList.DefaultChannelId
					+ "</channelId>\r"
					+ "	<cpId>701001</cpId>\r"
					+
					// "	<cpId>"+ConstList.cpid+"</cpId>\r"+
					"	<cpServiceId>" + chargeNumCode + "</cpServiceId>\r"
					+ "	<transIDO>" + transIDO + "</transIDO>\r"
					+ "	<versionId>2_0_0</versionId>\r" + "</request>\r");
			ConstList.config.logger.warn("msg=" + msg);
		}
		else if (url.indexOf("bizcontrol/BuyGameTool") >= 0)
		{
			msg = ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
					+ "<request>\r\n"
					+ "	<msgType>BuyGameToolReq</msgType>\r\n" + "	<sender>"
					+ ConstList.SenderId
					+ "</sender>\r\n"
					+ "	<userId>"
					+ userId
					+ "</userId>\r\n"
					+ "	<fid>1000</fid>\r\n"
					+ "	<cpId>"
					+ ConstList.cpid
					+ "</cpId>\r\n"
					+ "	<cpServiceId>"
					+ ConstList.gameId
					+ "</cpServiceId>\r\n"
					+ "	<consumeCode>"
					+ consumeid
					+ "</consumeCode>\r\n"
					+ "	<transIDO>"
					+ transIDO
					+ "</transIDO>\r\n"
					+ "	<versionId>2_0_0</versionId>\r\n"
					+ "	<key>" + key + "</key>\r\n" + "</request>\r");
		}
		else if (url.indexOf("bizcontrol/QueryBalance") >= 0)
			msg = ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
					+ "<request>\r\n"
					+ "	<msgType>QueryBalanceReq</msgType>\r\n" + "	<sender>"
					+ ConstList.SenderId + "</sender>\r\n"
					+ "	<userIdType>3</userIdType>\r\n" + "	<userLabel>"
					+ userId + "</userLabel>\r\n" + "	<channelId>"
					+ ConstList.ChannelId + "</channelId>\r\n" + "	<transIDO>"
					+ transIDO + "</transIDO>\r\n"
					+ "	<versionId>2_0_0</versionId>\r\n" + "</request>\r");
		else if (url.indexOf("bizcontrol/QueryChargeUpRecord") >= 0)
			msg = ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
					+ "<request>\r\n"
					+ "	<msgType>QueryChargeReq</msgType>\r\n" + "	<sender>"
					+ ConstList.SenderId
					+ "</sender>\r\n"
					+ "	<userIdType>3</userIdType>\r\n"
					+ "	<userLabel>"
					+ userId
					+ "</userLabel>\r\n"
					+ "	<channelId>"
					+ ConstList.ChannelId
					+ "</channelId>\r\n"
					+ "	<startDate>"
					+ Startdate
					+ "</startDate>\r\n"
					+ "	<endDate>"
					+ Enddate
					+ "</endDate>\r\n"
					+ "	<startSequence>1</startSequence>\r\n"
					+ "	<recordCount>20</recordCount>\r\n"
					+ "	<transIDO>"
					+ transIDO
					+ "</transIDO>\r\n"
					+ "	<versionId>2_0_0</versionId>\r\n" + "</request>\r");
		else if (url.indexOf("bizcontrol/QueryConsumeRecord") >= 0)
		{
			int type = 1;
			if (Utils.isNumeric(queryType))
				type = Integer.parseInt(queryType);

			if (type >= 1 && type <= 8)
			{

				/**
				 * <?xml version="1.0" encoding="UTF-8"?> <request>
				 * <msgType>QueryConsumeRecordReq</msgType>
				 * <queryType>1…8</queryType> <sender>101</sender>
				 * <channelId></channelId> <userIdType>1</userIdType>
				 * <userLabel>13800138000</userLabel> <queryMonth></queryMonth>
				 * <queryRange>1/2/3</queryRange> <payType>1</payType>
				 * <cpServiceId></cpServiceId> <!--此值被忽略-->
				 * <packageId></packageId> <!--此值被忽略--> <cpId></cpId>
				 * <!--此值被忽略--> </request>
				 **/

				msg = ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
						+ "<request>\r\n"
						+ "	<msgType>QueryConsumeRecordReq</msgType>\r\n"
						+ "	<queryType>"
						+ queryType
						+ "</queryType>\r\n"
						+ "	<sender>"
						+ ConstList.SenderId
						+ "</sender>\r\n"
						+ "	<channelId>"
						+ ConstList.ChannelId
						+ "</channelId>\r\n"
						+ "	<userIdType>3</userIdType>\r\n"
						+ "	<userLabel>"
						+ userId
						+ "</userLabel>\r\n"
						+ "	<queryMonth>"
						+ OneMonth
						+ "</queryMonth>\r\n"
						+ " 	<queryRange>"
						+ queryrange
						+ "</queryRange>\r\n"
						+ "	<payType>1</payType>\r\n"
						+ "	<cpServiceId></cpServiceId>\r\n"
						+ "	<packageId></packageId>\r\n" + "	<cpId></cpId>\r\n" + "</request>\r");
			}
			else if (type == 13)
			{
				msg = ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
						+ "<request>\r\n"
						+ "	<msgType>QueryConsumeRecordReq</msgType>\r\n"
						+ "<queryType>13</queryType>\r\n" + "	<sender>"
						+ ConstList.SenderId
						+ "</sender>\r\n"
						+ "	<channelId>"
						+ ConstList.ChannelId
						+ "</channelId>\r\n"
						+ "	<userIdType>3</userIdType>\r\n"
						+ "	<userLabel>"
						+ userId
						+ "</userLabel>\r\n"
						+ "	<queryMonth>"
						+ OneMonth
						+ "</queryMonth>\r\n"
						+ "    <queryRange>"
						+ queryrange
						+ "</queryRange>\r\n"
						+ "	<payType>1</payType>\r\n"
						+ "	<cpServiceId>"
						+ ConstList.gameId
						+ "</cpServiceId>\r\n"
						+ "	<packageId></packageId>\r\n"
						+ "	<cpId> "
						+ ConstList.cpid + "</cpId>\r\n" + "</request>\r");

			}
		}
		else if (url.indexOf("bizcontrol/LogoutOnlineGame") >= 0)
			msg = ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
					+ "<request>\r\n"
					+ "	<msgType>LogoutOnlineGameReq</msgType>\r\n"
					+ "	<sender>" + ConstList.SenderId + "</sender>\r\n"
					+ "	<userId>" + userId + "</userId>\r\n" + "	<channelId>"
					+ ConstList.ChannelId + "</channelId>\r\n" + "	<cpId>"
					+ ConstList.cpid + "</cpId>\r\n" + "	<cpServiceId>"
					+ ConstList.gameId + "</cpServiceId>\r\n" + "	<transIDO>"
					+ transIDO + "</transIDO>\r\n"
					+ "	<versionId>2_0_0</versionId>\r\n" + "</request>\r");
		else if (url.indexOf("UpdateSession") >= 0)
			msg = null;
		else if (url.indexOf("ActiveTest") >= 0)
			msg = "38";

		try
		{
			URL urls = new URL(url);
			HttpURLConnection uc = (HttpURLConnection) urls.openConnection();
			if (msg == null)
				uc.setRequestMethod("GET");
			else
				uc.setRequestMethod("POST");
			if (url.indexOf("ActiveTest") >= 0)
			{
				uc.setRequestProperty("content-type", "application/octstream");
			}
			else
				uc.setRequestProperty("content-type", "xml");
			uc.setRequestProperty("charset", "UTF-8");
			uc.setDoOutput(true);
			uc.setDoInput(true);
			uc.setReadTimeout(100000);
			uc.setConnectTimeout(10000);
			OutputStream os = uc.getOutputStream();
			DataOutputStream dos = new DataOutputStream(os);
			if (msg != null)
				dos.write(msg.getBytes("utf-8"));
			dos.flush();
			os.close();
			BufferedReader in = new BufferedReader(new InputStreamReader(
					uc.getInputStream(), "utf-8"));
			String readLine = "";
			while ((readLine = in.readLine()) != null)
			{
				sb.append(readLine);
			}
			in.close();
			uc.disconnect();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		ResponseStr = sb.toString();
		ConstList.config.logger.warn("msg=" + msg);
		ConstList.config.logger.warn("ResponseStr=" + ResponseStr);
		return ResponseStr;
	}

	public static void main(String[] args)
	{

	}
}
