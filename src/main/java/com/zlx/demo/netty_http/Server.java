package com.zlx.demo.netty_http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * Hello world!
 *
 */
public class Server {
	public static void main(String[] args) throws InterruptedException {
		// （1.1）创建主从Reactor线程池
		EventLoopGroup bossGroup = new NioEventLoopGroup(1);
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			// 1.2创建启动类ServerBootstrap实例，用来设置客户端相关参数
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup)// 1.2.1设置主从线程池组
					.channel(NioServerSocketChannel.class)// 1.2.2指定用于创建客户端NIO通道的Class对象
					.option(ChannelOption.SO_BACKLOG, 100)// 1.2.3设置客户端套接字参数
					.handler(new LoggingHandler(LogLevel.INFO))// 1.2.4设置日志handler
					.childHandler(new ChannelInitializer<SocketChannel>() {// 1.2.5设置用户自定义handler
						@Override
						public void initChannel(SocketChannel ch) throws Exception {
							ChannelPipeline p = ch.pipeline();

							// 1.2.1 配置http请求解码器
							p.addLast(new HttpRequestDecoder());
							// 1.2.2 设置请求合并handler
							p.addLast(new HttpObjectAggregator(65536));
							// 1.2.3设置http响应编码器
							p.addLast(new HttpResponseEncoder());
							// 1.2.4设置自定义handler
							p.addLast(new NettyServerHttpHandler());
						}
					});

			// 1.3 启动服务器
			ChannelFuture f = b.bind(7002).sync();
			System.out.println("----Server Started----");

			// 1.4 同步等待服务socket关闭
			f.channel().closeFuture().sync();
		} finally {
			// 1.5优雅关闭线程池组
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
}
