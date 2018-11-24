package com.yl.netty;

/**
 *@author archy_yu 
 **/

import java.util.Map;

import org.apache.log4j.Logger;

import com.yl.backlet.BackletKit;
import com.yl.backlet.base.IDataBacklet;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;


public class NettyHttpHandler extends SimpleChannelInboundHandler<Object>
{
	
	private static Logger log = Logger.getLogger(NettyHttpHandler.class);

	
	public HttpResponse handle(Object obj)
	{
		HttpRequest httpRequest = (HttpRequest)obj;
		HttpContent httpContent = (HttpContent)obj;
		
		String req = httpRequest.getUri().split("\\?")[1];
		String content = httpContent.content().toString(CharsetUtil.UTF_8);
		
		String cmd = HttpServerKit.base64Decoder(req);
		Map<String,String> params = HttpServerKit.getParamsFromString(content);
		String cmds[] = cmd.split("\\|");
		
		IDataBacklet backlet = BackletKit.getBacklet(cmds[0]);
		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
		
		if(backlet != null)
		{
			response.headers().set("Content-Type","text/xml; charset=utf-8");
			response.headers().set("err", "no");
			response.headers().set("zip","no");
			response.headers().set("tsh",System.currentTimeMillis() + "");
			
			try
			{
				long beginTime = System.currentTimeMillis();
				backlet.access(cmds[1], params,response);
				long endTime = System.currentTimeMillis();
				if((int)(endTime - beginTime) > 5)
				{
					log.error( "cmd[0] and cmd[1] :" + cmds[0] + " " + cmds[1] + " kill " + (endTime - beginTime) + "ms"  );
				}
			}
			catch (Exception t)
			{
				log.error("cmd[0] and cmd[1] :" + cmds[0] + " " + cmds[1] , t);
			}
		}
		else
		{
			log.error("error, no this cmd : " + cmd);
		}
		
		return response;
	}
	
	@Override
    public void channelReadComplete(ChannelHandlerContext ctx)
	{
        ctx.flush();
	}
	
	@Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) 
	{
        cause.printStackTrace();
        ctx.close();
    }

	@Override
	protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object object) throws Exception {
		HttpResponse response = handle(object);
		channelHandlerContext.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
	}
}
