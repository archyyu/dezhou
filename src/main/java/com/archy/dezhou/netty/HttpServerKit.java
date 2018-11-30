package com.archy.dezhou.netty;

/**
 *@author archy_yu 
 **/

import java.io.StringReader;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import sun.misc.BASE64Decoder;

import com.archy.dezhou.Global.ConstList;
import com.archy.dezhou.netty.base.IHttpServer;
import com.archy.dezhou.xload.XLoad;


public class HttpServerKit
{
	private static Logger log = Logger.getLogger(HttpServerKit.class);
	
	private static List<IHttpServer> httpServerList = new ArrayList<IHttpServer>();
	
	public static void initConfig(String folder)
	{
		try
		{
			byte[] bytes = XLoad.getResource(ConstList.httpConfigFileName);
			SAXReader reader = new SAXReader(); 
        	StringReader read = new StringReader(new String(bytes));
        	Document document = reader.read(read);
        	Element root = document.getRootElement();
        	
        	List<Element> list = root.elements();
        	for(Element subItem : list)
        	{
        		String host = subItem.element("host").getText();
        		int port = Integer.parseInt( subItem.element("port").getText() );
        		int cnt = Integer.parseInt( subItem.element("threadCnt").getText() );
        		HttpServer server = new HttpServer(host, port, cnt);
        		httpServerList.add(server);
        	}
			
		}
		catch (Exception e)
		{
			log.error("init http server error", e);
		}
	}
	
	public static void lanuchAllHttpServer()
	{
		for(IHttpServer server : httpServerList)
		{
			server.start();
		}
	}
	
	public static void shutdownAllHttpServer()
	{
		for(IHttpServer server : httpServerList)
		{
			server.stop();
		}
	}
	
	public static String base64Decoder(String source)
	{
		try
		{
			BASE64Decoder decode = new BASE64Decoder();
		 	byte[] TmpByte = decode.decodeBuffer(source);
		 	return new String(TmpByte);
		}
		catch (Exception e)
		{
			log.error("base 64 decode error", e);
		}
		return null;
	}
	
	public static Map<String,String> getParamsFromString(String source)
	{
		source = source.replace("%a=1", "a=1");
		String[] strs = source.split("&");
		Map<String,String> params = new HashMap<String,String>();
		
		for(String str : strs)
		{
			String[] strTs = str.split("=");
			if(strTs.length == 2)
			{
				params.put(strTs[0], strTs[1]);
			}
		}
		
		return params;
		
	}
	
}
