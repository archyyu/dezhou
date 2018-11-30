package com.archy.dezhou.backlet.base;

/**
 * @author archy_yu
 * */

import java.util.Map;
import io.netty.handler.codec.http.FullHttpResponse;

public interface IDataBacklet
{
	String HANDLENAME = "httphander";
	
	byte[] process(String subCmd, Map<String, String> parms, FullHttpResponse httpResponse);
	
	void access(String subCmd, Map<String, String> parms, FullHttpResponse httpResponse);
	
}
