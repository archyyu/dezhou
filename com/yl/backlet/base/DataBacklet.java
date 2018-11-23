package com.yl.backlet.base;

import io.netty.handler.codec.http.FullHttpResponse;
import java.util.Map;
import org.apache.log4j.Logger;
import com.yl.backlet.BackletKit;
import com.yl.util.ComPressUtil;

public abstract class DataBacklet implements IDataBacklet
{
	
	protected Logger log = Logger.getLogger(getClass());
	
	@Override
	public void access( String subCmd, Map<String, String> parms, FullHttpResponse httpResponse )
	{
		byte[] xmlByteA = this.process(subCmd,parms,httpResponse);
		
		if(xmlByteA == null)
		{
			log.error("xmlByteA is null, request parms : " + parms);
			xmlByteA = BackletKit.infoXml("InnerServerError").getBytes();
		}
		
//		System.err.println(new String(xmlByteA));
		
		if(xmlByteA.length > 300)
		{
			try
			{
				xmlByteA = ComPressUtil.Zip(xmlByteA);
				httpResponse.headers().set("zip","yes");
			}
			catch (Exception e)
			{
				log.error("zip error",e);
			}
		}
		
		httpResponse.headers().set("Content-Length", xmlByteA.length+"");
		httpResponse.content().writeBytes(xmlByteA);
	}
}
