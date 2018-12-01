package com.archy.dezhou.netty;

/**
 *@author archy_yu 
 **/

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.Logger;

import sun.misc.BASE64Decoder;

import com.archy.dezhou.netty.base.IHttpServer;
import com.archy.dezhou.util.XLoad;


public class HttpServerKit
{
	private static Logger log = Logger.getLogger(HttpServerKit.class);
	
	private static List<IHttpServer> httpServerList = new ArrayList<IHttpServer>();
	
	public static void initConfig()
	{
		try
		{

            String content = new String(XLoad.getResource("server.json"));

            JSONArray list = JSONArray.parseArray(content);

            for(int i=0;i<list.size();i++){

                JSONObject subItem = list.getJSONObject(i);

                String host = subItem.getString("host");
                int port = Integer.parseInt( subItem.getString("port") );
                int cnt = Integer.parseInt( subItem.getString("threadCnt") );
                HttpServer server = new HttpServer("", port, cnt);
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
