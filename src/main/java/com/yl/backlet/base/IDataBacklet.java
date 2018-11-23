package com.yl.backlet.base;

/**
 * @author archy_yu
 * */

import java.util.Map;
import io.netty.handler.codec.http.FullHttpResponse;

public interface IDataBacklet
{
	public final static String HANDLENAME = "httphander";
	
	public byte[] process(String subCmd,Map<String,String> parms,FullHttpResponse httpResponse);
	
	public void access(String subCmd,Map<String,String> parms,FullHttpResponse httpResponse);
	
}
