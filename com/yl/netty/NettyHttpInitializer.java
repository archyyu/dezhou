package com.yl.netty;

/**
 *@author archy_yu 
 **/

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;

public class NettyHttpInitializer extends ChannelInitializer<SocketChannel>
{

	@Override
	protected void initChannel(SocketChannel ch) throws Exception
	{
		ChannelPipeline p = ch.pipeline();
        p.addLast( "http-decoder", new HttpRequestDecoder() );
        p.addLast( "http-aggregator", new HttpObjectAggregator( 2*1024 ) );
        p.addLast( "http-encoder", new HttpResponseEncoder() );
        p.addLast( "http-chunked", new ChunkedWriteHandler() );
        p.addLast(new NettyHttpHandler());
	}

}
