package com.yl.netty;

/**
 *@author archy_yu 
 **/

import com.yl.netty.base.IHttpServer;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class HttpServer implements IHttpServer
{
	private int port = 0;
	
	private String host = null;
	
	
	private EventLoopGroup bossGroup = null;
	
	private EventLoopGroup workGroup = null;
	
	public HttpServer(String host, int port,int threadCnt)
	{
		this.host = host;
		this.port = port;
		bossGroup = new NioEventLoopGroup(threadCnt);
        workGroup = new NioEventLoopGroup(threadCnt);
	}
	
	@Override
	public void start()
	{
        try
        {
        	ServerBootstrap boot = new ServerBootstrap();
			boot.option( ChannelOption.SO_BACKLOG, 4096 );
			boot.option( ChannelOption.SO_REUSEADDR, true);
			boot.option( ChannelOption.TCP_NODELAY, true);
			boot.group( bossGroup, workGroup )
				.channel( NioServerSocketChannel.class )
				.childHandler( new NettyHttpInitializer() );
			
			boot.bind(this.host,this.port).sync();
        }
        catch (Exception e)
        {
        	e.printStackTrace();
        }
	}
	
	@Override
	public void stop()
	{
		bossGroup.shutdownGracefully();
		workGroup.shutdownGracefully();
	}
	
}
